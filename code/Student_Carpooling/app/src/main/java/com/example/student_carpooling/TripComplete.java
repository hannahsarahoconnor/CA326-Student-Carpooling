package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.student_carpooling.ratingRecyclerView.UserRating;
import com.example.student_carpooling.ratingRecyclerView.UserRatingAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TripComplete extends AppCompatActivity {

    private LinearLayoutManager ratingLayoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter ratingAdapter;

    private Button cancel, request, start;

    Intent intent;

    FirebaseAuth mAuth;

    Button Submit;

    private String DriverID, TripID, CurrentUserID,Username,PicUrl,DriverUsername,DriverPicUrl,CancelCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_complete);

        recyclerView = findViewById(R.id.ratingsRecycler);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ratingAdapter = new UserRatingAdapter(getDataRating(), TripComplete.this);
        ratingLayoutManager = new LinearLayoutManager(TripComplete.this);
        recyclerView.setAdapter(ratingAdapter);

      //if driver id == ur id, only show passengets

       intent = getIntent();
       DriverID = intent.getStringExtra("DriverID");
       TripID = intent.getStringExtra("TripID");

       DriverUsername = intent.getStringExtra("DriverUsername");
       DriverPicUrl = intent.getStringExtra("driverUrl");
       CancelCheck = intent.getStringExtra("Cancelled");



        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

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
        }}

        else{
            //if cancelled -- only rate the driver for this cancellation
            UserRating object = new UserRating(DriverID,DriverPicUrl,DriverUsername);
            results.add(object);
            ratingAdapter.notifyDataSetChanged();
        }

        Submit = findViewById(R.id.sumbit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the driver id == the current id, then we know that user type is a driver
                if(!CurrentUserID.equals(DriverID)){
                Intent intent = new Intent(TripComplete.this,PassengerActivity.class);
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
                        if(!passengerKey.equals(CurrentUserID)){
                            PassengerInfo(passengerKey);}
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
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Username")!=null){
                        Username = map.get("Username").toString();
                    }
                    if(map.get("profileImageUrl")!=null){
                        PicUrl = map.get("profileImageUrl").toString();
                    }

                    //create a new object for that user
                    UserRating object = new UserRating(ID,PicUrl,Username);
                    results.add(object);
                    ratingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    //this can have different cases

    //if a trip was ended/completed

    //if a trip was cancelled

    //what about if a passenger leaves a trip?



    //recycler view of people in the trip


    //Pic-> Username
    //Stars
    //submit button to leave and back to home page


    //where to save this info??


    private ArrayList results = new ArrayList<UserRating>();

    private List<UserRating> getDataRating() {
        return results;
    }

}
