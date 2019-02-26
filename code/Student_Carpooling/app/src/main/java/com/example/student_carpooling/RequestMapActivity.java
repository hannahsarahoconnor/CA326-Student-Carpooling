package com.example.student_carpooling;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.student_carpooling.tripRecyclerView.Trip;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestMapActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private static final String TAG = ".RequestMapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean LocationPermissionsGranted = false;

    private String PickUp,PickUpID,DriverID,TripID, CurrentUserID,driverNotificationKey;
    AutocompleteSupportFragment autocompleteFragment;

    private LatLng latLng;

    private Button Request, Cancel;

    Intent intent;

    private FirebaseAuth mAuth;



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;



        if (LocationPermissionsGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //pass the driver and trip id




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String apiKey = "AIzaSyBTJ-pUGMT8ypVgiyWqy0T_nSoT6z0bOIA";
        Places.initialize(getApplicationContext(), apiKey);

        //Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        getLocationPermission();

        Request = findViewById(R.id.request);
        Cancel = findViewById(R.id.cancel);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = getIntent();
                DriverID = intent.getStringExtra("DriverID");
                TripID = intent.getStringExtra("TripID");
               try{
                if(!PickUp.equals("null")){

                    Toast.makeText(RequestMapActivity.this, ""+DriverID, Toast.LENGTH_SHORT).show();

                   //get driver notification key to send notification key to information them of the request
                    DatabaseReference _DriverNotificationKey = FirebaseDatabase.getInstance().getReference().child("users").child(DriverID);
                    _DriverNotificationKey.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                            if(map.get("NotificationKey")!=null){
                                driverNotificationKey = map.get("NotificationKey").toString();
                                new SendNotification("You have a new request","Student Carpooling",driverNotificationKey);

                            }}
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //add the hash map of request (info passenger id, lat & long) to the tripdetails
                    DatabaseReference TripDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverID).child(TripID).child("TripRequests").child(CurrentUserID);
                    Map RequestInfo = new HashMap();
                    RequestInfo.put("Lat", latLng.latitude);
                    RequestInfo.put("Lon",latLng.longitude);
                    TripDB.setValue(RequestInfo);
                    Toast.makeText(RequestMapActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                    finish();

                }}
                catch (NullPointerException e){

                   Toast.makeText(RequestMapActivity.this, "Please enter a pick up point", Toast.LENGTH_SHORT).show();

               }}

        });

       Cancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });

    }


    private void init() {


        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setCountry("IE");
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                //LatLng string_location = place.getLatLng();
                //String address = (String) place.getAddress();
                PickUp = (String) place.getName();
                PickUpID = (String) place.getId();
                geoLocate();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                // Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(RequestMapActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        autocompleteFragment.setHint("Starting Point?");
    }


    private void geoLocate() {

        Geocoder geocoder = new Geocoder(RequestMapActivity.this);

        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(PickUp, 1);

        } catch (IOException e) {
            Log.e(TAG, "IOException:" + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            //Toast.makeText(RequestMapActivity.this, "" + address.toString(), Toast.LENGTH_LONG).show();
            //moveCamera(new LatLng(address.getLatitude(),address.getLongitude(),address.getAddressLine(0));
           // Toast.makeText(RequestMapActivity.this, "" + address.getAddressLine(0), Toast.LENGTH_LONG).show();

            latLng = new LatLng(address.getLatitude(), address.getLongitude());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),address.getAddressLine(0));
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */



    private void moveCamera(LatLng latLng, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        //mMap.getUiSettings().setZoomControlsEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).tilt(70)
                .zoom(15)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }


    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(RequestMapActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                LocationPermissionsGranted = true;
                initMap();
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
        Log.d(TAG, "onRequestPermissionsResult: called.");
        LocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            LocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    LocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }}