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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StartTrip extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    private static final String TAG = "StartTrip";


    private Intent intent;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean LocationPermissionsGranted = false;

    private String TripID, Username, CurrentUserID, DriverUserName, NotificationKey,PickedUp,PicUrl;
    private FirebaseAuth mAuth;

    private FusedLocationProviderClient fusedLocationProviderClient, fusedLocationProviderClient1;

    private ArrayList<Route> RouteArrayList = new ArrayList<>();

    private float PLat, PLon, DLon, DLat;

    private ImageView refresh;

    GoogleApiClient googleApiClient;
    Location lastLocation;

    Marker driverMarker;

    boolean arrived = false;

    int count = 0;

    GeoApiContext mGeoApiContext = null;

    private LatLng DriverPosition, Loc;

    LocationRequest locationRequest;

    MarkerOptions driver;


    private Button Back, End;
    //when press end-> add a value to the trip info : Completed: True
    //if Completed is true prompt users to rate eachother


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        fusedLocationProviderClient1 = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        intent = getIntent();
        TripID = intent.getStringExtra("TripID");
        DriverUserName = intent.getStringExtra("Username");
        PicUrl = intent.getStringExtra("driverUrl");

        getLocationPermission();

        Back = findViewById(R.id.back);
        End = findViewById(R.id.end);
        refresh = findViewById(R.id.refresh);

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
                            count = Integer.valueOf(dataSnapshot.child("CompletedTrips").getValue().toString());
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(StartTrip.this);
                    builder.setTitle("Are you sure you want to end this trip?").setMessage("You dont seem to have reached your destination yet, are you sure you wish to continue?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int which) {
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
                                    count = Integer.valueOf(dataSnapshot.child("CompletedTrips").getValue().toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            completedCount.child("CompletedTrips").setValue(count+1);
                            startActivity(intent);
                            finish();
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

                //finish the activity

            }
        });

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnPolylineClickListener(this);

        if (LocationPermissionsGranted) {

            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


            addPassengers();
            getDestination();



            handler.postDelayed(runnable, 1000);
            //get the new location every second


            //every 20 mins could rerun the passenger, as the markers wont remove unless the driver manually refreshed hte map


        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnInfoWindowClickListener(this);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getDriverLocation();
            handler.postDelayed(this, 1000);
        }
    };

    private void getDriverLocation() {


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (LocationPermissionsGranted) {

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation == null) throw new AssertionError();
                            DriverPosition = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            //driver = new MarkerOptions().position(DriverPosition).title(Username);
                            //mMap.addMarker(driver);

                            if(driverMarker != null){
                                driverMarker.remove();
                            }
                            driverMarker = mMap.addMarker(new MarkerOptions().position(DriverPosition).title("Driver"));
                            DatabaseReference updateLoc = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUserID).child(TripID);
                            GeoFire geoFire = new GeoFire(updateLoc);
                            geoFire.setLocation("DriverLocation", new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()));



                            Location Dst = new Location("");
                            Dst.setLatitude(Loc.latitude);
                            Dst.setLongitude(Loc.longitude);

                            Location Driver = new Location("");
                            Driver.setLatitude(DriverPosition.latitude);
                            Driver.setLongitude(DriverPosition.longitude);

                            float distance = Driver.distanceTo(Dst);

                            if(distance<100 && !arrived){
                                //send notification and display toast message
                                Toast.makeText(StartTrip.this, "You have arrived at your Destination", Toast.LENGTH_SHORT).show();
                                // new SendNotification(_driverUsername + " is at your pickup Location", "Student Carpooling", _notificationKey);
                                arrived = true;

                                DatabaseReference completed = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUserID).child(TripID);
                                completed.child("Completed").setValue(1);

                                Intent intent = new Intent(StartTrip.this, TripComplete.class);
                                intent.putExtra("TripID",TripID);
                                intent.putExtra("DriverID",CurrentUserID);
                                intent.putExtra("DriverUsername",DriverUserName);
                                intent.putExtra("driverUrl",PicUrl);
                                intent.putExtra("Cancelled","0");
                                final DatabaseReference completedCount = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserID);
                                completedCount.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        count = Integer.valueOf(dataSnapshot.child("CompletedTrips").getValue().toString());
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
            DatabaseReference Passengers = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUserID).child(TripID).child("Passengers");
            Passengers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot id : dataSnapshot.getChildren()) {
                            final String passengerKey = id.getKey();
                            DatabaseReference PassengerInfo = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUserID).child(TripID).child("Passengers").child(passengerKey);
                            //get Key...
                            PassengerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                    if (map.get("lat") != null) {
                                        PLat = Float.valueOf(map.get("lat").toString());

                                    }
                                    if (map.get("lon") != null) {
                                        PLon = Float.valueOf(map.get("lon").toString());

                                    }
                                    if (map.get("Username") != null) {
                                        Username = map.get("Username").toString();

                                    }
                                    if (map.get("NotificationKey") != null) {
                                        NotificationKey = map.get("NotificationKey").toString();

                                    }
                                    if (map.get("PickedUp") != null) {
                                        PickedUp = map.get("PickedUp").toString();

                                    }

                                      if(Integer.parseInt(PickedUp) == 0){
                                          LatLng passengerLoc = new LatLng(PLat, PLon);
                                          MarkerOptions options = new MarkerOptions().position(passengerLoc).title(Username).snippet("Calculate route to " + Username + "'s pick up location?").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                          mMap.addMarker(options);

                                      }
                                    //could update the databse under passenger--> PickedUp = 1 and driver can view this, and if picked up, remove pick --> show snackbar?

                                    //would this create duplicates?? test it out


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


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
        Toast.makeText(this, ""+DLat, Toast.LENGTH_SHORT).show();
        Loc = new LatLng(DLat, DLon);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Loc.latitude, Loc.longitude)).tilt(70)
                .zoom(15)
                .build();
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        MarkerOptions options = new MarkerOptions().position(Loc).title("Your Destination");
        mMap.addMarker(options);
    }

    private void moveCamera(LatLng latLng) { ;
        MarkerOptions options = new MarkerOptions().position(latLng).title("Your current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        //icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_icon2));
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
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
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
                Log.d(TAG, "run: result routes: " + result.routes.length);
                //clear the arraylist, for when it is clicked multiple times -- no dups
                //Toast.makeText(PassengerLocation.this, "test:"+polylineArrayList.size(), Toast.LENGTH_SHORT).show();
                if(RouteArrayList.size() > 0 ){
                    for(Route route: RouteArrayList){
                        //Toast.makeText(PassengerLocation.this, "test:"+id, Toast.LENGTH_SHORT).show()
                        route.getPolyline().remove();
                    }
                    RouteArrayList.clear();
                    RouteArrayList = new ArrayList<>();
                }


                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg" + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newPath = new ArrayList<>();

                    for(com.google.maps.model.LatLng latLng: decodedPath){
                        newPath.add(new LatLng(latLng.lat,latLng.lng));
                    }

                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newPath).color(Color.GRAY).width(10));
                    polyline.setClickable(true);
                    //add the polyline here
                    RouteArrayList.add(new Route(polyline, route.legs[0]));
                    //keep track of routes.legs[0] too.., diction - key being the polyline id?
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
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCGNnh1lFiJ4mGCAdzJa50I77c2ITyGhnY")
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
                Log.d(TAG, "distance: Fail"+ e.getMessage());

            }
        });

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        for(Route route: RouteArrayList){
            if(polyline.getId().equals(route.getPolyline().getId())){
                Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
                //change the color when clicks and show the duration time
                polyline.setColor(ContextCompat.getColor(this,R.color.quantum_orange));
                polyline.setZIndex(1);
                //show the directions
                //can we add to the

                Toast.makeText(this, ""+route.getDirectionsLeg().distance +"\n"+ route.getDirectionsLeg().duration, Toast.LENGTH_LONG).show();

                //show a button on screen, showing the list of written directions?
            }
            else{
                //change the color when clicks and show the duration time
                route.getPolyline().setColor(ContextCompat.getColor(this,R.color.quantum_grey));
                route.getPolyline().setZIndex(0);
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


