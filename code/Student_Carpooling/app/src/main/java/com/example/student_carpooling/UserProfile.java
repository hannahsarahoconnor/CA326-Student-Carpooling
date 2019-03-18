package com.example.student_carpooling;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView Name,Username,Uni,ratings,Type,TripCount;
    private String UserID, name,username,college,userType,picurl,CurrentUserID,surname;
    private DatabaseReference UserDb;
    private CircleImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView back = findViewById(R.id.back);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
        getSupportActionBar().setTitle("");}
        toolbar.setTitleTextColor(Color.WHITE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null){
        CurrentUserID = firebaseUser.getUid();
        }
        //get id from intent
        Intent intent = getIntent();
        TripCount = findViewById(R.id.CompletedNo);
        ratings = findViewById(R.id.RatingNo);

        Button report = findViewById(R.id.report);
        ratingBar = findViewById(R.id.rating);
        Name = findViewById(R.id.Name);
        Username = findViewById(R.id.Username);
        Uni = findViewById(R.id.College);
        Type = findViewById(R.id.type);
        profilePic = findViewById(R.id.ProfilePic);

        UserID = intent.getStringExtra("ID");
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        getUserDB();


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //show custom alert dialog
                final AlertDialog dialogBuilder = new AlertDialog.Builder(UserProfile.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.report_user, null);
                TextView User = dialogView.findViewById(R.id.textView);
                User.setText("Report " + username);
                final EditText reason =  dialogView.findViewById(R.id.reason);
                Button Submit =  dialogView.findViewById(R.id.Submit);
                Button Cancel = dialogView.findViewById(R.id.Cancel);

                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //send to database...
                        String Report = reason.getText().toString();
                        DatabaseReference reportedUsers = FirebaseDatabase.getInstance().getReference().child("ReportedUsers").child(UserID);
                        Map<String, Object> ReportInfo = new HashMap<>();
                        ReportInfo.put("Reason",Report);
                        ReportInfo.put("ReportedBy",CurrentUserID);
                        reportedUsers.push().setValue(ReportInfo);
                        Toast.makeText(UserProfile.this, "Thank you for your user feedback", Toast.LENGTH_SHORT).show();
                        dialogBuilder.dismiss();
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });

    }


    private void getUserDB(){
        UserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //makes sure the data is present, else the app will crash if not
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    //data originally added is kept in this format
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {

                        if (map.get("Name") != null) {
                            name = (String) map.get("Name");
                            Name.setText(name);

                        }
                        if (map.get("Surname") != null) {
                            surname = (String) map.get("Surname");
                            Name.setText(name + " " + surname);

                        }
                        if (map.get("Type") != null) {
                            userType = (String) map.get("Type");
                            Type.setText(userType);

                        }
                        if (map.get("CompletedTrips") != null) {
                            String completed = Objects.requireNonNull(map.get("CompletedTrips")).toString();
                            TripCount.setText(completed);
                        }
                        if (map.get("University") != null) {
                            college = (String) map.get("University");
                            Uni.setText(college);
                        }
                        if (map.get("Username") != null) {
                            username = (String) map.get("Username");
                            Username.setText("@" + username);
                        }
                        if (map.get("profileImageUrl") != null) {
                            picurl = (String) map.get("profileImageUrl");

                            if (picurl != null) {
                                if (picurl.equals("defaultPic")) {
                                    profilePic.setImageResource(R.drawable.logo);
                                } else {
                                    Glide.with(getApplication()).load(picurl).into(profilePic);
                                }
                            }
                        }

                        int ratingSum = 0;
                        float ratingTotal = 0;
                        for (DataSnapshot rates : dataSnapshot.child("Ratings").getChildren()) {
                            String ratingStr = Objects.requireNonNull(rates.getValue()).toString();
                            int rating = (Integer.parseInt(ratingStr));
                            ratingSum = ratingSum + rating;
                            ratingTotal++;
                        }

                        //calculate average and set bar
                        if (ratingTotal != 0) {
                            float ratingAvg = ratingSum / ratingTotal;
                            ratingBar.setRating(ratingAvg);
                            ratingBar.setIsIndicator(true);
                            ratings.setText("" + Math.round(ratingTotal));
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




}
