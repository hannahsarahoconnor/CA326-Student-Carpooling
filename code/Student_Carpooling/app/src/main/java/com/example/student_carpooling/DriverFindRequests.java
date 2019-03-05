package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.tripRequestsRecyclerView.RequestTrip;
import com.example.student_carpooling.tripRequestsRecyclerView.RequestTripAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DriverFindRequests extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView NUsername, Nemail;
    private String ProfilePicUrl;
    private FirebaseAuth mAuth;
    private DatabaseReference UserDb, reference;

    NavigationView navigationView;
    private ImageView navProfile;

    private RecyclerView tripRecyclerView;
    private RequestTripAdapter tripAdapter;
    private RecyclerView.LayoutManager tripLayoutManager;

    private String UserID,Starting,Date,Destination,Note,Username, Fullname, First, Surname,Time,LuggageCheck,Day,PassengerURL,PassUsername;

    private java.util.Date tripdate;

    private TabLayout tabLayout;
    private ViewPager tabSwitch;
    private TabAdapter tabAdapter;

    FirebaseUser CurrentUser;

    String PassengerKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_find_requests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        UserID = mAuth.getCurrentUser().getUid();
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        getUserDB();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View hView = navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.UsernameNav);
        Nemail = hView.findViewById(R.id.EmailNav);
        navProfile = hView.findViewById(R.id.imageView);

        setupFirebaseListener();



        tripRecyclerView = findViewById(R.id.requestsRecycler);
        tripRecyclerView.setNestedScrollingEnabled(false); //not true?
        tripRecyclerView.setHasFixedSize(true);
        //resultsTrips.clear();
        tripAdapter = new RequestTripAdapter(getDataTrips(),DriverFindRequests.this);
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tripRecyclerView.setAdapter(tripAdapter);

        getPassengerIDs();
    }


    private void getPassengerIDs(){

        final DatabaseReference PassengerID = FirebaseDatabase.getInstance().getReference().child("TripRequests");
        PassengerID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        PassengerKey = id.getKey();
                        getRequestIds(PassengerKey);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRequestIds(final String PassID){
        DatabaseReference TripIDs = FirebaseDatabase.getInstance().getReference().child("TripRequests").child(PassID);
        TripIDs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //if there is any info there
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        //then get the info under that unique ID
                        String RequestKey = id.getKey();
                        getRequestData(PassID,RequestKey);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRequestData(final String PassID, final String RequestID){
        DatabaseReference RequestIDs = FirebaseDatabase.getInstance().getReference().child("TripRequests").child(PassID).child(RequestID);
        RequestIDs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("Time") != null) {
                        Time = map.get("Time").toString();
                    }

                    //check that none of them are null
                    if (map.get("Date") != null) {
                        Day = map.get("Date").toString();
                    }
                    if (map.get("Fullname") != null) {
                        Fullname = map.get("Fullname").toString();
                    }

                    if (map.get("Luggage") != null) {
                        LuggageCheck = map.get("Luggage").toString();
                    }
                    if (map.get("Note") != null) {
                        Note = map.get("Note").toString();
                    }
                    if (map.get("Starting") != null) {
                        Starting = map.get("Starting").toString();
                    }

                    if (map.get("Destination") != null) {
                        Destination = map.get("Destination").toString();
                    }

                    if (map.get("Username") != null) {
                        PassUsername = map.get("Username").toString();
                    }

                    if (map.get("ProfilePic") != null) {
                        PassengerURL = map.get("ProfilePic").toString();
                    }

                    if (!PassID.equals(UserID)) {
                        Date rightNow = Calendar.getInstance().getTime();

                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                        try {

                            String dateStr = Day + " " + Time;
                            Date Datee = format.parse(dateStr);
                            long mili = Datee.getTime();
                            tripdate = new Date(mili);
                            //Toast.makeText(FilteredTrips.this, "t:"+tripdate, Toast.LENGTH_SHORT).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //only show upcoming requests

                        if (rightNow.before(tripdate)) {

                            RequestTrip object = new RequestTrip(RequestID,"Driver",Note, PassUsername, PassID,PassengerURL, Day, Time,Fullname, LuggageCheck, Starting, Destination);
                            //sort the trips based on their date in milisec


                            //the trip is expired after 5 hours after if not started
                            resultsTrips.add(object);
                            resultsTrips.sort(new Comparator<RequestTrip>() {
                                @Override
                                public int compare(RequestTrip o1, RequestTrip o2) {
                                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                                    String date1 = o1.getDate() + " " + o1.getTime();
                                    String date2 = o2.getDate() + " " + o2.getTime();
                                    Date Date1 = null;
                                    Date Date2 = null;
                                    try {
                                        Date1 = format.parse(date1);
                                        Date2 = format.parse(date2);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long mili = Date1.getTime();
                                    long mili2 = Date2.getTime();
                                    Date datenew1 = new Date(mili);
                                    Date datenew2 = new Date(mili2);


                                    return datenew2.compareTo(datenew1);

                                }
                            });
                            tripAdapter.notifyDataSetChanged();
                        }


                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private ArrayList resultsTrips = new ArrayList<RequestTrip>();

    private ArrayList<RequestTrip> getDataTrips() {
        return resultsTrips;

    }


    //This is required to reset the Recycler View, otherwise each time the trips available will duplicate
    @Override
    public void onResume() {
        super.onResume();
        //resultsTrips.clear();

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
        getMenuInflater().inflate(R.menu.driver_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {

            case R.id.action_settings:
                AlertDialog.Builder dialog = new AlertDialog.Builder(DriverFindRequests.this);
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
                                    Toast.makeText(DriverFindRequests.this,"Account Successfully deleted",Toast.LENGTH_LONG).show();
                                    UserDb.removeValue();
                                    Intent intent = new Intent(DriverFindRequests.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(DriverFindRequests.this,"Account couldn't be deleted at this time",Toast.LENGTH_LONG).show();
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

        switch (id) {
            case R.id.nav_message:
                Intent msg = new Intent(DriverFindRequests.this, DriverMessage.class);
                startActivity(msg);
                break;

            case R.id.nav_profile:
                Intent profile = new Intent(DriverFindRequests.this, DriverProfile.class);
                startActivity(profile);
                break;

            case R.id.nav_sign_out:
                FirebaseAuth.getInstance().signOut();

            case R.id.nav_create_trips:
                Intent create = new Intent(DriverFindRequests.this, DriverCreate.class);
                startActivity(create);
                break;

            case R.id.nav_my_trips:
                Intent trips = new Intent(DriverFindRequests.this, DriverTrips.class);
                startActivity(trips);
                break;

            case R.id.nav_find_trips_requests:
                Intent requests = new Intent(DriverFindRequests.this, DriverFindRequests.class);
                startActivity(requests);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                    Toast.makeText(DriverFindRequests.this, "Sign Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DriverFindRequests.this, MainActivity.class);
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

    private void getUserDB(){
        UserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //makes sure the data is present, else the app will crash if not
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0){
                    //data originally added is kept in this format
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map.get("Username")!=null){
                        String DBUsername = map.get("Username").toString();
                        NUsername.setText(DBUsername);
                    }
                    if(map.get("profileImageUrl")!=null){
                        ProfilePicUrl = map.get("profileImageUrl").toString();
                        if(!ProfilePicUrl.equals("defaultPic")) {
                            Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);}
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //not needed
            }
        });
    }

}
