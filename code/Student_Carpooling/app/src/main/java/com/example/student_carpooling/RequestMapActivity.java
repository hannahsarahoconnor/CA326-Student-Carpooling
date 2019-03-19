package com.example.student_carpooling;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
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
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestMapActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private String TripID, CurrentUserID,driverNotificationKey,PickUp;
    private AutocompleteSupportFragment autocompleteFragment;
    private LatLng latLng;
    private Intent intent;



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        init();
        }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        String apiKey = getResources().getString(R.string.google_maps_places_key);
        Places.initialize(getApplicationContext(), apiKey);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        Button Request = findViewById(R.id.request);
        Button Cancel = findViewById(R.id.cancel);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null){
          CurrentUserID = firebaseUser.getUid();
        }

        Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = getIntent();
                String DriverID = intent.getStringExtra("DriverID");
                TripID = intent.getStringExtra("TripID");
                try{
                    //make sure that a place has been selected
                    if(!PickUp.equals("null")){
                        //get driver notification key to send notification key to information them of the request
                        DatabaseReference _DriverNotificationKey = FirebaseDatabase.getInstance().getReference().child("users").child(DriverID);
                        _DriverNotificationKey.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                                if(map!= null) {

                                if(map.get("NotificationKey")!=null){
                                    driverNotificationKey = (String) map.get("NotificationKey");
                                    new Notification("You have a new request","Student Carpooling",driverNotificationKey);

                                }}}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //add the hash map of request (info passenger id, lat & long) to the tripdetails
                        DatabaseReference TripDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverID).child(TripID).child("TripRequests").child(CurrentUserID);
                        Map<String, Object> RequestInfo = new HashMap<>();
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
        //filter the result to address so a more exact location can be retrieved
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                PickUp = place.getName();
                //get the geo coordinates of the place name
                geoLocate();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(RequestMapActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        autocompleteFragment.setHint("Starting Point?");
    }


    private void geoLocate() {

        Geocoder geocoder = new Geocoder(RequestMapActivity.this);

        List<Address> list = new ArrayList<>();
        try {
            //only want one result
            list = geocoder.getFromLocationName(PickUp, 1);

        } catch (IOException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (list.size() > 0) {
            // get the first results(only 1)
            Address address = list.get(0);
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
            //moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),address.getAddressLine(0));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).tilt(70).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            MarkerOptions options = new MarkerOptions().position(latLng).title(address.getAddressLine(0));
            mMap.addMarker(options);
        }
    }


}