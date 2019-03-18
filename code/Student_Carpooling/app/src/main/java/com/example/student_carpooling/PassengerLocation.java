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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
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

public class PassengerLocation extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    private String UserID;
    private Float Lat,Lon,DstLat,DstLon;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean LocationPermissionsGranted = false;
    private LatLng passengerLoc,DriverPosition,DestLoc;
    private ArrayList<Route> RouteArrayList = new ArrayList<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //attach the polyline listener to the map
        mMap.setOnPolylineClickListener(this);

        //make sure the driver has agreed to accessing device location
        if (LocationPermissionsGranted) {
            Intent intent = getIntent();
            String Username = intent.getStringExtra("Username");
            String TripID = intent.getStringExtra("TripID");
            String PassID = intent.getStringExtra("ID");

            FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            if(CurrentUser != null){
               UserID = CurrentUser.getUid();
            }

            //get the passenger's location
            getPassengerLocation(Username,TripID, PassID);
            getCurrentUserLocation();
            //get destination coordinates
            getDestination(TripID);

            //attach info window listener for map markers
            mMap.setOnInfoWindowClickListener(this);

            //permission check
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location2);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(PassengerLocation.this);
        }

        //get permissions from user
        getLocationPermission();

        Button Back = findViewById(R.id.back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getPassengerLocation(final String Username,String TripID, String PassID){

        DatabaseReference Passenger = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID).child("Passengers").child(PassID);
        Passenger.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                if (map != null) {
                    if (map.get("lat") != null) {
                        String latStr = Objects.requireNonNull(map.get("lat")).toString();
                        Lat = (Float.parseFloat(latStr));

                    }
                    if (map.get("lon") != null) {
                        String lonStr = Objects.requireNonNull(map.get("lon")).toString();
                        Lon = (Float.parseFloat(lonStr));

                    }

                    //create new marker

                    String title = Username + "'s pick up location";
                    passengerLoc = new LatLng(Lat, Lon);
                    MarkerOptions options = new MarkerOptions().position(passengerLoc).title(title).snippet("Calculate route to " + Username + "?").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    mMap.addMarker(options);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getDestination(String TripID){
        DatabaseReference Destination = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
        Destination.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                };
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                if (map != null) {
                    if (map.get("DstLat") != null) {
                        String latStr = Objects.requireNonNull(map.get("DstLat")).toString();
                        DstLat = (Float.parseFloat(latStr));

                    }
                    if (map.get("DstLon") != null) {
                        String lonStr = Objects.requireNonNull(map.get("DstLon")).toString();
                        DstLon = (Float.parseFloat(lonStr));

                    }

                    DestLoc = new LatLng(DstLat,DstLon);
                    MarkerOptions options2 = new MarkerOptions().position(DestLoc).title("Your Destination").snippet("Calculate route to your destination?");
                    mMap.addMarker(options2);
                }



            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void getCurrentUserLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (LocationPermissionsGranted) {

                //get device location
                final Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(this,new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();

                            if (currentLocation != null) {
                                DriverPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            }

                            //create map bounds so all markers will be shown on screen
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(DestLoc);
                            builder.include(passengerLoc);
                            builder.include(DriverPosition);
                            LatLngBounds bounds = builder.build();

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,35);
                            mMap.animateCamera(cameraUpdate);

                        } else {
                            Toast.makeText(PassengerLocation.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

    private void getRoute(Marker marker){


        //
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
                Toast.makeText(PassengerLocation.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            LocationPermissionsGranted = false;
                            return;
                        }
                    }
                    LocationPermissionsGranted = true;
                }
            }
        }
    }

    private void DrawRoute(final DirectionsResult result){
        //using a handler to pass this to the main thread as because the google map is on the main thread and this function is called inside another
        //to add this polylines to the map need to do this.
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
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
                    //get the encoded path ( get all the points along each rote)in order to build the polyline

                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newPath = new ArrayList<>();

                    for(com.google.maps.model.LatLng latLng: decodedPath){
                        newPath.add(new LatLng(latLng.lat,latLng.lng));
                    }

                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newPath).color(Color.GRAY).width(10));

                    //make it clickable show that the info about distance/duration will show
                    polyline.setClickable(true);

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

    @Override
    public void onPolylineClick(Polyline polyline) {
        for(Route route: RouteArrayList){
            if(polyline.getId().equals(route.getPolyline().getId())){
                //change the color when clicks and show the duration time
                polyline.setColor(ContextCompat.getColor(this,R.color.quantum_orange));
                //show the directions
                //can we add to the
                Toast.makeText(this, ""+route.getDirectionsLeg().distance +"\n"+ route.getDirectionsLeg().duration, Toast.LENGTH_LONG).show();
            }
            else{
                //change the color when clicks and show the duration time
                route.getPolyline().setColor(ContextCompat.getColor(this,R.color.quantum_grey));
            }
        }

    }
}

