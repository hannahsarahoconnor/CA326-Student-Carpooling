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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PassengerMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker driverMarker;
    private String TripId, DriverId;
    private float DstLat, DstLon, Lat, Lon;
    private com.google.android.gms.maps.model.LatLng passengerLoc,DstLoc,driverLL ;
    private String CurrentUserID, _driverUsername,_notificationKey,PicUrl;
    private int arrived = 0,Completed;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        Button Back = findViewById(R.id.back);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null){
        CurrentUserID = firebaseUser.getUid();
        }

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

        Intent intent = getIntent();

        TripId = intent.getStringExtra("TripID");
        DriverId = intent.getStringExtra("DriverID");
        _notificationKey = intent.getStringExtra("NotificationKey");
        _driverUsername = intent.getStringExtra("Username");

        PicUrl = intent.getStringExtra("PicUrl");


        DatabaseReference CompletedCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverId).child(TripId);
        CompletedCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {

                        if (map.get("Completed") != null) {
                            String completed = Objects.requireNonNull(map.get("Completed")).toString();
                            Completed = Integer.parseInt(completed);
                            if (Completed == 0) {


                                    PickUpLocation();
                                    DestinationLoc();


                                    //add destination marker

                                    handler.postDelayed(runnable, 1000);
                                    //get the new location every second
                                mMap.getUiSettings().setZoomControlsEnabled(true);
                                //this may not be needed?
                                if (ActivityCompat.checkSelfPermission(PassengerMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PassengerMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }

                                //allow the map to get the devices location
                                mMap.setMyLocationEnabled(true);
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                            }

                            if (Completed == 1) {
                                //the trip is ended -> prompt rating system
                                Intent intent = new Intent(PassengerMap.this, TripComplete.class);
                                intent.putExtra("TripID", TripId);
                                intent.putExtra("DriverID", DriverId);
                                intent.putExtra("DriverUsername", _driverUsername);
                                intent.putExtra("driverUrl", PicUrl);
                                intent.putExtra("Cancelled", "0");

                                //update completed trip count
                                final DatabaseReference completedCheck = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserID);
                                completedCheck.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String completed = (String) dataSnapshot.child("CompletedTrips").getValue();
                                        if (completed != null) {
                                            count = Integer.valueOf(completed);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                completedCheck.child("CompletedTrips").setValue(count + 1);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




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
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {
                        if (map.get("lat") != null) {
                            String lat = Objects.requireNonNull(map.get("lat")).toString();
                             Lat = Float.parseFloat(lat);
                        }
                        if (map.get("lon") != null) {
                            String lon = Objects.requireNonNull(map.get("lon")).toString();
                            Lon = Float.parseFloat(lon);
                        }
                        passengerLoc = new com.google.android.gms.maps.model.LatLng(Lat, Lon);
                        MarkerOptions options = new MarkerOptions().position(passengerLoc).title("Your Pickup Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        mMap.addMarker(options);

                    }
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
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {
                        if (map.get("DstLat") != null) {
                            String lat = Objects.requireNonNull(map.get("DstLat")).toString();
                            DstLat = Float.parseFloat(lat);
                        }
                        if (map.get("DstLon") != null) {
                            String lon = Objects.requireNonNull(map.get("DstLon")).toString();
                            DstLon = Float.parseFloat(lon);
                        }
                        DstLoc = new com.google.android.gms.maps.model.LatLng(DstLat, DstLon);
                        MarkerOptions options = new MarkerOptions().position(DstLoc).title("Your Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        mMap.addMarker(options);
                    }
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
                    GenericTypeIndicator<List<Object>> t = new GenericTypeIndicator<List<Object>>() {};
                    List<Object> map = dataSnapshot.getValue(t);
                    double lon = 0;
                    double lat = 0;
                    if(map != null) {
                        if (map.get(0) != null) {
                            lat = Double.parseDouble(map.get(0).toString());
                        }
                        if (map.get(1) != null) {
                            lon = Double.parseDouble(map.get(1).toString());
                        }
                        driverLL = new com.google.android.gms.maps.model.LatLng(lat, lon);
                        if (driverMarker != null) {
                            driverMarker.remove();
                        }

                        driverMarker = mMap.addMarker(new MarkerOptions().position(driverLL).title("Driver"));

                        //create map bounds so all markers will be shown on screen

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(driverLL);
                        builder.include(DstLoc);
                        builder.include(passengerLoc);
                        LatLngBounds bounds = builder.build();

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,35);
                        mMap.animateCamera(cameraUpdate);

                        //less than 100 km in distance
                        //check to see if driver is close to that passenger.. or close to destination...
                        Location pass = new Location("");
                        pass.setLatitude(Lat);
                        pass.setLongitude(Lon);

                        Location Driver = new Location("");
                        Driver.setLatitude(driverLL.latitude);
                        Driver.setLongitude(driverLL.longitude);

                        float distance = Driver.distanceTo(pass);

                        if (distance < 100 && arrived == 0) {
                            arrived++;
                            //could update the databse under passenger--> PickedUp = 1 and driver can view this, and if picked up, remove pick --> show snackbar?
                            DatabaseReference PickedUp = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverId).child(TripId).child("Passengers").child(CurrentUserID);
                            PickedUp.child("PickedUp").setValue(1);
                        }

                        if(arrived > 0){
                            //send notification and display toast message
                            //dont want the passenger to get repeated notifications
                            Toast.makeText(PassengerMap.this, "The Driver is at your pickup Location", Toast.LENGTH_SHORT).show();
                            new SendNotification("Your driver " + _driverUsername + " is at your pickup Location", "Student Carpooling", _notificationKey);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
