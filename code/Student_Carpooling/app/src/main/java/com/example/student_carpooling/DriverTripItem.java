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
    private DatabaseReference UserDb;
    private String TripID, UserName, profilePicurl;

    FirebaseUser CurrentUser;
    TextView starting, destination, time ,date, seats, luggage;

    Intent intent;

    private LinearLayoutManager passengerLayoutManager;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter passengerAdapter;

    private Button cancel, request, start;


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
                finish();
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
        TripID = intent.getStringExtra("TripID");

        Toast.makeText(DriverTripItem.this,""+TripID,Toast.LENGTH_LONG).show();

        starting.setText(_starting);
        destination.setText(_destination);
        date.setText(_date);
        time.setText(_time);
        seats.setText(_seats);
        luggage.setText(_luggage);


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
                             Toast.makeText(DriverTripItem.this,"This trip has been deleted",Toast.LENGTH_LONG).show();

                             Intent intent = new Intent(DriverTripItem.this,DriverTrips.class);
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

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void getPassengers() {

        try {
            DatabaseReference PassengersID = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID).child("Passengers");
            //get each id in the passenger child and use that id to query the users db to get more information
            PassengersID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot id : dataSnapshot.getChildren()) {
                            final String passengerKey = id.getKey();
                            DatabaseReference PassengerInfo = FirebaseDatabase.getInstance().getReference().child("users").child(passengerKey);
                            PassengerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        //get the info and create a new user object
                                        //required -> Id, profilepicurl, username
                                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                                        if (map.get("profileImageUrl") != null) {
                                            profilePicurl = map.get("profileImageUrl").toString();

                                        }
                                        if (map.get("Username") != null) {
                                            UserName = map.get("Username").toString();

                                        }

                                        Passenger object = new Passenger(passengerKey, profilePicurl, UserName);
                                        resultsPassengers.add(object);
                                        passengerAdapter.notifyDataSetChanged();


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }



                }

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

    private ArrayList resultsPassengers = new ArrayList<Passenger>();

    private List<Passenger> getDataPassenger() {
        return resultsPassengers;
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

}
