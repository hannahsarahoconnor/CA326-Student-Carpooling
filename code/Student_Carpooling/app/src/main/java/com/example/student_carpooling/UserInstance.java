package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;

public class UserInstance extends AppCompatActivity {

    //get current user id
    private FirebaseAuth mAuth;
    private DatabaseReference UserDb;
    private String UserID, Type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_instance);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();


        OneSignal.startInit(this).init();
        //notify one signal that the user wishes to recieves nofications
        OneSignal.setSubscription(true);
        //get key and add unique key to user database for that user
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("users").child(UserID).child("NotificationKey").setValue(userId);

            }
        });
        //show notfication in topbar
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        new SendNotification("Hello!!", "Student Carpooling", null);

        Button Passenger;
        Button Driver;

        Passenger = findViewById(R.id.Rider);
        Driver = findViewById(R.id.Driver);

        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);



        Passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDb.child("Type").setValue("Passenger");
                moveToPassenger();
            }
        });

        Driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDb.child("Type").setValue("Driver");
                moveToDriver();
            }
        });




    }

    private void moveToDriver(){

        Intent intent = new Intent(UserInstance.this, DriverMain.class);
        startActivity(intent);
        finish();
    }

    private void moveToPassenger(){
        Intent intent = new Intent(UserInstance.this, PassengerActivity.class);
        startActivity(intent);
        finish();
    }
}

