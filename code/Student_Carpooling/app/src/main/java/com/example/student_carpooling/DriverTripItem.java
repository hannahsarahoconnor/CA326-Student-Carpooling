package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.passengerRecyclerView.Passenger;
import com.example.student_carpooling.passengerRecyclerView.PassengerAdapter;
import com.example.student_carpooling.seatRecyclerView.Seat;
import com.example.student_carpooling.seatRecyclerView.SeatAdapter;
import com.example.student_carpooling.usersRecyclerView.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DriverTripItem extends AppCompatActivity {


    Toolbar toolbar;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;

    private ImageView navProfile;
    private TextView textView;
    private String email,UserID;
    private DatabaseReference UserDb, StartedDb;
    private String TripID, UserName, profilePicurl, NotificationKey, Name,Surname,Fullname, DriverUsername, isStarted,isCompleted,Type;

    private float Lat,Lon, dlat, dlon;
    FirebaseUser CurrentUser;
    TextView starting, destination, time ,date, seats, luggage;

    String Seats;

    String _username;

    Intent intent;

    int completed, started, passengerCount=0;

    private LinearLayoutManager passengerLayoutManager,seatsLayoutManager;


    private RecyclerView recyclerView, SeatrecyclerView;
    private RecyclerView.Adapter passengerAdapter, seatsAdapter;

    private Button cancel, request, start;

    private ArrayList<String> PassengerNotKey = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_trip_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Trip Information");

        textView = findViewById(R.id.text);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //be able to go back out of the activity
                Intent intent = new Intent(DriverTripItem.this, DriverTrips.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        UserID = mAuth.getCurrentUser().getUid();
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);

        recyclerView = findViewById(R.id.passengerRecycler);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        passengerAdapter = new PassengerAdapter(getDataPassenger(), DriverTripItem.this);
        passengerLayoutManager = new LinearLayoutManager(DriverTripItem.this);
        recyclerView.setAdapter(passengerAdapter);

        //recycler view for the seats -- show the no in real times once a new passenger is accepted or one is removed
        SeatrecyclerView = findViewById(R.id.seatsRecycler);
        SeatrecyclerView.setNestedScrollingEnabled(false);
        SeatrecyclerView.setHasFixedSize(true);
        SeatrecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        seatsAdapter = new SeatAdapter(getDataSeat(), DriverTripItem.this);
        seatsLayoutManager = new LinearLayoutManager(DriverTripItem.this);
        SeatrecyclerView.setAdapter(seatsAdapter);


        starting = findViewById(R.id.Starting);
        destination = findViewById(R.id.Destination);
        date = findViewById(R.id.Date);
        time = findViewById(R.id.Time);
        seats = findViewById(R.id.Seats);
        luggage = findViewById(R.id.Luggage);

        cancel = findViewById(R.id.Cancel);
        request = findViewById(R.id.Requests);
        start = findViewById(R.id.Start);

        intent = getIntent();

        String _starting = intent.getStringExtra("Starting");
        String _destination = intent.getStringExtra("Destination");
        String _date = intent.getStringExtra("Date");
        String _time = intent.getStringExtra("Time");
        String _seats = intent.getStringExtra("Seats");
        String _luggage = intent.getStringExtra("Luggage");
        _username = intent.getStringExtra("Username");

        dlat = intent.getFloatExtra("DstLat",0);
        dlon = intent.getFloatExtra("DstLon",0);





        TripID = intent.getStringExtra("TripID");

        starting.setText(_starting);
        destination.setText(_destination);
        date.setText(_date);
        time.setText(_time);
        StartedDb = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
        StartedDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map.get("Started") != null) {
                    started = Integer.parseInt(map.get("Started").toString());
                }
                if (map.get("Completed") != null) {
                    completed = Integer.parseInt(map.get("Completed").toString());
                    //Toast.makeText(DriverTripItem.this, "" + completed, Toast.LENGTH_SHORT).show();
                    if(completed == 1){
                        cancel.setVisibility(View.INVISIBLE);
                        request.setVisibility(View.INVISIBLE);
                        start.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //listener for the seats no!!

        DatabaseReference SeatCount = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
        SeatCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Seats") != null) {
                        Seats = map.get("Seats").toString();
                        resultsSeats.clear();
                        //seatsAdapter.notifyDataSetChanged();

                        Seat object = new Seat(Seats);
                        resultsSeats.add(object);
                        seatsAdapter.notifyDataSetChanged();


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //check to see if the trip has been started yet, or completed


        //is current time is later-- also set iscompleted to true...


        getPassengers();



            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DriverTripItem.this);
                    dialog.setTitle("Are you sure you want to delete this trip?");
                    dialog.setMessage("By Doing this, all participating passengers will be notified of this cancellation and will be able to review your account");
                    dialog.setPositiveButton("Delete Trip", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference TripDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
                            TripDB.removeValue();
                            Toast.makeText(DriverTripItem.this, "This trip has been deleted", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(DriverTripItem.this, DriverTrips.class);
                            startActivity(intent);
                            //send notification to passengers of the cancellation
                        }


                    });

                    dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();

                }
            });

            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DriverTripItem.this, TripRequests.class);
                    intent.putExtra("TripID", TripID);
                    startActivity(intent);
                }
            });

            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Show a dialog first to confirm that want to start this trip


                    //get a passenger counts
                    if (passengerAdapter.getItemCount() != 0) {

                        //only send the notification when the trip first starts, as the driver can click in and out and map without constantly resending the notifications each time
                        // if the trip is past the date/trip, the driver cannot start it..
                        //if (!Boolean.parseBoolean(isStarted) && !Boolean.parseBoolean(isCompleted)) {
                       if(started == 0){
                        for (String notKey : PassengerNotKey) {
                            new SendNotification(_username + " has started the trip, you are now able to track them", "Student Carpooling", notKey);
                        }}
                    PassengerNotKey.clear();
                    Toast.makeText(DriverTripItem.this, "" + PassengerNotKey.size(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DriverTripItem.this, StartTrip.class);
                    intent.putExtra("DLat",dlat);
                    intent.putExtra("DLon", dlon);
                    intent.putExtra("TripID", TripID);
                    intent.putExtra("Username", _username);
                    startActivity(intent);
                } else

                {
                    Toast.makeText(DriverTripItem.this, "This trip doesn't have any passengers", Toast.LENGTH_SHORT).show();
                }}

            });

        }



    private void getPassengers() {

        try {
            DatabaseReference PassengersID = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID).child("Passengers");
            //get each id in the passenger child and use that id to query the users db to get more information
            PassengersID.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot id : dataSnapshot.getChildren()) {
                            final String passengerKey = id.getKey();
                            PassengerInfo(passengerKey);
                            //create an array list of passengers notification keys to send message when driver cancels or starts the trip


                        }}}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }catch (Exception e){
            //if data doesnt exist - empty recycler view -> no passengers yet
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }


    }


    private void PassengerInfo(final String ID){
        DatabaseReference PassengerInfo = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID).child("Passengers").child(ID);
        PassengerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get lon and lat
                if (dataSnapshot.exists()) {
                    //get the info and create a new user object
                    //required -> Id, profilepicurl, username
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("lat") != null) {
                        Lat = Float.parseFloat(map.get("lat").toString());

                    }
                    if (map.get("lon") != null) {
                        Lon = Float.parseFloat(map.get("lon").toString());

                    }
                    PassengerUserInfo(ID,Lat,Lon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void PassengerUserInfo(final String PassID,final float Lat,final float Lon){

        DatabaseReference PassengerInfo = FirebaseDatabase.getInstance().getReference().child("users").child(PassID);
        PassengerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //get the info and create a new user object
                    //required -> Id, profilepicurl, username
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("profileImageUrl") != null) {
                        profilePicurl = map.get("profileImageUrl").toString();
                        //Toast.makeText(DriverTripItem.this, ""+profilePicurl, Toast.LENGTH_SHORT).show();

                    }
                    if (map.get("Username") != null) {
                        UserName = map.get("Username").toString();

                    }
                    if (map.get("NotificationKey") != null) {
                        NotificationKey = map.get("NotificationKey").toString();

                    }
                    if (map.get("Name") != null) {
                        Name = map.get("Name").toString();
                    }
                    if (map.get("Surname") != null) {
                        Surname = map.get("Surname").toString();
                    }

                    Fullname = Name + " " + Surname;


                    Passenger object = new Passenger("Driver",_username,TripID,dlat,dlon,Fullname,PassID,profilePicurl,UserName,Lat,Lon,NotificationKey);
                    PassengerNotKey.add(NotificationKey);
                    resultsPassengers.add(object);
                    passengerAdapter.notifyDataSetChanged();
                    passengerCount++;

                    if(passengerCount ==0 ){

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


    private ArrayList resultsSeats = new ArrayList<Seat>();

    private List<Seat> getDataSeat() {
        return resultsSeats;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {

            case R.id.action_settings:
                AlertDialog.Builder dialog = new AlertDialog.Builder(DriverTripItem.this);
                dialog.setTitle("Are you sure you want to delete your account?");
                dialog.setMessage("By Doing this.....");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //is deleted
                                    Toast.makeText(DriverTripItem.this,"Account Successfully deleted",Toast.LENGTH_LONG).show();
                                    UserDb.removeValue();
                                    Intent intent = new Intent(DriverTripItem.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(DriverTripItem.this,"Account couldn't be deleted at this time",Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void HideDetails(){
        //once this trip is completed, hide the buttons -- cancel start and view requests...
        cancel.setVisibility(View.GONE);
        request.setVisibility(View.GONE);
        start.setVisibility(View.GONE);

        //how to stop the driver from seeing the passengers locations??
    }

    @Override
    public void onResume() {
        super.onResume();
        resultsPassengers.clear();

    }


}
