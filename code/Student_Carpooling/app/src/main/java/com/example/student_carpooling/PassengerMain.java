package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class PassengerMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ImageView navProfile;
    private TextView NUsername, Nemail,Welcome;
    private String UserID,DBUsername,ProfilePicUrl, Day,Time;
    private DatabaseReference UserDb;
    private int count = 0;
    private Date tripdate;
    private FirebaseUser CurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
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


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        if (CurrentUser != null) {
            UserID = CurrentUser.getUid();
            UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
            getUserDB();

            OneSignal.startInit(this).init();
            //notify one signal that the user wishes to receives notifications
            OneSignal.setSubscription(true);
            //get key and add unique key to user database for that user
            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(UserID).child("NotificationKey").setValue(userId);

                }
            });
            //show notification in topbar
            OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        }

        setupFirebaseListener();



        TextView TodayDate = findViewById(R.id.todaydate);
        Welcome = findViewById(R.id.welcome);

        //show current date on screen
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        TodayDate.setText(currentDate);



    }

    //Navigation Drawer built in Functions

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
                AlertDialog.Builder dialog = new AlertDialog.Builder(PassengerMain.this);
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
                                    Toast.makeText(PassengerMain.this,"Account Successfully deleted",Toast.LENGTH_LONG).show();
                                    UserDb.removeValue();
                                    Intent intent = new Intent(PassengerMain.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(PassengerMain.this,"Account couldn't be deleted at this time",Toast.LENGTH_LONG).show();
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

            case R.id.help:
                Intent intent = new Intent(PassengerMain.this,PassengerHelp.class);
                startActivity(intent);
                break;

            case R.id.contact:
                ContactAdmin();
                break;
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
                Intent msg = new Intent(PassengerMain.this, PassengerMessage.class);
                startActivity(msg);
                break;

            case R.id.pass_profile:
                Intent profile = new Intent(PassengerMain.this, PassengerProfile.class);
                startActivity(profile );
                break;

            case R.id.pass_sign_out:
                FirebaseAuth.getInstance().signOut();

            case R.id.pass_find_trips:
                Intent create = new Intent(PassengerMain.this,FindTrips.class);
                startActivity(create);
                break;

            case R.id.pass_trips:
                Intent trips = new Intent(PassengerMain.this,PassengerTrips.class);
                startActivity(trips);
                break;

            case R.id.create_request:
                Intent requests = new Intent(PassengerMain.this,PassengerCreateRequests.class);
                startActivity(requests);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ContactAdmin(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(PassengerMain.this).create();
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
                Intent intent1 = new Intent(PassengerMain.this,ChatActivity.class);
                intent1.putExtra("Username","StudentCarpooling");
                intent1.putExtra("ID",AdminID);
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
    private void getUserDB(){
        UserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //makes sure the data is present, else the app will crash if not
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0){
                    //data originally added is kept in this format
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map != null) {
                        if (map.get("Username") != null) {
                            DBUsername = (String) map.get("Username");
                            NUsername.setText(DBUsername);
                            getTrips(DBUsername);
                        }
                        if (map.get("profileImageUrl") != null) {
                            ProfilePicUrl = (String) map.get("profileImageUrl");
                            if (ProfilePicUrl != null && !ProfilePicUrl.equals("defaultPic")) {
                                Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);
                            }
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

    //get ids of trips that user is a passenger of

    private void getTrips(final String username){
        DatabaseReference getDriverId = FirebaseDatabase.getInstance().getReference().child("users").child(UserID).child("Trips");
        getDriverId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot id : dataSnapshot.getChildren()){
                        final String DriverId = id.getKey();
                        if(DriverId != null){
                        DatabaseReference getTripId = FirebaseDatabase.getInstance().getReference().child("users").child(UserID).child("Trips").child(DriverId);
                        getTripId.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot id : dataSnapshot.getChildren()) {
                                    String TripId = id.getKey();
                                    getTripIds(DriverId,TripId, username);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    }
                }
                else{
                    Welcome.setText("Hello " + username + "!\n\n" + "You have no scheduled trips today");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //show welcome message and number of trips
    private void getTripIds(String DriverID, String TripID, final String Username){
        DatabaseReference TripsDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverID).child(TripID);
        TripsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();


                    if(map != null){
                    //check that none of them are null
                    if (map.get("Date") != null) {
                        Day = (String) map.get("Date");

                    }
                    if (map.get("Time") != null) {
                        Time = (String) map.get("Time");
                    }


                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                    try {

                        String dateStr = Day + " " + Time;
                        Date Date = format.parse(dateStr);
                        long mil = Date.getTime();
                        tripdate = new Date(mil);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Date rightNow = Calendar.getInstance().getTime();


                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(tripdate);
                    cal2.setTime(rightNow);
                    boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

                    if(sameDay && tripdate.after(rightNow)){
                        count++;
                    }


                }
                if(count >0){
                    Welcome.setText("Hello " + Username + "!\n\n" + "You have " + count + " scheduled trips today");
                }
                else{
                    Welcome.setText("Hello " + Username + "!\n\n" + "You have no scheduled trips today");
                }

            }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //check for when user signs out

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
                    Toast.makeText(PassengerMain.this, "Sign Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PassengerMain.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    //Activity listeners
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
}
