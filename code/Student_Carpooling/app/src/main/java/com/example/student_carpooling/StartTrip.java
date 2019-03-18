package com.example.student_carpooling;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StartTrip extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    private Intent intent;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
    private Boolean LocationPermissionsGranted = false;
    private int PickedUp;
    private String TripID, Username, CurrentUserID, DriverUserName,PicUrl;
    private ArrayList<Route> RouteArrayList = new ArrayList<>();
    private float PLat, PLon, DLon, DLat;
    private Marker driverMarker;
    private boolean arrived = false;
    private int count = 0;
    private LatLng DriverPosition, Loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null){
            CurrentUserID = firebaseUser.getUid();
        }

        intent = getIntent();
        TripID = intent.getStringExtra("TripID");
        DriverUserName = intent.getStringExtra("Username");
        PicUrl = intent.getStringExtra("driverUrl");

        getLocationPermission();

        Button Back = findViewById(R.id.back);
        Button End = findViewById(R.id.end);
        ImageView refresh = findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove polylines are reset the markers
                if (mMap != null) {
                    refreshMap();
                    addPassengers();
                    getDriverLocation();
                    getDestination();
                }
            }
        });

        //pop up dialog for end
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        End.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set completed to true,
                //how to know if the driver is near their said destination?
                Location Dst = new Location("");
                Dst.setLatitude(Loc.latitude);
                Dst.setLongitude(Loc.longitude);

                Location Driver = new Location("");
                Driver.setLatitude(DriverPosition.latitude);
                Driver.setLongitude(DriverPosition.longitude);

                float distance = Driver.distanceTo(Dst);

                if(distance<500){
                    //Set completed to true
                    //Toast.makeText(StartTrip.this, ""+CurrentUserID, Toast.LENGTH_SHORT).show();
                    DatabaseReference completed = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUserID).child(TripID);
                    completed.child("Completed").setValue(1);


                    //arrived at destination -- they can complete the trip && prompt the rate users
                    Intent intent = new Intent(StartTrip.this,TripComplete.class);
                    intent.putExtra("TripID",TripID);
                    intent.putExtra("DriverID",CurrentUserID);
                    intent.putExtra("DriverUsername",DriverUserName);
                    intent.putExtra("driverUrl",PicUrl);
                    intent.putExtra("Cancelled","0");

                    final DatabaseReference completedCount = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserID);
                    completedCount.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String completedTrips = (String) dataSnapshot.child("CompletedTrips").getValue();
                            if (completedTrips != null) {
                                count = Integer.valueOf(completedTrips);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    completedCount.child("CompletedTrips").setValue(count+1);
                    startActivity(intent);

                    finish();
                }
                else{
                    //AlertDialog -- you have no yet reached your destination, are you sure you want to end this trip??
                   showDialog();
                }
            }
        });

    }

    private void showDialog() {
       final AlertDialog dialogBuilder = new AlertDialog.Builder(StartTrip.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog, null);
                TextView Text = dialogView.findViewById(R.id.Text);
                Text.setText("You don't seem to have reached your destination yet. Are you sure you wish to end this trip? ");
                Button Submit = dialogView.findViewById(R.id.Submit);


                Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference completed = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUserID).child(TripID);
                        completed.child("Completed").setValue(1);
                        Intent intent = new Intent(StartTrip.this,TripComplete.class);
                        intent.putExtra("TripID",TripID);
                        intent.putExtra("DriverID",CurrentUserID);
                        intent.putExtra("DriverUsername",DriverUserName);
                        intent.putExtra("driverUrl",PicUrl);
                        intent.putExtra("Cancelled","0");

                        final DatabaseReference completedCount = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserID);
                        completedCount.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String completedTrips = (String) dataSnapshot.child("CompletedTrips").getValue();
                                if (completedTrips != null) {
                                    count = Integer.valueOf(completedTrips);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        completedCount.child("CompletedTrips").setValue(count+1);
                        startActivity(intent);
                        finish();
                        dialogBuilder.dismiss();
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //attach the polyline listener to the map
        mMap.setOnPolylineClickListener(this);

        if (LocationPermissionsGranted) {

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            addPassengers();
            getDestination();

            //get the new driver location every second
            handler.postDelayed(runnable, 1000);
            Passengerhandler.postDelayed(Passengerrunnable, 600000);


        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnInfoWindowClickListener(this);
    }

    //get the updated driver location every second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getDriverLocation();
            handler.postDelayed(this, 1000);
        }
    };


    //add this handler to get the passengers pick up locations  every 5 mins otherwise the passenger markers wont be refreshed unless the driver clicks the refresh button
    //old passenger markers from passenger who have been collected will be left on the map otherwise
    private Handler Passengerhandler = new Handler();
    private Runnable Passengerrunnable = new Runnable() {
        @Override
        public void run() {
            addPassengers();
            Passengerhandler.postDelayed(this, 600000);
        }
    };


    private void getDriverLocation() {


        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (LocationPermissionsGranted) {

                final Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(this,new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation == null) throw new AssertionError();
                            DriverPosition = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());




                            //gives the effect of the marker moving, rather than having duplicates created
                            if(driverMarker != null){
                                driverMarker.remove();
                            }
                            driverMarker = mMap.addMarker(new MarkerOptions().position(DriverPosition).title("Driver"));


                            //update the driver location in the database for passsenger
                            DatabaseReference updateLoc = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUserID).child(TripID);
                            GeoFire geoFire = new GeoFire(updateLoc);
                            geoFire.setLocation("DriverLocation", new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()));

                            //create map bounds so all markers will be shown on screen
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(Loc);
                            builder.include(DriverPosition);
                            LatLngBounds bounds = builder.build();

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,35);
                            mMap.animateCamera(cameraUpdate);


                            Location Dst = new Location("");
                            Dst.setLatitude(Loc.latitude);
                            Dst.setLongitude(Loc.longitude);

                            Location Driver = new Location("");
                            Driver.setLatitude(DriverPosition.latitude);
                            Driver.setLongitude(DriverPosition.longitude);

                            //get distance between the driver and destination points
                            // distanceTo will return the distance in kilometres
                            float distance = Driver.distanceTo(Dst);

                            //so if the distance is less than 100 km and arrived hasnt been set to true
                            if(distance<100 && !arrived){
                                //send notification and display toast message
                                Toast.makeText(StartTrip.this, "You have arrived at your Destination", Toast.LENGTH_SHORT).show();
                                arrived = true;


                                DatabaseReference completed = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUserID).child(TripID);
                                completed.child("Completed").setValue(1);


                                //start the rating system activity
                                Intent intent = new Intent(StartTrip.this, TripComplete.class);
                                intent.putExtra("TripID",TripID);
                                intent.putExtra("DriverID",CurrentUserID);
                                intent.putExtra("DriverUsername",DriverUserName);
                                intent.putExtra("driverUrl",PicUrl);
                                intent.putExtra("Cancelled","0");

                                //update the number of trips that driver has completed by 1
                                final DatabaseReference completedCount = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserID);
                                completedCount.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String completedTrips =  Objects.requireNonNull(dataSnapshot.child("CompletedTrips").getValue()).toString();
                                        count = Integer.parseInt(completedTrips);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                completedCount.child("CompletedTrips").setValue(count+1);
                                startActivity(intent);
                                finish();


                            }



                        } else {
                            Toast.makeText(StartTrip.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    private void addPassengers(){
            refreshMap();
            DatabaseReference Passengers = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUserID).child(TripID).child("Passengers");
            Passengers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot id : dataSnapshot.getChildren()) {
                            final String passengerKey = id.getKey();
                            if (passengerKey != null) {
                                DatabaseReference PassengerInfo = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUserID).child(TripID).child("Passengers").child(passengerKey);
                                //get Key...
                                PassengerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                                        Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                                        if (map != null) {
                                            if (map.get("lat") != null) {
                                                String latStr = Objects.requireNonNull(map.get("lat")).toString();
                                                PLat = (Float.parseFloat(latStr));
                                            }
                                            if (map.get("lon") != null) {
                                                String latStr = Objects.requireNonNull(map.get("lon")).toString();
                                                PLon = (Float.parseFloat(latStr));

                                            }
                                            if (map.get("Username") != null) {
                                                Username = (String) map.get("Username");

                                            }
                                            if (map.get("PickedUp") != null) {
                                                String PickedUpStr = Objects.requireNonNull(map.get("PickedUp")).toString();
                                                PickedUp = (Integer.parseInt(PickedUpStr));
                                            }

                                            //only show the passenger marker when they havent been picked up
                                            //within the passenger map activity, once the driver has reached their location, picked up will be set to 1

                                            if (PickedUp == 0) {
                                                LatLng passengerLoc = new LatLng(PLat, PLon);
                                                MarkerOptions options = new MarkerOptions().position(passengerLoc).title(Username).snippet("Calculate route to " + Username + "'s pick up location?").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                                mMap.addMarker(options);

                                            }



                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    private void getDestination(){
        DLat = intent.getFloatExtra("DLat", DLat);
        DLon = intent.getFloatExtra("DLon", DLon);
        Loc = new LatLng(DLat, DLon);
        MarkerOptions options = new MarkerOptions().position(Loc).title("Your Destination").snippet("Calculate the route to your destination?");
        mMap.addMarker(options);

    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                LocationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            LocationPermissionsGranted = false;
                            return;
                        }
                    }
                    LocationPermissionsGranted = true;
                    //initialize our map
                    //initMap();
                }
            }
        }
    }

    private void DrawRoute(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //clear the arraylist, for when it is clicked multiple times -- no dups
                if(RouteArrayList.size() > 0 ){

                    //this for loop removes all the other routes
                    //so when a new route is created, all previous routes will be removed
                    for(Route route : RouteArrayList){
                        route.getPolyline().remove();
                    }
                    RouteArrayList.clear();
                    RouteArrayList = new ArrayList<>();
                }


                for(DirectionsRoute route: result.routes){
                    List<com.google.maps.model.LatLng> Path = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newPath = new ArrayList<>();

                    //then loop thro the coordinates for each individual polyline
                    for(com.google.maps.model.LatLng latLng: Path){
                        //add each to the array list 'new path '
                        newPath.add(new LatLng(latLng.lat,latLng.lng));
                    }
                    //create new polyineoption object, '.addAll -> adds all the coordinates from new path array list to the end of the  new polyline ,so that polyline is added to the map
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newPath).color(Color.GRAY).width(10));
                    //make it clickable show that the info about distance/duration will show
                    polyline.setClickable(true);
                    //keep track of each polyline and its duration/distance information -- so add a to our list of 'route' objects
                    RouteArrayList.add(new Route(polyline, route.legs[0]));
                }
            }
        });
    }


    @Override
    public void onInfoWindowClick(final Marker marker) {

        if(marker.getTitle().equals("Your current Location")){
            marker.hideInfoWindow();
            //dont want to determine route to self
        }else{
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(marker.getSnippet()).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int which) {
                    getRoute(marker);
                    dialog.dismiss();
                }
            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int which) {
                            dialog.cancel();

                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }


    }
    private void getRoute(Marker marker){
        //need a way to remove old polylines-- each has its own id, add it to a list to keep track

        String apiKey = getResources().getString(R.string.google_maps_routes);
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
        DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(context);


        //turn to false for only one route--best -- in this case show all
        directionsApiRequest.alternatives(true);
        //the driver is the origin
        directionsApiRequest.origin(new com.google.maps.model.LatLng(DriverPosition.latitude,DriverPosition.longitude));
        directionsApiRequest.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                DrawRoute(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(StartTrip.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        for(Route route: RouteArrayList){
            //only want the clicked polyline is be orange
            if(polyline.getId().equals(route.getPolyline().getId())){
                //change the color when clicks and show the duration time
                polyline.setColor(ContextCompat.getColor(this,R.color.quantum_orange));
                //show the directions
                //can we add to the

                //z index refers to where the polyline will be placed on the map

                polyline.setZIndex(1);

                Toast.makeText(this, ""+route.getDirectionsLeg().distance +"\n"+ route.getDirectionsLeg().duration, Toast.LENGTH_LONG).show();
            }
            else{
                //change the color when clicks and show the duration time
                route.getPolyline().setColor(ContextCompat.getColor(this,R.color.quantum_grey));
                //need to set the z index, otherwise part of the polyline wont be shown if its apart of another polyline
                polyline.setZIndex(0);
            }
        }
    }


    private void refreshMap(){

        if(mMap != null){
            mMap.clear();

            if(RouteArrayList.size() >0){
                RouteArrayList.clear();
                RouteArrayList = new ArrayList<>();
            }
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
    }



}


