package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.student_carpooling.findTripsRecyclerView.FindTrip;
import com.example.student_carpooling.findTripsRecyclerView.FindTripAdapter;
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
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FilteredTrips extends AppCompatActivity {


    private RecyclerView tripRecyclerView;
    private RecyclerView.Adapter FiltertripAdapter;
    private String UserID,Day, Destination,Starting, LuggageCheck, First, Surname, Fullname, Note, Time, UserName, DriverProfilePicUrl;
    private Date tripdate;
    private int Started, Cancelled,Seat;
    private String DriverKey;
    private FirebaseUser CurrentUser;

    private String inputDst, inputStart, inputDate, inputLuggage, drivername;
    private long mili,mili2;
    private TextView textView1, textView2;
    private Button createRequest;
    private ArrayList<String> declined;
    private ArrayList<String> passengers;
    private ArrayList<String> drivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_trips2);

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

        Intent intent = getIntent();
        inputDate = intent.getStringExtra("Date");
        inputStart = intent.getStringExtra("Starting");
        inputDst = intent.getStringExtra("Destination");
        inputLuggage = intent.getStringExtra("Luggage");
        drivername = intent.getStringExtra("Driver");

        drivers = new ArrayList<>();
        declined = new ArrayList<>();
        passengers = new ArrayList<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();

        if(CurrentUser != null){
            UserID = CurrentUser.getUid();
        }

        textView1 = findViewById(R.id.Text);
        textView2 = findViewById(R.id.Text2);
        createRequest = findViewById(R.id.Request);

        tripRecyclerView = findViewById(R.id.FilterTripsRecycler);
        tripRecyclerView.setNestedScrollingEnabled(true); //not true?
        tripRecyclerView.setHasFixedSize(true);
        FiltertripAdapter = new FindTripAdapter(getDataFilterTrips(), FilteredTrips.this);
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(FilteredTrips.this));
        tripRecyclerView.setAdapter(FiltertripAdapter);

        drivers.clear();
        getDriverId();


        //for trip in results:.. if in declined... remove.. notify change


    }


    //first set up onclick for the button in the recycler view


    private void getDriverId() {
        final DatabaseReference DriverID = FirebaseDatabase.getInstance().getReference().child("TripForms");
        DriverID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        DriverKey = id.getKey();
                        getTripIds(DriverKey);

                    }
                }
                else{
                    textView1.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    createRequest.setVisibility(View.VISIBLE);
                    createRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FilteredTrips.this,PassengerCreateRequests.class);
                            startActivity(intent);
                            finish();

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTripIds(final String Key) {
        DatabaseReference TripIDs = FirebaseDatabase.getInstance().getReference().child("TripForms").child(Key);
        TripIDs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //if there is any info there
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        //then get the info under that unique ID
                        String TripKey = id.getKey();
                        if(declined.size()>0){
                            declined.clear();
                        }
                        if(passengers.size()>0){
                            passengers.clear();
                        }
                        //getDeclinedList(Key,TripKey);
                        UserTripDB(Key, TripKey);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void UserTripDB(final String Key, final String ID) {
        DatabaseReference TripsDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(Key).child(ID);
        //Drivers full name is stored within "users"
        if(passengers.size() > 0){
            passengers.clear();
        }
        getPassengerList(Key,ID);
        if(declined.size() > 0){
            declined.clear();
        }
        getDeclinedList(Key,ID);
        DatabaseReference UserDB = FirebaseDatabase.getInstance().getReference().child("users").child(Key);
        UserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator );

                    if (map != null) {
                        if (map.get("Name") != null) {
                            String name = (String) map.get("Name");
                            if (name != null) {
                                First = name.substring(0, 1).toUpperCase() + name.substring(1);
                            }
                        }
                        if (map.get("Surname") != null) {
                            String surname = (String) map.get("Surname");
                            if (surname != null) {
                                Surname = surname.substring(0, 1).toUpperCase() + surname.substring(1);
                            }
                        }

                        if (map.get("profileImageUrl") != null) {
                            DriverProfilePicUrl = (String) map.get("profileImageUrl");
                        }

                        if (map.get("Username") != null) {
                            UserName = (String) map.get("Username");
                            drivers.add(UserName);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        TripsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                    if (map != null) {
                        if (map.get("Time") != null) {
                            Time = (String) map.get("Time");
                        }

                        //check that none of them are null
                        if (map.get("Date") != null) {
                            Day = (String) map.get("Date");
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                            try {

                                String dateStr = Day + " " + Time;
                                Date Datee = format.parse(dateStr);
                                long mili = Datee.getTime();
                                tripdate = new Date(mili);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        if (map.get("Seats") != null) {
                            String SeatStr = Objects.requireNonNull(map.get("Seats")).toString();
                            Seat = (Integer.parseInt(SeatStr));
                        }

                        if (map.get("Luggage") != null) {
                            LuggageCheck = (String) map.get("Luggage");
                        }
                        if (map.get("Note") != null) {
                            Note = (String) map.get("Note");
                        }
                        if (map.get("Starting") != null) {
                            Starting = (String) map.get("Starting");
                        }

                        if (map.get("Destination") != null) {
                            Destination = (String) map.get("Destination");
                        }

                        if (map.get("Started") != null) {
                            String StartedStr = Objects.requireNonNull(map.get("Started")).toString();
                            Started = (Integer.parseInt(StartedStr));
                        }

                        if (map.get("Cancelled") != null) {
                            String CancelledStr = Objects.requireNonNull(map.get("Cancelled")).toString();
                            Cancelled = (Integer.parseInt(CancelledStr));
                        }

                    }
                    Date rightNow = Calendar.getInstance().getTime();

                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                    try {

                        String dateStr = Day + " " + Time;
                        Date Datee = format.parse(dateStr);
                        long mili = Datee.getTime();
                        tripdate = new Date(mili);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //if (!declined.contains(CurrentUser.getUid())){
                    //continue
                    //not passenger, show the trip


                    Fullname = First + " " + Surname;
                    //if(passengers.size() >0){
                    //    Id = passengers.get(0);
                    // }
                    // if(declined.size() >0){
                    //    Un = declined.get(0);
                    // }
                    //user can put in blank and leave all trip going to dst, and dont have to specific a starting point

                    Toast.makeText(FilteredTrips.this, ""+passengers.size(), Toast.LENGTH_SHORT).show();

                    if (!Key.equals(UserID)) {
                        if (!passengers.contains(UserID)) {
                            if (!TextUtils.isEmpty(drivername)) {
                                if (rightNow.before(tripdate)) {
                                    if ((Seat) != 0) {
                                        if (UserName.equals(drivername)) {
                                            FindTrip object = new FindTrip(UserID, ID, Fullname, UserName, DriverProfilePicUrl, Time, Day, Starting, Destination, String.valueOf(Seat), LuggageCheck, Note, Key);
                                            results.add(object);
                                            results.sort(new Comparator<FindTrip>() {
                                                @Override
                                                public int compare(FindTrip o1, FindTrip o2) {
                                                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                                                    String date1 = o1.getDate() + " " + o1.getTime();
                                                    String date2 = o2.getDate() + " " + o2.getTime();
                                                    try {
                                                        Date Date1 = format.parse(date1);
                                                        Date Date2 = format.parse(date2);
                                                        mili = Date1.getTime();
                                                        mili2 = Date2.getTime();
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Date datenew1 = new Date(mili);
                                                    Date datenew2 = new Date(mili2);
                                                    return datenew1.compareTo(datenew2);

                                                }
                                            });

                                            FiltertripAdapter.notifyDataSetChanged();

                                        }
                                    }

                                }

                            } else if (TextUtils.isEmpty(drivername)) {
                                if (Starting.contains(inputStart) || TextUtils.isEmpty(inputStart)) {
                                    if (Destination.contains(inputDst) || TextUtils.isEmpty(inputDst)) {
                                        if (LuggageCheck.equals(inputLuggage)) {
                                            if (Day.equals(inputDate) || inputDate == null) {
                                                if (rightNow.before(tripdate)) {
                                                    if (Cancelled != 1) {
                                                        if (Started != 1) {
                                                            if (((Seat) != 0)) {
                                                                //if(Id.length() == 0 && Un.length() == 0){


                                                                FindTrip object = new FindTrip(UserID, ID, Fullname, UserName, DriverProfilePicUrl, Time, Day, Starting, Destination, String.valueOf(Seat), LuggageCheck, Note, Key);

                                                                results.add(object);
                                                                results.sort(new Comparator<FindTrip>() {
                                                                    @Override
                                                                    public int compare(FindTrip o1, FindTrip o2) {
                                                                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                                                                        String date1 = o1.getDate() + " " + o1.getTime();
                                                                        String date2 = o2.getDate() + " " + o2.getTime();
                                                                        try {
                                                                            Date Date1 = format.parse(date1);
                                                                            Date Date2 = format.parse(date2);
                                                                            mili = Date1.getTime();
                                                                            mili2 = Date2.getTime();
                                                                        } catch (ParseException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        Date datenew1 = new Date(mili);
                                                                        Date datenew2 = new Date(mili2);
                                                                        return datenew1.compareTo(datenew2);

                                                                    }
                                                                });

                                                                FiltertripAdapter.notifyDataSetChanged();


                                                                //declined.clear();
                                                                // passengers.clear();
                                                                //Toast.makeText(FilteredTrips.this, ""+results.size(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }

                                                }


                                            }


                                        }
                                    }

                                }


                                if (!drivers.contains(drivername) && !TextUtils.isEmpty(drivername)) {
                                    Toast.makeText(FilteredTrips.this, "" + drivername, Toast.LENGTH_SHORT).show();
                                    //show empty recycler view for driver
                                    tripRecyclerView.setVisibility(View.GONE);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("That username does not exist!");
                                }
                                if (TextUtils.isEmpty(drivername) && results.isEmpty()) {
                                    tripRecyclerView.setVisibility(View.GONE);
                                    textView1.setVisibility(View.VISIBLE);
                                    textView2.setVisibility(View.VISIBLE);
                                    createRequest.setVisibility(View.VISIBLE);


                                }


                            }
                        }else{
                            passengers.clear();
                        }

                    }
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private ArrayList<FindTrip> results = new ArrayList<>();

    private ArrayList<FindTrip> getDataFilterTrips() {
        return results;

    }

    private void addToList(String id){
        declined.add(id);

    }


    @Override
    public void onResume() {
        super.onResume();
        //results.clear();

    }

    private void getDeclinedList(String Key,String TripKey) {
        //get those declined and add to list
        DatabaseReference declinedCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(Key).child(TripKey).child("Declined");
        //requestCheck.
        declinedCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        String KeyCheck = id.getKey();
                        if(KeyCheck!=null) {
                            if (KeyCheck.equals(CurrentUser.getUid())) {
                                addToList(KeyCheck);
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
    private void getPassengerList(String Key,String TripKey) {
        //get those declined and add to list
        DatabaseReference passengerCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(Key).child(TripKey).child("Passengers");
        //requestCheck.
        passengerCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        String KeyCheck = id.getKey();
                        if(KeyCheck!=null) {
                            if (KeyCheck.equals(CurrentUser.getUid())) {
                                passengers.add(KeyCheck);
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
}