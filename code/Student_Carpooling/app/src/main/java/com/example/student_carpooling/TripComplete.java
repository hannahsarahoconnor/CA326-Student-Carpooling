package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.student_carpooling.ratingRecyclerView.UserRating;
import com.example.student_carpooling.ratingRecyclerView.UserRatingAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TripComplete extends AppCompatActivity {

    private RecyclerView.Adapter ratingAdapter;
    private String DriverID, TripID, CurrentUserID,Username,PicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_complete);

        RecyclerView recyclerView = findViewById(R.id.ratingsRecycler);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ratingAdapter = new UserRatingAdapter(getDataRating(), TripComplete.this);
        recyclerView.setAdapter(ratingAdapter);

      //if driver id == ur id, only show passengets

       Intent intent = getIntent();
       DriverID = intent.getStringExtra("DriverID");
       TripID = intent.getStringExtra("TripID");

       String DriverUsername = intent.getStringExtra("DriverUsername");
       String DriverPicUrl = intent.getStringExtra("driverUrl");
       String CancelCheck = intent.getStringExtra("Cancelled");


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
        CurrentUserID = firebaseUser.getUid();}

        if(Integer.parseInt(CancelCheck)==0){

        if(!CurrentUserID.equals(DriverID)){
            //Get Passengers
            UserRating object = new UserRating(DriverID,DriverPicUrl,DriverUsername);
            results.add(object);
            ratingAdapter.notifyDataSetChanged();
            getPassengers();
        }
        else{
            getPassengers();
        }
        }
        else{
            //if cancelled -- only rate the driver for this cancellation
            //disable the textview that says you have finished a trip!
            TextView complete1 = findViewById(R.id.complete);
            TextView complete2 = findViewById(R.id.complete2);
            complete1.setVisibility(View.INVISIBLE);
            complete2.setVisibility(View.INVISIBLE);
            UserRating object = new UserRating(DriverID,DriverPicUrl,DriverUsername);
            results.add(object);
            ratingAdapter.notifyDataSetChanged();
        }

        Button Submit = findViewById(R.id.sumbit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the driver id == the current id, then we know that user type is a driver
                if(!CurrentUserID.equals(DriverID)){
                Intent intent = new Intent(TripComplete.this, PassengerMain.class);
                startActivity(intent);
                }
                else{
                    Intent intent = new Intent(TripComplete.this,DriverMain.class);
                    startActivity(intent);
                }
            }
        });



    }

    private void getPassengers(){
        DatabaseReference passengers = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverID).child(TripID).child("Passengers");
        passengers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get ids -- make sure not the same as current user
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        final String passengerKey = id.getKey();
                        if (passengerKey != null && !passengerKey.equals(CurrentUserID)) {
                            PassengerInfo(passengerKey);
                        }
                    }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void PassengerInfo(final String ID){
        DatabaseReference passengers = FirebaseDatabase.getInstance().getReference().child("TripForms").child(DriverID).child(TripID).child("Passengers").child(ID);
        passengers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {
                        if (map.get("Username") != null) {
                            Username = (String) map.get("Username");
                        }
                        if (map.get("profileImageUrl") != null) {
                            PicUrl =(String) map.get("profileImageUrl");
                        }

                        //create a new object for that user
                        UserRating object = new UserRating(ID, PicUrl, Username);
                        results.add(object);
                        ratingAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private ArrayList<UserRating> results = new ArrayList<>();

    private List<UserRating> getDataRating() {
        return results;
    }

}
