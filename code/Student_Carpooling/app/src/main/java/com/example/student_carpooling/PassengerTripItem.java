package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.passengerRecyclerView.Passenger;
import com.example.student_carpooling.passengerRecyclerView.PassengerAdapter;
import com.example.student_carpooling.passengerTripsRecyclerView.PassengerTrip;
import com.example.student_carpooling.usersRecyclerView.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PassengerTripItem extends AppCompatActivity {

    Intent intent;

    int passengerCount=0;

    TextView Starting, Destination, Date, Time, DriverUserName, DriverText, PassengerText, CancelText;
    ImageView DriverPic, MessageDriver, DriverProfile;

    Button Leave,Track;

    private LinearLayoutManager passengerLayoutManager;


    ImageView delete;

    private String Cancelled,Completed,_tripid, _driverid,_driverUsername,UserId, Name,NotificationKey, Surname, Fullname, PicUrL, NotficationKey, Username;

    private Button CancelButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter passengerAdapter;
    private float dstlat, dstlon, Plat, Plon, Lat, Lon;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_trip_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //be able to go back out of the activity
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();


        recyclerView = findViewById(R.id.passengerRecycler);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        passengerAdapter = new PassengerAdapter(getDataPassenger(), PassengerTripItem.this);
        passengerLayoutManager = new LinearLayoutManager(PassengerTripItem.this);
        recyclerView.setAdapter(passengerAdapter);

        delete = findViewById(R.id.delete);
        Leave = findViewById(R.id.Leave);
        Track = findViewById(R.id.Track);

        intent = getIntent();
        String _starting = intent.getStringExtra("Starting");
        String _destination = intent.getStringExtra("Destination");
        String _date = intent.getStringExtra("Date");
        String _time = intent.getStringExtra("Time");
        _tripid = intent.getStringExtra("TripID");
        _driverid = intent.getStringExtra("DriverID");
        _driverUsername = intent.getStringExtra("DriverUsername");
        NotificationKey = intent.getStringExtra("NotificationKey");
        final String _PicUrl = intent.getStringExtra("PicURL");
        final String _driverName = intent.getStringExtra("DriverName");
        Lat = intent.getFloatExtra("lat", 0);
        Lon = intent.getFloatExtra("lon", 0);
        dstlat = intent.getFloatExtra("dstlat", 0);
        dstlon = intent.getFloatExtra("dstlon", 0);


        Starting = findViewById(R.id.Starting);
        Destination = findViewById(R.id.Destination);
        Date = findViewById(R.id.Date);
        Time = findViewById(R.id.Time);
        DriverUserName = findViewById(R.id.DriverUserName);
        DriverPic = findViewById(R.id.DriverProfilePic);
        MessageDriver = findViewById(R.id.message);
        DriverProfile = findViewById(R.id.profile);
        DriverText = findViewById(R.id.driverText);
        PassengerText = findViewById(R.id.passengerText);
        CancelButton = findViewById(R.id.CancelButton);
        CancelText = findViewById(R.id.CancelText);


        Starting.setText(_starting);
        Destination.setText(_destination);
        Time.setText(_time);
        Date.setText(_date);

        //check if trip is cancelled
        Toast.makeText(PassengerTripItem.this, ""+_tripid, Toast.LENGTH_SHORT).show();
        DatabaseReference cancelCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(_driverid).child(_tripid);
        cancelCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map.get("Completed") != null) {
                    Completed = map.get("Completed").toString();


                if (map.get("Cancelled") != null) {
                    String Cancel = map.get("Cancelled").toString();


                    if (Integer.parseInt(Cancel) == 1) {
                        recyclerView.setVisibility(View.GONE);
                        DriverProfile.setVisibility(View.GONE);
                        Track.setVisibility(View.GONE);
                        MessageDriver.setVisibility(View.GONE);
                        DriverUserName.setVisibility(View.GONE);
                        DriverPic.setVisibility(View.GONE);
                        PassengerText.setVisibility(View.GONE);
                        DriverText.setVisibility(View.GONE);
                        Leave.setVisibility(View.GONE);

                        CancelText.setVisibility(View.VISIBLE);
                        CancelButton.setVisibility(View.VISIBLE);

                        CancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PassengerTripItem.this,TripComplete.class);
                                intent.putExtra("TripID",_tripid);
                                intent.putExtra("DriverID",_driverid);
                                intent.putExtra("DriverUsername",_driverName);
                                intent.putExtra("driverUrl",_PicUrl);
                                intent.putExtra("Cancelled","1");
                                startActivity(intent);
                                finish();

                            }
                        });

                    }


                    if (Integer.parseInt(Cancel) == 0) {


                        if (!_PicUrl.equals("defaultPic")) {
                            Glide.with(PassengerTripItem.this).load(_PicUrl).into(DriverPic);
                        }

                        DriverUserName.setText(_driverUsername);

                        MessageDriver.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PassengerTripItem.this, ChatActivity.class);
                                intent.putExtra("Username", _driverUsername);
                                intent.putExtra("ID", _driverid);
                                intent.putExtra("Fullname", _driverName);
                                intent.putExtra("ProfilePicURL", _PicUrl);
                                Toast.makeText(PassengerTripItem.this, "Starting Chat with " + _driverUsername, Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                        });

                        DriverProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PassengerTripItem.this, UserProfile.class);
                                startActivity(intent);

                            }
                        });

                        Track.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PassengerTripItem.this, PassengerMap.class);
                                intent.putExtra("TripID", _tripid);
                                intent.putExtra("DriverID", _driverid);
                                intent.putExtra("DstLat", dstlat);
                                intent.putExtra("DstLon", dstlon);
                                intent.putExtra("Lat", Lat);
                                intent.putExtra("Lon", Lon);
                                intent.putExtra("PicUrl", _PicUrl);
                                intent.putExtra("NotificationKey", NotificationKey);
                                startActivity(intent);
                            }
                        });

                        Leave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        //set up recycler view of other passengers in the trip
                        getOtherPassengers(_driverid, _tripid);

                    }

                        if (Integer.parseInt(Completed) == 1 || Integer.parseInt(Cancel) == 1) {
                            delete.setVisibility(View.VISIBLE);

                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PassengerTripItem.this);
                                    builder.setTitle("Are you sure you want to delete this trip?").setMessage("Once deleted, you will no longer see this trip information, are you sure?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int which) {
                                            //dont wanna delete it completed as then the passenger cant see, add value -> deleted, so that it wont be show in their page
                                            DatabaseReference trips = FirebaseDatabase.getInstance().getReference().child("users").child(UserId).child("Trips").child(_driverid).child(_tripid);
                                            trips.removeValue();
                                            Intent intent = new Intent(PassengerTripItem.this,PassengerTrips.class);
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

    private void getOtherPassengers(final String DriverID,final String TripId){

        DatabaseReference TripPassengers = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverID).child(TripId).child("Passengers");
        TripPassengers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot id : dataSnapshot.getChildren()){
                        String PassKey = id.getKey();
                        if(!PassKey.equals(UserId)){
                         // Toast.makeText(PassengerTripItem.this, ""+PassKey, Toast.LENGTH_SHORT).show();
                          getPassengerInfo(DriverID,TripId,PassKey);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPassengerInfo(String DriverID, String TripId, final String ID){
        DatabaseReference TripPassengers = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverID).child(TripId).child("Passengers").child(ID);
        TripPassengers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("NotificationKey")!= null){
                        NotficationKey = map.get("NotificationKey").toString();
                    }
                    if(map.get("Username")!= null){
                        Username = map.get("Username").toString();
                    }
                    if(map.get("lat")!= null){
                        Plat = Float.parseFloat(map.get("lat").toString());
                    }
                    if(map.get("lon")!= null){
                        Plon = Float.parseFloat(map.get("lon").toString());
                    }
                    if(map.get("profileImageUrl")!= null){
                        PicUrL = map.get("profileImageUrl").toString();
                    }
                    if(map.get("Fullname")!= null){
                        Fullname = map.get("Fullname").toString();
                    }
                   //Toast.makeText(PassengerTripItem.this, ""+PicUrL, Toast.LENGTH_SHORT).show();


                    Passenger object = new Passenger("Passenger",_driverUsername,_tripid,dstlat,dstlon,Fullname,ID,PicUrL,Username,Plat,Plon,NotficationKey);
                    resultsPassengers.add(object);

                    passengerAdapter.notifyDataSetChanged();
                    passengerCount++;
                    if(passengerCount ==0 ){
                       Toast.makeText(PassengerTripItem.this, "There are no other passengers", Toast.LENGTH_SHORT).show();
                   }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private ArrayList resultsPassengers = new ArrayList<Passenger>();

    private List<Passenger> getDataPassenger() {
        return resultsPassengers;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if >0 check
        resultsPassengers.clear();
    }
}
