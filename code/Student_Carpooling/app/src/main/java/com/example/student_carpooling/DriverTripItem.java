package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import java.sql.Driver;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class DriverTripItem extends AppCompatActivity {


    Toolbar toolbar;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;

    private ImageView navProfile;
    private TextView textView, cancelledTV,PassengerText;
    private String email,UserID;
    private ImageView delete;
    private DatabaseReference UserDb, StartedDb;
    private String TripID, UserName, profilePicurl, NotificationKey, Name,Surname,Fullname, DriverUsername, isStarted,isCompleted,Type, DriverNotKey;

    private float Lat,Lon, dlat, dlon;
    FirebaseUser CurrentUser;
    TextView starting, destination, time ,date, seats, luggage;

    String Seats;

    long mili, mili2,now;

    String _username,driverUrl;

    Date rightNow,tripdate,expiredTripdate;

    Intent intent;

    int completed, started,cancelled, passengerCount=0;

    private LinearLayoutManager passengerLayoutManager,seatsLayoutManager;


    private RecyclerView recyclerView, SeatrecyclerView;
    private RecyclerView.Adapter passengerAdapter, seatsAdapter;

    private Button cancel, request, start;

    private ArrayList<String> PassengerNotKey = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_trip_item);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Hi ");toolbar.setTitleTextColor(Color.WHITE);


        textView = findViewById(R.id.text);
        cancelledTV = findViewById(R.id.cancelled);
        PassengerText = findViewById(R.id.passengerText);

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
        UserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map.get("NotificationKey")!=null){
                        DriverNotKey = map.get("NotificationKey").toString();
                    }
                    if(map.get("profileImageUrl")!=null){
                        driverUrl = map.get("profileImageUrl").toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        delete = findViewById(R.id.deleteTrip);
        intent = getIntent();

        final String _starting = intent.getStringExtra("Starting");
        final String _destination = intent.getStringExtra("Destination");
        final String _date = intent.getStringExtra("Date");
        final String _time = intent.getStringExtra("Time");
        String _seats = intent.getStringExtra("Seats");
        String _luggage = intent.getStringExtra("Luggage");
        _username = intent.getStringExtra("Username");

        dlat = intent.getFloatExtra("DstLat",0);
        dlon = intent.getFloatExtra("DstLon",0);


        rightNow = Calendar.getInstance().getTime();
        now = rightNow.getTime();

        //add a timer to remind the driver their trip is to start in an hour
        //add a timer to remind driver to start this trip

        TripID = intent.getStringExtra("TripID");

        starting.setText(_starting);
        destination.setText(_destination);
        date.setText(_date);
        time.setText(_time);
        StartedDb = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
        StartedDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map.get("Completed") != null) {
                    completed = Integer.parseInt(map.get("Completed").toString());

                    if(Integer.parseInt(map.get("Completed").toString())==1){
                        cancel.setVisibility(View.INVISIBLE);
                        request.setVisibility(View.INVISIBLE);
                        start.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        SeatrecyclerView.setVisibility(View.GONE);
                        PassengerText.setVisibility(View.GONE);
                        cancelledTV.setVisibility(View.VISIBLE);
                        cancelledTV.setText("This trip has been completed");
                        delete.setVisibility(View.VISIBLE);
                    }
                }


                if (map.get("Cancelled") != null) {
                   // cancelled = Integer.parseInt(map.get("Cancelled").toString());

                    if(Integer.parseInt(map.get("Cancelled").toString()) == 1){

                        cancel.setVisibility(View.INVISIBLE);
                        request.setVisibility(View.INVISIBLE);
                        start.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        SeatrecyclerView.setVisibility(View.GONE);
                        PassengerText.setVisibility(View.GONE);
                        cancelledTV.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.VISIBLE);
                    }
                }

                if (map.get("Started") != null) {
                    started = Integer.parseInt(map.get("Started").toString());
                    Toast.makeText(DriverTripItem.this, ""+started, Toast.LENGTH_SHORT).show();
                    if(Integer.parseInt(map.get("Started").toString()) == 0) {
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                        try {
                            //Split the original time and add 5 hours
                            StringTokenizer tokens = new StringTokenizer(_time, ":");
                            int hours = Integer.parseInt(tokens.nextToken()) + 5;
                            String mins = tokens.nextToken();
                            String ExpiredTime = Integer.toString(hours) + ":" + mins;
                            String ExpiredDateStr = _date + " " + ExpiredTime;
                            Date ExpiredDate = format.parse(ExpiredDateStr);
                            long mili2 = ExpiredDate.getTime();
                            expiredTripdate = new Date(mili2);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(rightNow.after(expiredTripdate)){
                            //this trip has expired
                            cancel.setVisibility(View.INVISIBLE);
                            request.setVisibility(View.INVISIBLE);
                            start.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            SeatrecyclerView.setVisibility(View.GONE);
                            PassengerText.setVisibility(View.GONE);
                            cancelledTV.setVisibility(View.VISIBLE);
                            cancelledTV.setText("This trip has expired");
                            delete.setVisibility(View.VISIBLE);

                        }
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
                //replace its content with cancelled --> true
                AlertDialog.Builder dialog = new AlertDialog.Builder(DriverTripItem.this);
                dialog.setTitle("Are you sure you want to delete this trip?");
                dialog.setMessage("Please message your passengers informing them why you wish to cancel as all participating passengers will be notified of this cancellation and will be able to review your account.Also, you will no longer have access to this trips' passengers");
                dialog.setPositiveButton("Cancel Trip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference TripDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
                        //TripDB.removeValue();
                        //DatabaseReference NewDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
                        TripDB.child("Cancelled").setValue(1);
                        Toast.makeText(DriverTripItem.this, "This trip has been cancelled", Toast.LENGTH_LONG).show();
                        //send notification to passengers of the cancellation
                        for(String key : PassengerNotKey){
                            new SendNotification(_username+" has cancelled your trip, click on the trip details to leave a review", "Student Carpooling",key);
                        }
                        Intent intent = new Intent(DriverTripItem.this, DriverTrips.class);
                        startActivity(intent);

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
                    if(started == 0){
                    Intent intent = new Intent(DriverTripItem.this, TripRequests.class);
                    intent.putExtra("TripID", TripID);
                    startActivity(intent);}
                    else{
                        Toast.makeText(DriverTripItem.this, "This carpool has already started, you cannot accept any more new passengers", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        StringTokenizer tokens = new StringTokenizer(_time, ":");
        int hours = Integer.parseInt(tokens.nextToken()) -1;
        String mins = tokens.nextToken();
        String newTime = Integer.toString(hours) + ":" + mins;

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
        try {

            String dateStr = _date + " " + newTime;
            String dateStr2 = _date + " " + _time;
            Date Datee = format.parse(dateStr);
            Date Date = format.parse(dateStr2);
            mili = Datee.getTime();
            mili2 = Date.getTime();
            tripdate = new Date(mili2);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        CountDownTimer countDownTimer = new CountDownTimer(mili-now,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mili = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                new SendNotification("Your trip to "+_destination+ " starts in an hour", "Student Carpooling",DriverNotKey);
                //new SendNotification to passengers
                for (String notKey : PassengerNotKey) {
                    new SendNotification("Your trip to "+_destination+ " starts in an hour", "Student Carpooling", notKey);
                }

            }
        };

        countDownTimer.start();
        CountDownTimer countDownTimer2 = new CountDownTimer(mili2-now,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mili2 = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                new SendNotification("Your trip to "+_destination+ " is scheduled to start, please start the trip", "Student Carpooling",DriverNotKey);

            }
        };

        countDownTimer2.start();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DriverTripItem.this);
                builder.setTitle("Are you sure you want to delete this trip?").setMessage("You will no longer be able to see the details of this trip,are you sure?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int which) {
                        //dont wanna delete it completed as then the passenger cant see, add value -> deleted, so that it wont be show in their page
                        DatabaseReference trips = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUser.getUid()).child(TripID);
                        trips.child("Deleted").setValue(1);

                        Intent intent = new Intent(DriverTripItem.this,DriverTrips.class);
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


        start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(started==1){
                        //continue to activity
                        startTrip();
                    }


                 else if(rightNow.before(tripdate) && started==0){
                        //show dialog confirming if they want to start this trip early
                        AlertDialog.Builder dialog = new AlertDialog.Builder(DriverTripItem.this);
                        dialog.setTitle("Are you sure you want to start this trip early?");
                        dialog.setMessage("You trip isn't scheduled to start yet, are you sure you wish to continue>");
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (started == 0) {
                                    if (PassengerNotKey.size() > 0) {
                                        for (String notKey : PassengerNotKey) {
                                            new SendNotification(_username + " has started the trip, you are now able to track them", "Student Carpooling", notKey);
                                        }
                                    }
                                }
                                DatabaseReference StartUpdate = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
                                StartUpdate.child("Started").setValue(1);
                                startTrip();
                            }


                        });

                        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();

                    }
               else {
                    //only send the notification when the trip first starts, as the driver can click in and out and map without constantly resending the notifications each time
                    // if the trip is past the date/trip, the driver cannot start it..
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DriverTripItem.this);
                    dialog.setTitle("Are you sure you want to start this trip");
                    dialog.setMessage("By starting this trip, all passengers of this trip will be notified and will be able to track your location");
                    dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (started == 0) {
                                if (PassengerNotKey.size() > 0) {
                                    for (String notKey : PassengerNotKey) {
                                        new SendNotification(_username + " has started the trip, you are now able to track them", "Student Carpooling", notKey);
                                    }
                                }
                            }
                            DatabaseReference StartUpdate = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
                            StartUpdate.child("Started").setValue(1);
                            startTrip();
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

                }
                }
            });

    }
    //if an hour after, the trip isnt started.. left users rate??


    private void startTrip(){
        Intent intent = new Intent(DriverTripItem.this, StartTrip.class);
        intent.putExtra("DLat", dlat);
        intent.putExtra("DLon", dlon);
        intent.putExtra("TripID", TripID);
        intent.putExtra("Username", _username);
        intent.putExtra("driverUrl",driverUrl);
        startActivity(intent);
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
        PassengerInfo.addValueEventListener(new ValueEventListener() {
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
        PassengerInfo.addValueEventListener(new ValueEventListener() {
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
       // resultsPassengers.clear();

    }


}
