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
import android.util.Log;
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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

public class PassengerLocation extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    private static final String TAG = "PassengerLocation";



    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean LocationPermissionsGranted = false;

    private String PickUp, PickUpID, DriverID, TripID, CurrentUserID, driverNotificationKey;

    private LatLng latLng, passengerLoc,DriverPosition,DestLoc;

    GeoApiContext mGeoApiContext =null;

    private Button Back;

    Intent intent;
    private FirebaseAuth mAuth;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private ArrayList<Route> RouteArrayList = new ArrayList<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.setOnPolylineClickListener(this);


        if (LocationPermissionsGranted) {

            intent = getIntent();
            String PassengerID = intent.getStringExtra("ID");
            String Username = intent.getStringExtra("Username");
            float Lat = (float) intent.getFloatExtra("Lat",0);
            float Lon = (float) intent.getFloatExtra("Lon",0);
            float DstLon = (float) intent.getFloatExtra("DLon",0);
            float DstLat = (float) intent.getFloatExtra("DLat",0);

            getCurrentUserLocation();

            String title = Username + "'s pick up location";
            passengerLoc = new LatLng(Lat,Lon);
            MarkerOptions options = new MarkerOptions().position(passengerLoc).title(title).snippet("Calculate route to "+Username +"?").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));;
            mMap.addMarker(options);

            DestLoc = new LatLng(DstLat,DstLon);
            MarkerOptions options2 = new MarkerOptions().position(DestLoc).title("Your Destination");
            mMap.addMarker(options2);

            mMap.setOnInfoWindowClickListener(this);

            //also create marker for their destination

            //getPassengerLocation(PassengerID,Username,Lat,Lon);
            //once both markers are set up -- form a route between the two using directions API

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

        mapFragment.getMapAsync(PassengerLocation.this);

        getLocationPermission();

        Back = findViewById(R.id.back);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

       // lat = Float.valueOf(intent.getStringExtra("Lat"));
      // Toast.makeText(this, ""+Lat, Toast.LENGTH_SHORT).show();



        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void getCurrentUserLocation() {


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (LocationPermissionsGranted) {

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();

                            DriverPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

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



    private void moveCamera(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).tilt(70)
                .zoom(10)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                Log.d(TAG, "HI routes: "+ result.routes[0].toString());
                Log.d(TAG, "HI duration: "+ result.routes[0].legs[0].duration);
                Log.d(TAG, "HI distance: "+ result.routes[0].legs[0].duration);
                Log.d(TAG, "HI geo: "+ result.geocodedWaypoints[0].toString());
                DrawRoute(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.d(TAG, "distance: Fail"+ e.getMessage());

            }
        });

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
}

