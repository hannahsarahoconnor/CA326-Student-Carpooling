package com.example.student_carpooling;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class PassengerMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean LocationPermissionsGranted = false;

    Marker driverMarker;

    private Button Back;

    String TripId, DriverId;

    float DstLat, DstLon, Lat, Lon;

    com.google.android.gms.maps.model.LatLng passengerLoc,DstLoc,driverLL ;

    Intent intent;
    private FirebaseAuth mAuth;

    String CurrentUserID, _driverUsername,_notificationKey;

    int arrived = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationPermission();

        Back = findViewById(R.id.back);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //show the passengers pick up location, should show the other passnegers positions too...
        //Show Pop up -- your driver has X other pick ups for this trip..
        //the destination and the movements of the driver
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (LocationPermissionsGranted) {

            //get data from intent

            intent = getIntent();

            TripId = intent.getStringExtra("TripID");
            DriverId = intent.getStringExtra("DriverID");
            _notificationKey = intent.getStringExtra("NotificationKey");

            //DstLat = intent.getFloatExtra("DstLat",0);
            //DstLon = intent.getFloatExtra("DstLon",0);

            //place your pick up marker
            DatabaseReference PickUpLocation = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverId).child(TripId).child("Passengers").child(CurrentUserID);
            PickUpLocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if(map.get("lat")!=null){
                            Lat = Float.parseFloat(map.get("lat").toString());
                        }
                        if(map.get("lon")!=null){
                            Lon = Float.parseFloat(map.get("lon").toString());
                        }
                        passengerLoc = new com.google.android.gms.maps.model.LatLng(Lat, Lon);
                        MarkerOptions options = new MarkerOptions().position(passengerLoc).title("Your Pickup Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        mMap.addMarker(options);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(passengerLoc).tilt(70)
                                .zoom(15)
                                .build();
                        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference Dst = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverId).child(TripId);
            Dst.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if(map.get("DstLat")!=null){
                            DstLat = Float.parseFloat(map.get("DstLat").toString());
                        }
                        if(map.get("DstLon")!=null){
                            DstLon = Float.parseFloat(map.get("DstLon").toString());
                        }
                        DstLoc = new com.google.android.gms.maps.model.LatLng(DstLat,DstLon);
                        MarkerOptions options = new MarkerOptions().position(DstLoc).title("Your Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        mMap.addMarker(options);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            //PickUpLocation();
            //DestinationLoc();


            //add destination marker

            handler.postDelayed(runnable, 1000);
            //get the new location every second


        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //this may not be needed?
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
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getDriverLocation();
            handler.postDelayed(this, 5000);
        }
    };


    public void PickUpLocation(){
        DatabaseReference PickUpLocation = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverId).child(TripId).child("Passengers").child(CurrentUserID);
        PickUpLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("lat")!=null){
                        Lat = Float.parseFloat(map.get("lat").toString());
                    }
                    if(map.get("lon")!=null){
                        Lon = Float.parseFloat(map.get("lon").toString());
                    }
                    passengerLoc = new com.google.android.gms.maps.model.LatLng(Lat, Lon);
                    MarkerOptions options = new MarkerOptions().position(passengerLoc).title("Your Pickup Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    mMap.addMarker(options);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void DestinationLoc(){
        DatabaseReference Dst = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverId).child(TripId);
        Dst.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("DstLat")!=null){
                        DstLat = Float.parseFloat(map.get("DstLat").toString());
                    }
                    if(map.get("DstLon")!=null){
                        DstLon = Float.parseFloat(map.get("DstLon").toString());
                    }
                    DstLoc = new com.google.android.gms.maps.model.LatLng(DstLat,DstLon);
                    MarkerOptions options = new MarkerOptions().position(DstLoc).title("Your Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    mMap.addMarker(options);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void getDriverLocation(){
        // add listener to the changes of the drivers' lat and lon in the database
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverId).child(TripId).child("DriverLocation").child("l");
        driverLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double lon = 0;
                    double lat = 0;
                    if(map.get(0)!=null){
                        lat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1)!=null){
                        lon = Double.parseDouble(map.get(1).toString());
                    }
                    driverLL = new com.google.android.gms.maps.model.LatLng(lat,lon);
                    if(driverMarker != null){
                        driverMarker.remove();
                    }

                    driverMarker = mMap.addMarker(new MarkerOptions().position(driverLL).title("Driver"));



                    //less than 100 km in distance
                    //check to see if driver is close to that passenger.. or close to destination...

                    Location pass = new Location("");
                    pass.setLatitude(Lat);
                    pass.setLongitude(Lon);

                    Location Driver = new Location("");
                    Driver.setLatitude(driverLL.latitude);
                    Driver.setLongitude(driverLL.longitude);

                    float distance = Driver.distanceTo(pass);

                    if(distance<100 && arrived==0){
                        arrived++;
                        //send notification and display toast message
                        Toast.makeText(PassengerMap.this, "The Driver is at your pickup Location", Toast.LENGTH_SHORT).show();
                        new SendNotification(_driverUsername + " is at your pickup Location", "Student Carpooling", _notificationKey);
                        //dont want the passenger to get repeated notifications
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

    @Override
    protected void onStop() {
        super.onStop();
    }
}
