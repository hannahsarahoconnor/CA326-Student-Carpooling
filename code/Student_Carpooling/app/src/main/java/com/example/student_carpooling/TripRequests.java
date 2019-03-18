package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.student_carpooling.requestsRecyclerView.Requests;
import com.example.student_carpooling.requestsRecyclerView.RequestsAdapter;
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

public class TripRequests extends AppCompatActivity {

    private RecyclerView.Adapter requestAdapter;
    private RecyclerView requestRecyclerView;

    private String TripId,UserID,profilePicURL,First,Surname,Fullname,Username,NotificationKey,CurrentUserName;
    private DatabaseReference TripDB;
    private TextView textView1, textView2;
    private Button findRequest;
    private float lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView back = findViewById(R.id.back);
        if(getSupportActionBar() != null){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textView1 = findViewById(R.id.Text);
        textView2 = findViewById(R.id.Text2);
        findRequest = findViewById(R.id.Request);

        Intent intent = getIntent();
        TripId = intent.getStringExtra("TripID");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser != null){
            UserID = CurrentUser.getUid();
        }
        TripDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripId);

        requestRecyclerView = findViewById(R.id.requestRecycler);
        requestRecyclerView.setNestedScrollingEnabled(true);
        requestRecyclerView.setHasFixedSize(true);
        requestAdapter = new RequestsAdapter(getDataRequest(), TripRequests.this);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(TripRequests.this));
        requestRecyclerView.setAdapter(requestAdapter);

        getUserName();
        getRequests(); }


    private void getUserName(){
        TripDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {

                        if (map.get("Username") != null) {
                            CurrentUserName = (String) map.get("Username");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getRequests() {
        try{
            DatabaseReference requestsDB = TripDB.child("TripRequests");
            requestsDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //if there is any info there
                        for (DataSnapshot id : dataSnapshot.getChildren()) {
                            //then get the info under that unique ID
                            final String PassengerID = id.getKey();
                            if(PassengerID != null){
                            DatabaseReference PassengerCoordinates = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripId).child("TripRequests").child(PassengerID);
                            PassengerCoordinates.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                        };
                                        Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                                        if (map != null) {
                                            if (map.get("Lat") != null) {
                                                String LatStr = Objects.requireNonNull(map.get("Lat")).toString();
                                                lat = (Float.valueOf(LatStr));
                                            }
                                            if (map.get("Lon") != null) {
                                                String LonStr = Objects.requireNonNull(map.get("Lon")).toString();
                                                lon = (Float.valueOf(LonStr));
                                            }

                                            PassengerInfo(PassengerID, lat, lon);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        }
                    }else{
                        requestRecyclerView.setVisibility(View.GONE);
                        textView1.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        findRequest.setVisibility(View.VISIBLE);

                        findRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TripRequests.this,DriverFindRequests.class);
                                startActivity(intent);
                            }
                        });
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch(Exception e){

            requestRecyclerView.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            findRequest.setVisibility(View.VISIBLE);

            findRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TripRequests.this,DriverFindRequests.class);
                    startActivity(intent);
                }
            });

        }
    }
    private void PassengerInfo(final String passengerID, final float lat, final float lon) {
        DatabaseReference passengersDB = FirebaseDatabase.getInstance().getReference().child("users").child(passengerID);
        passengersDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {
                        if (map.get("profileImageUrl") != null) {
                            profilePicURL = (String) map.get("profileImageUrl");
                        }
                        if (map.get("Name") != null) {
                            String name = (String) map.get("Name");
                            if (name != null) {
                                First = name.substring(0, 1).toUpperCase() + name.substring(1);
                            }
                        }
                        if (map.get("Surname") != null) {
                            String surname =( String) map.get("Surname");
                            if (surname != null) {
                                Surname = surname.substring(0, 1).toUpperCase() + surname.substring(1);
                            }
                        }
                        if (map.get("Username") != null) {
                            Username = (String) map.get("Username");
                        }
                        if (map.get("NotificationKey") != null) {
                            NotificationKey = (String) map.get("NotificationKey");
                        }

                        Fullname = First + " " + Surname;

                        Requests object = new Requests(CurrentUserName, NotificationKey, TripId, passengerID, profilePicURL, lon, lat, Username, Fullname);
                        resultsRequest.add(object);
                        requestAdapter.notifyDataSetChanged();


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<Requests> resultsRequest = new ArrayList<>();
    private ArrayList<Requests> getDataRequest() {
        return  resultsRequest;
    }

    @Override
    public void onResume() {
        super.onResume();
        resultsRequest.clear();

    }
}

