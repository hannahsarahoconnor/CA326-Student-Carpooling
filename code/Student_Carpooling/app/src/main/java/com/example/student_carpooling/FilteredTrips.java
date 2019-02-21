package com.example.student_carpooling;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.filterTripsRecyclerView.FilterTrip;
import com.example.student_carpooling.filterTripsRecyclerView.FilterTripAdapter;
import com.example.student_carpooling.tripRecyclerView.Trip;
import com.example.student_carpooling.tripRecyclerView.TripAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class FilteredTrips extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Dialog dialog, dialog2;

    private RecyclerView tripRecyclerView;
    private RecyclerView.Adapter FiltertripAdapter;
    private RecyclerView.LayoutManager tripLayoutManager;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView NUsername, Nemail, txt;
    private String ProfilePicUrl,UserID, Date, Destination, Seats, Starting, LuggageCheck, Time, UserName,DriverProfilePicUrl;
    private FirebaseAuth mAuth;
    private String DBUsername;
    private DatabaseReference UserDb, reference;
    Date TripDate,date;
    LinearLayout linearLayout;
    private String DriverKey;
    FirebaseUser CurrentUser;


    Toolbar toolbar=null;



    NavigationView navigationView;
    private ImageView navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_trips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        UserID = mAuth.getCurrentUser().getUid();
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        getUserDB();

        View hView =  navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.usernameNav);
        Nemail = hView.findViewById(R.id.emailNav);
        navProfile = hView.findViewById(R.id.ImageView);

        setupFirebaseListener();

       tripRecyclerView = findViewById(R.id.FilterTripsRecycler);
        tripRecyclerView.setNestedScrollingEnabled(true); //not true?
       tripRecyclerView.setHasFixedSize(true);
        FiltertripAdapter = new FilterTripAdapter(getDataFilterTrips(),FilteredTrips.this);
        tripLayoutManager = new LinearLayoutManager(FilteredTrips.this);
        tripRecyclerView.setLayoutManager(tripLayoutManager);

       tripRecyclerView.setAdapter(FiltertripAdapter);



       dialog = new Dialog(this);
       dialog2 = new Dialog(this);

        getDriverId();
    }

    //first set up onclick for the button in the recycler view

    public void showPopUp(View v){
        TextView ClosePopUp;
        Button Request;
        dialog.setContentView(R.layout.request_popup);
        ClosePopUp = dialog.findViewById(R.id.close);
        Request = (Button) dialog.findViewById(R.id.requestTrip);

        ClosePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send the request to the drawer
            }
        });
        //transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.passenger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {

            case R.id.action_settings:
                AlertDialog.Builder dialog = new AlertDialog.Builder(FilteredTrips.this);
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
                                    Toast.makeText(FilteredTrips.this,"Account Successfully deleted",Toast.LENGTH_LONG).show();
                                    UserDb.removeValue();
                                    Intent intent = new Intent(FilteredTrips.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(FilteredTrips.this,"Account couldn't be deleted at this time",Toast.LENGTH_LONG).show();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.pass_message:
                Intent msg = new Intent(FilteredTrips.this, PassengerMessage.class);
                startActivity(msg);
                break;

            case R.id.pass_profile:
                Intent profile = new Intent(FilteredTrips.this, PassengerProfile.class);
                startActivity(profile );
                break;

            case R.id.pass_sign_out:
                FirebaseAuth.getInstance().signOut();

            case R.id.pass_find_trips:
                Intent create = new Intent(FilteredTrips.this,FindTrips.class);
                startActivity(create);
                break;

            case R.id.pass_trips:
                Intent trips = new Intent(FilteredTrips.this,PassengerTrips.class);
                startActivity(trips);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getUserDB(){
        UserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //makes sure the data is present, else the app will crash if not
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0){
                    //data originally added is kept in this format
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map.get("Username")!=null){
                        DBUsername = map.get("Username").toString();
                        NUsername.setText(DBUsername);
                    }
                    if(map.get("profileImageUrl")!=null){
                        ProfilePicUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //not needed
            }
        });
    }


    public void showDriverPopUp(View v){
        TextView ClosePopUp;
        Button Request;
        dialog2.setContentView(R.layout.driver_popup);
        ClosePopUp = dialog2.findViewById(R.id.close);
        Request = (Button) dialog2.findViewById(R.id.messageDriver);

        ClosePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send the request to the drawer
            }
        });
        //transparent background
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.show();

    }

    private void getDriverId(){
        final DatabaseReference DriverID = FirebaseDatabase.getInstance().getReference().child("TripForms");
        DriverID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        DriverKey = id.getKey();
                        getTripIds(DriverKey);
                        getDriverImage(DriverKey);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTripIds(final String Key){
        DatabaseReference TripIDs = FirebaseDatabase.getInstance().getReference().child("TripForms").child(Key);
        TripIDs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //if there is any info there
                    for(DataSnapshot id : dataSnapshot.getChildren()){
                        //then get the info under that unique ID
                        String TripKey = id.getKey();
                        UserTripDB(TripKey);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDriverImage(String ID){
        DatabaseReference driverDB = FirebaseDatabase.getInstance().getReference().child("users").child(ID);
        driverDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if(!ProfilePicUrl.equals("defaultPic")) {
                        Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UserTripDB(String ID) {
        //push().getKey();

        //get all driver trips
        DatabaseReference TripsDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverKey).child(ID);
        TripsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();


                    //check that none of them are null
                    if (map.get("Date") != null) {
                        Date = map.get("Date").toString();
                        StringTokenizer tokens = new StringTokenizer(Date, "/");
                        Integer day = Integer.parseInt(tokens.nextToken());
                        Integer month = Integer.parseInt(tokens.nextToken());
                        Integer year = Integer.parseInt(tokens.nextToken());
                        //year month date
                        // year in date is saying 3919 rather than 2019

                        Calendar calendar = Calendar.getInstance();

                        TripDate = new Date(year-1900, month - 1, day);
                        String date_n = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(TripDate);

                    }
                    if (map.get("Time") != null) {
                        Time = map.get("Time").toString();
                    }

                    if (map.get("Seats") != null) {
                        Seats = map.get("Seats").toString();
                    }

                    if (map.get("Luggage") != null) {
                        LuggageCheck = map.get("Luggage").toString().toUpperCase();
                    }

                    if (map.get("Starting") != null) {
                        Starting = map.get("Starting").toString().toUpperCase();
                    }

                    if (map.get("Destination") != null) {
                        Destination = map.get("Destination").toString().toUpperCase();
                    }

                    if (map.get("Username") != null) {
                        UserName = map.get("Username").toString();
                    }



                    String pattern = "dd-MM-yy";
                    date = new Date();
                    String Currentdate = new SimpleDateFormat(pattern).format(date);

                    if (!(UserName.equals(DBUsername))) {
                        //make sure its not a past trips or isnt one that was created by that user.

                        int compare = date.compareTo(TripDate);
                        // if date greater, tripdate is a past d


                        if (date.compareTo(TripDate) < 0 && !(date.compareTo(TripDate) > 0)) {
                            //current date is before tripdate

                            //if not empty apply all those that have been entered
                            //getIntent().getStringExtra("Starting");
                           // getIntent().getStringExtra("Destination");
                          //  getIntent().getStringExtra("Date");
                          //  getIntent().getStringExtra("Time");
                           // getIntent().getStringExtra("Luggage");
                            //





                            FilterTrip object = new FilterTrip(DriverProfilePicUrl,Date, Time, Seats, UserName, Starting, Destination);
                            resultsTrips.add(object);
                            FiltertripAdapter.notifyDataSetChanged();
                        }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private ArrayList resultsTrips = new ArrayList<FilterTrip>();
    //private ArrayList resultsTrips;

    private ArrayList<FilterTrip> getDataFilterTrips() {

        //isnt working
       // if(resultsTrips.size() == 0){
          //  txt.setText("There are no matching trips..");
        //}
        return resultsTrips;

    }

    @Override
    public void onResume() {
        super.onResume();
        resultsTrips.clear();

    }


    private void setupFirebaseListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    Nemail.setText(email);
                } else {
                    Toast.makeText(FilteredTrips.this, "Sign Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FilteredTrips.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }

    }
}
