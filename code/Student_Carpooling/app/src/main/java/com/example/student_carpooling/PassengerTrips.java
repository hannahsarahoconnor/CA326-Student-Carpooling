package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class PassengerTrips extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ImageView navProfile;
    private TextView NUsername, Nemail;
    private String UserID;
    private DatabaseReference UserDb;
    private String ProfilePicUrl;
    private String Time,Date,DriverId, TripId,Starting,Destination,DriverUsername,profileImageUrl, Name,Surname,Fullname,NotificationKey;
    private float lat, lon, DstLat,DstLon;
    private FirebaseUser CurrentUser;
    private RecyclerView.Adapter tripAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_trips);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.usernameNav);
        Nemail = hView.findViewById(R.id.emailNav);
        navProfile = hView.findViewById(R.id.ImageView);

        setupFirebaseListener();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser != null){
        UserID = CurrentUser.getUid();
        }
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        getUserDB();

        RecyclerView tripRecyclerView = findViewById(R.id.TripsRecycler);
        tripRecyclerView.setNestedScrollingEnabled(true); //not true?
        tripRecyclerView.setHasFixedSize(true);
        tripAdapter = new PassengerTripAdapter(getDataTrips(),PassengerTrips.this);
        tripRecyclerView.setLayoutManager( new LinearLayoutManager(PassengerTrips.this));
        tripRecyclerView.setAdapter(tripAdapter);


        getDrivers();

    }
    //when driver accepts a passenger add a child to their info -> Trips > driver id > trip id
    private ArrayList<PassengerTrip> trips = new ArrayList<>();
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
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                    if(map != null) {
                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl = (String) map.get("profileImageUrl");
                    }
                    if(map.get("Name")!=null){
                        Name = (String) map.get("Name");
                    }
                    if(map.get("Surname")!=null){
                        Surname = (String) map.get("Surname");
                    }

                    Fullname = Name + " " + Surname;


                }
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
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {
                        if (map.get("lat") != null) {
                            String Lat = Objects.requireNonNull(map.get("lat")).toString();
                            lat = Float.parseFloat(Lat);

                        }
                        if (map.get("lon") != null) {
                            String Lon = Objects.requireNonNull(map.get("lon")).toString();
                            lon = Float.parseFloat(Lon);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getTripInfo(String DriverKey, final String TripKey){
        DatabaseReference TripInfo = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverKey).child(TripKey);
        TripInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                    if(map != null) {

                    if (map.get("Time") != null) {
                        Time = (String) map.get("Time");
                    }

                    //check that none of them are null
                    if (map.get("Date") != null) {
                        Date = (String) map.get("Date");

                    if (map.get("Starting") != null) {
                        Starting = (String) map.get("Starting");
                    }

                    if (map.get("Destination") != null) {
                        Destination = (String) map.get("Destination");
                    }

                   if (map.get("Username") != null) {
                       DriverUsername = (String) map.get("Username");
                    }

                  if (map.get("DstLat") != null) {
                      String lat = Objects.requireNonNull(map.get("DstLat")).toString();
                      DstLat = Float.parseFloat(lat);
                   }

                    if(map.get("DstLon") != null) {
                        String lon = Objects.requireNonNull(map.get("DstLon")).toString();
                        DstLon = Float.parseFloat(lon);
                    }

                        //Toast.makeText(PassengerTrips.this, ""+profileImageUrl, Toast.LENGTH_SHORT).show();

                    PassengerTrip object = new PassengerTrip(NotificationKey,Fullname,profileImageUrl,DriverUsername,TripKey, DriverId, lat,lon,DstLat,DstLon,Starting, Destination,Time,Date);
                    trips.add(object);
                    tripAdapter.notifyDataSetChanged();

                            }}}

    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
                DeleteAccount();
                break;

            case R.id.help:
                Intent intent = new Intent(PassengerTrips.this,PassengerHelp.class);
                startActivity(intent);
                break;

            case R.id.contact:
                ContactAdmins();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                    if(map != null) {
                    if(map.get("Username")!=null){
                        String DBUsername = (String) map.get("Username");
                        NUsername.setText(DBUsername);
                    }
                    if(map.get("profileImageUrl")!=null){
                        ProfilePicUrl = (String) map.get("profileImageUrl");

                        if (ProfilePicUrl != null && !ProfilePicUrl.equals("defaultPic")) {
                            Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);
                        }
                    }

                    if(map.get("NotificationKey")!=null){
                        NotificationKey= (String) map.get("NotificationKey");

                    }


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
    private void DeleteAccount() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(PassengerTrips.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        TextView Text = dialogView.findViewById(R.id.Text);
        TextView Title = dialogView.findViewById(R.id.Title);
        Title.setText("Delete Account");
        Text.setText("By deleting your account, you will no longer be able to sign in and all of your user data will be deleted. If you wish to you use the app again in the future, you must re-register. Are you sure you wish to continue? ");
        Button Submit = dialogView.findViewById(R.id.Submit);


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //is deleted
                            Toast.makeText(PassengerTrips.this, "Account Successfully deleted", Toast.LENGTH_LONG).show();
                            UserDb.removeValue();
                            Intent intent = new Intent(PassengerTrips.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            dialogBuilder.dismiss();
                        } else {
                            Toast.makeText(PassengerTrips.this, "Account couldn't be deleted at this time", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void ContactAdmins(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(PassengerTrips.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        TextView Text = dialogView.findViewById(R.id.Text);
        ImageView warn = dialogView.findViewById(R.id.warn);
        ImageView admin = dialogView.findViewById(R.id.admin);
        TextView Title = dialogView.findViewById(R.id.Title);
        warn.setVisibility(View.GONE);
        admin.setVisibility(View.VISIBLE);
        Title.setText("Contact Admins");
        Text.setText("If you have any further issues or queries regarding this app, please click yes to start a private chat with the admins");
        Button Submit = dialogView.findViewById(R.id.Submit);


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String AdminID = getResources().getString(R.string.AdminID);
                Intent intent1 = new Intent(PassengerTrips.this,ChatActivity.class);
                intent1.putExtra("Username","StudentCarpooling");
                intent1.putExtra("ID", AdminID);
                intent1.putExtra("Fullname","Admins");
                intent1.putExtra("ProfilePicURL","defaultPic");
                startActivity(intent1);
                finish();
                dialogBuilder.dismiss();

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
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
