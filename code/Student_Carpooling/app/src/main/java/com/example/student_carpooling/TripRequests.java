package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.student_carpooling.messagesRecyclerView.Message;
import com.example.student_carpooling.messagesRecyclerView.MessageAdapter;
import com.example.student_carpooling.requestsRecyclerView.Requests;
import com.example.student_carpooling.requestsRecyclerView.RequestsAdapter;
import com.example.student_carpooling.tripRecyclerView.Trip;
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

public class TripRequests extends AppCompatActivity {

    RecyclerView.Adapter requestAdapter;
    RecyclerView requestRecyclerView;
    LinearLayoutManager requestLayoutManager;

    Intent intent;

    private FirebaseAuth mAuth;
    private String TripId,UserID,profilePicURL,First,Surname,Fullname,Username;
    private DatabaseReference UserDb;
    FirebaseUser CurrentUser;

    float lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //be able to go back out of the activity
                finish();
            }
        });

        intent = getIntent();

        TripId = intent.getStringExtra("TripID");

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        UserID = mAuth.getCurrentUser().getUid();
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);


        requestRecyclerView = findViewById(R.id.requestRecycler);
        requestRecyclerView.setNestedScrollingEnabled(true);
        requestRecyclerView.setHasFixedSize(true);
        requestAdapter = new RequestsAdapter(getDataRequest(), TripRequests.this);
        requestLayoutManager = new LinearLayoutManager(getApplicationContext());
        requestRecyclerView.setAdapter(requestAdapter);

      // Toast.makeText(TripRequests.this, ""+ TripId, Toast.LENGTH_SHORT).show();
        //Toast.makeText(TripRequests.this, ""+UserID, Toast.LENGTH_SHORT).show();
        getRequests();


    }

    private void getRequests(){
        DatabaseReference requestsDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripId).child("TripRequests");
        requestsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //if there is any info there

                    for(DataSnapshot id : dataSnapshot.getChildren()){
                        //then get the info under that unique ID
                        String PassengerID = id.getKey();
                        Toast.makeText(TripRequests.this, ""+PassengerID, Toast.LENGTH_SHORT).show();
                        DatabaseReference PassengerCoordinates = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripId).child("TripRequests").child(PassengerID);
                        PassengerCoordinates.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                    if (map.get("Lat") != null) {
                                        lat = Float.valueOf(map.get("Lat").toString());
                                        Toast.makeText(TripRequests.this, ""+lat, Toast.LENGTH_SHORT).show();

                                    }
                                    if (map.get("Lon") != null) {
                                        lon = Float.valueOf(map.get("Lon").toString());
                                    }

                                    //PassengerInfo(PassengerID,lat,lon);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        PassengerInfo(PassengerID,lat,lon);

                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void PassengerInfo(final String passengerID, final float lat, final float lon) {
        DatabaseReference passengersDB = FirebaseDatabase.getInstance().getReference().child("users").child(passengerID);
        passengersDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("profileImageUrl") != null) {
                        profilePicURL = map.get("profileImageUrl").toString();
                    }
                    if (map.get("Name") != null) {
                        String name = map.get("Name").toString();
                        First = name.substring(0, 1).toUpperCase() + name.substring(1);
                    }

                    if (map.get("Surname") != null) {
                        String surname = map.get("Surname").toString();
                        Surname = surname.substring(0, 1).toUpperCase() + surname.substring(1);
                    }
                    if (map.get("Username") != null) {
                        Username = map.get("Username").toString();
                    }


                    Fullname = First + " " + Surname;
                    Requests object = new Requests(passengerID,profilePicURL,lon,lat,Username,Fullname);
                    resultsRequest.add(object);
                    requestAdapter.notifyDataSetChanged();




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private ArrayList resultsRequest = new ArrayList<Requests>();
    private List<Requests> getDataRequest() {
        return  resultsRequest;
    }

    @Override
    public void onResume() {
        super.onResume();
        resultsRequest.clear();

    }
}
