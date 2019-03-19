package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.student_carpooling.passengerRecyclerView.Passenger;
import com.example.student_carpooling.passengerRecyclerView.PassengerAdapter;
import com.example.student_carpooling.seatRecyclerView.Seat;
import com.example.student_carpooling.seatRecyclerView.SeatAdapter;
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

public class DriverTripItem extends AppCompatActivity {

    private TextView cancelledTV,PassengerText,PassengerCount;
    private String UserID;
    private ImageView delete;
    private String TripID, UserName, profilePicurl, NotificationKey, Name,Surname,Fullname, DriverNotKey;
    private float Lat,Lon, dlat, dlon;
    private FirebaseUser CurrentUser;
    private String Seats;
    private long mili, mili2;
    private String _username,driverUrl;
    private Date rightNow,tripdate,expiredTripdate;
    int started,passengerCount=0;

    private RecyclerView recyclerView, SeatrecyclerView;
    private RecyclerView.Adapter passengerAdapter, seatsAdapter;

    private Button cancel, request, start;

    private ArrayList<String> PassengerNotKey = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_trip_item);


        cancelledTV = findViewById(R.id.cancelled);
        PassengerText = findViewById(R.id.passengerText);
        PassengerCount = findViewById(R.id.Text);
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
        CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser != null){
            UserID = CurrentUser.getUid();
        }

        DatabaseReference UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        UserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {
                        if (map.get("NotificationKey") != null) {
                            DriverNotKey = (String) map.get("NotificationKey");
                        }
                        if (map.get("profileImageUrl") != null) {
                            driverUrl = (String) map.get("profileImageUrl");
                        }
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
        recyclerView.setAdapter(passengerAdapter);

        //recycler view for the seats -- show the no in real times once a new passenger is accepted or one is removed
        SeatrecyclerView = findViewById(R.id.seatsRecycler);
        SeatrecyclerView.setNestedScrollingEnabled(false);
        SeatrecyclerView.setHasFixedSize(true);
        SeatrecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        seatsAdapter = new SeatAdapter(getDataSeat(), DriverTripItem.this);
        SeatrecyclerView.setAdapter(seatsAdapter);

        TextView starting, destination, time ,date;
        starting = findViewById(R.id.Starting);
        destination = findViewById(R.id.Destination);
        date = findViewById(R.id.Date);
        time = findViewById(R.id.Time);

        cancel = findViewById(R.id.Cancel);
        request = findViewById(R.id.Requests);
        start = findViewById(R.id.Start);
        delete = findViewById(R.id.delete);

        Intent intent = getIntent();

        final String _starting = intent.getStringExtra("Starting");
        final String _destination = intent.getStringExtra("Destination");
        final String _date = intent.getStringExtra("Date");
        final String _time = intent.getStringExtra("Time");
        _username = intent.getStringExtra("Username");

        dlat = intent.getFloatExtra("DstLat",0);
        dlon = intent.getFloatExtra("DstLon",0);


        rightNow = Calendar.getInstance().getTime();
        long now = rightNow.getTime();

        //add a timer to remind the driver their trip is to start in an hour
        //add a timer to remind driver to start this trip

        TripID = intent.getStringExtra("TripID");

        starting.setText(_starting);
        destination.setText(_destination);
        date.setText(_date);
        time.setText(_time);
        DatabaseReference StartedDb = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
        StartedDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                if(map != null){

                if (map.get("Completed") != null) {
                    String completedStr = Objects.requireNonNull(map.get("Completed")).toString();
                    int completed = (Integer.parseInt(completedStr));
                    if(completed ==1){
                        cancel.setVisibility(View.INVISIBLE);
                        request.setVisibility(View.INVISIBLE);
                        start.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        SeatrecyclerView.setVisibility(View.GONE);
                        PassengerText.setVisibility(View.GONE);
                        cancelledTV.setVisibility(View.VISIBLE);
                        cancelledTV.setText("This trip has been completed");
                        delete.setVisibility(View.VISIBLE);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog();
                            }
                        });
                    }
                }


                if (map.get("Cancelled") != null) {
                    String cancelledStr = Objects.requireNonNull(map.get("Cancelled")).toString();
                    int cancelled = (Integer.parseInt(cancelledStr));
                    if(cancelled == 1){
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
                    String startedStr = Objects.requireNonNull(map.get("Started")).toString();
                    started = (Integer.parseInt(startedStr));
                    if (started == 0) {
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

                        if (rightNow.after(expiredTripdate)) {
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
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog();
                                }
                            });

                        }
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
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                    if(map != null) {
                        if (map.get("Seats") != null) {
                            Seats = Objects.requireNonNull(map.get("Seats")).toString();
                            resultsSeats.clear();
                            //seatsAdapter.notifyDataSetChanged();

                            Seat object = new Seat(Seats);
                            resultsSeats.add(object);
                            seatsAdapter.notifyDataSetChanged();


                        }
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
                CancelDialog();
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if(started == 0){
                    Intent intent = new Intent(DriverTripItem.this, TripRequests.class);
                    intent.putExtra("TripID", TripID);
                    startActivity(intent);//}

                   // else{
                    //    Toast.makeText(DriverTripItem.this, "This carpool has already started, you cannot accept any more new passengers", Toast.LENGTH_SHORT).show();
                   // }
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
                new Notification("Your trip to "+_destination+ " starts in an hour", "Student Carpooling",DriverNotKey);
                //new Notification to passengers
                for (String notKey : PassengerNotKey) {
                    new Notification("Your trip to "+_destination+ " starts in an hour", "Student Carpooling", notKey);
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
                new Notification("Your trip to "+_destination+ " is scheduled to start, please start the trip", "Student Carpooling",DriverNotKey);

            }
        };

        countDownTimer2.start();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
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

                    final AlertDialog dialogBuilder = new AlertDialog.Builder(DriverTripItem.this).create();
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog, null);
                    TextView Text = dialogView.findViewById(R.id.Text);
                    Text.setText("You trip isn't scheduled to start yet, are you sure you wish to continue?");
                    Button Submit = dialogView.findViewById(R.id.Submit);


                    Submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (started == 0) {
                                if (PassengerNotKey.size() > 0) {
                                    for (String notKey : PassengerNotKey) {
                                        new Notification(_username + " has started the trip, you are now able to track them", "Student Carpooling", notKey);
                                    }
                                }
                            }
                            DatabaseReference StartUpdate = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
                            StartUpdate.child("Started").setValue(1);
                            startTrip();
                            finish();
                            dialogBuilder.dismiss();
                        }
                    });

                    dialogBuilder.setView(dialogView);
                    dialogBuilder.show();
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
                                        new Notification(_username + " has started the trip, you are now able to track them", "Student Carpooling", notKey);
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

    private void CancelDialog(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(DriverTripItem.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        TextView Text = dialogView.findViewById(R.id.Text);
        Text.setText("Please message your passengers informing them why you wish to cancel as all participating passengers will be notified of this cancellation and will be able to review your account.Also, you will no longer have access to this trips' passengers ");
        Button Submit = dialogView.findViewById(R.id.Submit);


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference TripDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
                //TripDB.removeValue();
                //DatabaseReference NewDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
                TripDB.child("Cancelled").setValue(1);
                Toast.makeText(DriverTripItem.this, "This trip has been cancelled", Toast.LENGTH_LONG).show();
                //send notification to passengers of the cancellation
                for(String key : PassengerNotKey){
                    new Notification(_username+" has cancelled your trip, click on the trip details to leave a review", "Student Carpooling",key);
                }
                Intent intent = new Intent(DriverTripItem.this, DriverTrips.class);
                startActivity(intent);
                finish();
                dialogBuilder.dismiss();

    }
});
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

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


                        }
                    }
                    else{
                        PassengerCount.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }catch (Exception e){
            //if data doesnt exist - empty recycler view -> no passengers yet
            recyclerView.setVisibility(View.GONE);
            PassengerCount.setVisibility(View.VISIBLE);
        }


    }
    private void showDialog() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(DriverTripItem.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog, null);
                TextView Text = dialogView.findViewById(R.id.Text);
                Text.setText("By deleting this trip, you will no longer be able to view it's information. Are you sure you wish to continue? ");
                Button Submit = dialogView.findViewById(R.id.Submit);


                Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference trips = FirebaseDatabase.getInstance().getReference().child("TripForms").child(CurrentUser.getUid()).child(TripID);
                        trips.child("Deleted").setValue(1);

                        Intent intent = new Intent(DriverTripItem.this,DriverTrips.class);
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


    private void PassengerInfo(final String ID){
        DatabaseReference PassengerInfo = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID).child("Passengers").child(ID);
        PassengerInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get lon and lat
                if (dataSnapshot.exists()) {
                    //get the info and create a new user object
                    //required -> Id, profilepicurl, username
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
                        PassengerUserInfo(ID, Lat, Lon);
                    }
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
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                    if(map != null) {
                        if (map.get("profileImageUrl") != null) {
                            profilePicurl = (String) map.get("profileImageUrl");
                        }
                        if (map.get("Username") != null) {
                            UserName = (String) map.get("Username");

                        }
                        if (map.get("NotificationKey") != null) {
                            NotificationKey = (String) map.get("NotificationKey");

                        }
                        if (map.get("Name") != null) {
                            Name = (String) map.get("Name");
                        }
                        if (map.get("Surname") != null) {
                            Surname = (String) map.get("Surname");
                        }

                        Fullname = Name + " " + Surname;

                        Passenger object = new Passenger("Driver", _username, TripID, dlat, dlon, Fullname, PassID, profilePicurl, UserName,Lat, Lon, NotificationKey);
                        PassengerNotKey.add(NotificationKey);
                        resultsPassengers.add(object);
                        passengerAdapter.notifyDataSetChanged();
                        passengerCount++;

                        if (passengerCount == 0) {

                            PassengerCount.setVisibility(View.VISIBLE);
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


    private ArrayList<Seat> resultsSeats = new ArrayList<>();
    private List<Seat> getDataSeat() {
        return resultsSeats;
    }


    @Override
    public void onResume() {
        super.onResume();

    }


}
