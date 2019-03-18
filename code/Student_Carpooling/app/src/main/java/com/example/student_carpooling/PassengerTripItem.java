package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.passengerRecyclerView.Passenger;
import com.example.student_carpooling.passengerRecyclerView.PassengerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class PassengerTripItem extends AppCompatActivity {


    private int passengerCount=0;

    private TextView DriverUserName, DriverText, PassengerText, CancelText;
    private ImageView DriverPic, MessageDriver, DriverProfile;
    private Date rightNow,expiredTripdate;
    private Button Leave,Track;

    private ImageView delete;

    private String _tripid, _driverid,_driverUsername,UserId,NotificationKey,Fullname, PicUrL, NotficationKey, Username;

    private TextView passCount;
    private Button CancelButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter passengerAdapter;
    private float dstlat, dstlon, Plat, Plon, Lat, Lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_trip_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView back = findViewById(R.id.back);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser != null) {
            UserId = CurrentUser.getUid();
        }



        passCount = findViewById(R.id.text);
        recyclerView = findViewById(R.id.passengerRecycler);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        passengerAdapter = new PassengerAdapter(getDataPassenger(), PassengerTripItem.this);
        recyclerView.setAdapter(passengerAdapter);

        delete = findViewById(R.id.delete);
        Leave = findViewById(R.id.Leave);
        Track = findViewById(R.id.Track);
        rightNow = Calendar.getInstance().getTime();

        Intent intent = getIntent();
        String _starting = intent.getStringExtra("Starting");
        String _destination = intent.getStringExtra("Destination");
        final String _date = intent.getStringExtra("Date");
        final String _time = intent.getStringExtra("Time");
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


        TextView Starting = findViewById(R.id.Starting);
        TextView Destination = findViewById(R.id.Destination);
        TextView Date = findViewById(R.id.Date);
        TextView Time = findViewById(R.id.Time);
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

        DatabaseReference cancelCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(_driverid).child(_tripid);
        cancelCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                if(map!= null) {
                if (map.get("Completed") != null) {
                    String completed = Objects.requireNonNull(map.get("Completed")).toString();
                    int Completed = Integer.parseInt(completed);

                    if(map.get("Started") != null) {

                     String started = Objects.requireNonNull(map.get("Started")).toString();
                     int Started = Integer.parseInt(started);
                     if(Started == 0){
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                        try {
                            //Split the original time and add 5 hours
                            StringTokenizer tokens = new StringTokenizer(_time, ":");
                            int hours = Integer.parseInt(tokens.nextToken()) + 5;
                            String mins = tokens.nextToken();
                            String ExpiredTime = Integer.toString(hours) + ":" + mins;
                            String ExpiredDateStr = _date + " " + ExpiredTime;
                            java.util.Date ExpiredDate = format.parse(ExpiredDateStr);
                            long mili2 = ExpiredDate.getTime();
                            expiredTripdate = new Date(mili2);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(rightNow.after(expiredTripdate)){
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
                        }
                    }

                    if(Started == 1 && Completed == 1){
                        recyclerView.setVisibility(View.GONE);
                        DriverProfile.setVisibility(View.GONE);
                        Track.setVisibility(View.GONE);
                        MessageDriver.setVisibility(View.GONE);
                        DriverUserName.setVisibility(View.GONE);
                        DriverPic.setVisibility(View.GONE);
                        PassengerText.setVisibility(View.GONE);
                        DriverText.setVisibility(View.GONE);
                        Leave.setVisibility(View.GONE);
                        CancelText.setText("This trip is completed");
                        CancelText.setVisibility(View.VISIBLE);
                        CancelButton.setVisibility(View.GONE);
                        delete.setVisibility(View.VISIBLE);
                        showDialog();
                        return;
                    }


                    if (map.get("Cancelled") != null) {
                        String cancel = Objects.requireNonNull(map.get("Cancelled")).toString();
                        int Cancel = Integer.parseInt(cancel);

                        if (Completed == 1 || Cancel == 1) {
                            delete.setVisibility(View.VISIBLE);
                           showDialog();

                        }


                        if (Cancel == 1){
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
                                    Intent intent = new Intent(PassengerTripItem.this, TripComplete.class);
                                    intent.putExtra("TripID", _tripid);
                                    intent.putExtra("DriverID", _driverid);
                                    intent.putExtra("DriverUsername", _driverName);
                                    intent.putExtra("driverUrl", _PicUrl);
                                    intent.putExtra("Cancelled", "1");
                                    startActivity(intent);
                                    finish();

                                }
                            });

                        }


                        if (Cancel == 0) {

                            try {
                                if (!_PicUrl.equals("defaultPic")) {
                                    Glide.with(PassengerTripItem.this).load(_PicUrl).into(DriverPic);
                                }
                            } catch (Exception e) {
                                //Toast.makeText(PassengerTripItem.this, "", Toast.LENGTH_SHORT).show();
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
                                    intent.putExtra("ID", _driverid);
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
                                    intent.putExtra("Username", _driverUsername);
                                    startActivity(intent);
                                }
                            });

                            Leave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //deleted trip

                                }
                            });
                            //check for if passenger was removed -- is showing is only review driver
                            DatabaseReference removedCheck = FirebaseDatabase.getInstance().getReference().child("users").child(UserId).child("Trips").child(_driverid).child(_tripid);
                            removedCheck.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                                    if(map!= null) {
                                    if (map.get("Removed") != null) {
                                        String removed = Objects.requireNonNull(map.get("Removed")).toString();
                                        int Removed = Integer.parseInt(removed);
                                        if(Removed == 1){
                                            recyclerView.setVisibility(View.GONE);
                                            Track.setVisibility(View.GONE);
                                            CancelText.setVisibility(View.VISIBLE);
                                            CancelText.setText("The driver has removed you from the trip");
                                            PassengerText.setVisibility(View.GONE);
                                            Leave.setVisibility(View.GONE);
                                            CancelButton.setVisibility(View.VISIBLE);

                                            CancelButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(PassengerTripItem.this, TripComplete.class);
                                                    intent.putExtra("TripID", _tripid);
                                                    intent.putExtra("DriverID", _driverid);
                                                    intent.putExtra("DriverUsername", _driverName);
                                                    intent.putExtra("driverUrl", _PicUrl);
                                                    intent.putExtra("Cancelled", "1");
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            });

                                        }
                                        else{
                                            //set up recycler view of other passengers in the trip
                                            getOtherPassengers(_driverid, _tripid);
                                        }
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

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void showDialog() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(PassengerTripItem.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog, null);
                TextView Text = dialogView.findViewById(R.id.Text);
                Text.setText("By deleting this trip, you will no longer be able to view it's information. Are you sure you wish to continue? ");
                Button Submit = dialogView.findViewById(R.id.Submit);


                Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference trips = FirebaseDatabase.getInstance().getReference().child("users").child(UserId).child("Trips").child(_driverid).child(_tripid);
                        trips.removeValue();
                        Intent intent = new Intent(PassengerTripItem.this, PassengerTrips.class);
                        startActivity(intent);
                        finish();
                        dialogBuilder.dismiss();
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
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
                        if (PassKey != null && !PassKey.equals(UserId)) {
                            getPassengerInfo(DriverID, TripId, PassKey);
                        }

                    }
                }
                else{
                    passCount.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
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
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {
                        if (map.get("NotificationKey") != null) {
                            NotficationKey = (String) map.get("NotificationKey");
                        }
                        if (map.get("Username") != null) {
                            Username = (String) map.get("Username");
                        }
                        if (map.get("lat") != null) {
                            String lat = (String) map.get("lat");
                            if (lat != null) {
                                Plat = Float.parseFloat(lat);
                            }
                        }
                        if (map.get("lon") != null) {
                            String lon = (String) map.get("lon");
                            if (lon != null) {
                                Plon = Float.parseFloat(lon);
                            }
                        }
                        if (map.get("profileImageUrl") != null) {
                            PicUrL = (String) map.get("profileImageUrl");
                        }
                        if (map.get("Fullname") != null) {
                            Fullname = (String) map.get("Fullname");
                        }


                        Passenger object = new Passenger("Passenger", _driverUsername, _tripid, dstlat, dstlon, Fullname, ID, PicUrL, Username, Plat, Plon, NotficationKey);
                        resultsPassengers.add(object);

                        passengerAdapter.notifyDataSetChanged();
                        passengerCount++;
                        if (passengerCount == 0) {
                            Toast.makeText(PassengerTripItem.this, "There are no other passengers", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<Passenger> resultsPassengers = new ArrayList<>();

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
