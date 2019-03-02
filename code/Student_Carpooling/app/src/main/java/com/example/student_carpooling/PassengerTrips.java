package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.findTripsRecyclerView.FindTrip;
import com.example.student_carpooling.findTripsRecyclerView.FindTripAdapter;
import com.example.student_carpooling.passengerTripsRecyclerView.PassengerTrip;
import com.example.student_carpooling.passengerTripsRecyclerView.PassengerTripAdapter;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class PassengerTrips extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;

    private ImageView navProfile;
    private TextView NUsername, Nemail;
    private String email,UserID;
    private DatabaseReference UserDb;
    private String ProfilePicUrl;

    String Time,Date,DriverId, TripId,Starting,Destination,DriverUsername,profileImageUrl, Name,Surname,Fullname,NotificationKey;

    float lat, lon, DstLat,DstLon;
    FirebaseUser CurrentUser;

    private RecyclerView tripRecyclerView;
    private RecyclerView.Adapter tripAdapter;
    private RecyclerView.LayoutManager tripLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_trips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.usernameNav);
        Nemail = hView.findViewById(R.id.emailNav);
        navProfile = hView.findViewById(R.id.ImageView);

        setupFirebaseListener();

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        UserID = mAuth.getCurrentUser().getUid();
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        getUserDB();

        tripRecyclerView = findViewById(R.id.TripsRecycler);
        tripRecyclerView.setNestedScrollingEnabled(true); //not true?
        tripRecyclerView.setHasFixedSize(true);
        tripAdapter = new PassengerTripAdapter(getDataTrips(),PassengerTrips.this);
        tripLayoutManager = new LinearLayoutManager(PassengerTrips.this);
        tripRecyclerView.setLayoutManager(tripLayoutManager);
        tripRecyclerView.setAdapter(tripAdapter);


        getDrivers();

    }
    //when driver accepts a passenger add a child to their info -> Trips > driver id > trip id
    private ArrayList trips = new ArrayList<PassengerTrip>();
    private ArrayList<PassengerTrip> getDataTrips() {
        return trips;
    }


    private void getDrivers(){
        DatabaseReference Driverids = FirebaseDatabase.getInstance().getReference().child("users").child(UserID).child("Trips");
        Driverids.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        DriverId = snapshot.getKey();
                        getTrips(DriverId);
                        getDriverInfo(DriverId);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDriverInfo(String ID){
        DatabaseReference DriverPic = FirebaseDatabase.getInstance().getReference().child("users").child(ID);
        DriverPic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl = map.get("profileImageUrl").toString();
                    }
                    if(map.get("Name")!=null){
                        Name = map.get("Name").toString();
                    }
                    if(map.get("Surname")!=null){
                        Surname = map.get("Surname").toString();
                    }

                    Fullname = Name + " " + Surname;


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTrips(final String DriverKey){
        DatabaseReference Tripids = FirebaseDatabase.getInstance().getReference().child("users").child(UserID).child("Trips").child(DriverKey);
        Tripids.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    TripId = snapshot.getKey();
                    getTripInfo(DriverKey,TripId);
                    getCoordinates(DriverKey,TripId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCoordinates(String Driver, String Trip){
        DatabaseReference Coord = FirebaseDatabase.getInstance().getReference().child("TripForms").child(Driver).child(Trip).child("Passengers").child(CurrentUser.getUid());
        Coord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("lat")!=null){
                        lat = Float.parseFloat(map.get("lat").toString());

                    }
                    if(map.get("lon")!=null){
                        lon = Float.parseFloat(map.get("lon").toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getTripInfo(String DriverKey, String TripKey){
        DatabaseReference TripInfo = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverKey).child(TripKey);
        TripInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("Time") != null) {
                        Time = map.get("Time").toString();
                    }

                    //check that none of them are null
                    if (map.get("Date") != null) {
                        Date = map.get("Date").toString();

                    if (map.get("Starting") != null) {
                        Starting = map.get("Starting").toString();
                    }

                    if (map.get("Destination") != null) {
                        Destination = map.get("Destination").toString();
                    }

                   if (map.get("Username") != null) {
                       DriverUsername = map.get("Username").toString();
                    }

                  if (map.get("DstLat") != null) {
                      DstLat = Float.parseFloat(map.get("DstLat").toString());
                   }

                if(map.get("DstLon") != null) {
                    DstLon = Float.parseFloat(map.get("DstLon").toString());
                    }

                    PassengerTrip object = new PassengerTrip(NotificationKey,Fullname,profileImageUrl,DriverUsername,TripId, DriverId, lat,lon,DstLat,DstLon,Starting, Destination,Time,Date);
                    trips.add(object);
                    tripAdapter.notifyDataSetChanged();

                            }}

    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(PassengerTrips.this);
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
                                    Toast.makeText(PassengerTrips.this,"Account Successfully deleted",Toast.LENGTH_LONG).show();
                                    UserDb.removeValue();
                                    Intent intent = new Intent(PassengerTrips.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(PassengerTrips.this,"Account couldn't be deleted at this time",Toast.LENGTH_LONG).show();
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
                Intent msg = new Intent(PassengerTrips.this, PassengerMessage.class);
                startActivity(msg);
                break;

            case R.id.pass_profile:
                Intent profile = new Intent(PassengerTrips.this, PassengerProfile.class);
                startActivity(profile );
                break;

            case R.id.pass_sign_out:
                FirebaseAuth.getInstance().signOut();

            case R.id.pass_find_trips:
                Intent create = new Intent(PassengerTrips.this,FindTrips.class);
                startActivity(create);
                break;

            case R.id.pass_trips:
                Intent trips = new Intent(PassengerTrips.this,PassengerTrips.class);
                startActivity(trips);
                break;

            case R.id.create_request:
                Intent requests = new Intent(PassengerTrips.this,PassengerCreateRequests.class);
                startActivity(requests);
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
                        String DBUsername = map.get("Username").toString();
                        NUsername.setText(DBUsername);
                    }
                    if(map.get("profileImageUrl")!=null){
                        ProfilePicUrl = map.get("profileImageUrl").toString();

                        if(!ProfilePicUrl.equals("defaultPic")) {
                            Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);
                        }
                    }

                    if(map.get("NotificationKey")!=null){
                        NotificationKey= map.get("NotificationKey").toString();

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //not needed
            }
        });
    }

    private void setupFirebaseListener(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    String email = user.getEmail();
                    Nemail.setText(email);
                }
                else{
                    Toast.makeText(PassengerTrips.this, "Sign Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PassengerTrips.this, MainActivity.class);
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
        if(mAuthStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        trips.clear();
    }
}
