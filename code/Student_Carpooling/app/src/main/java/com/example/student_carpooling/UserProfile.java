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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    //show how many carpools theyve been apart of

    private RatingBar ratingBar;
    private TextView Name,Username,Uni,ratingText,Type,TripCount;
    private Intent intent;
    private String UserID, name,username,college,userType,picurl,CurrentUserID;
    private DatabaseReference UserDb;
    private CircleImageView profilePic;
    private Button report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView back = findViewById(R.id.back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //get id from intent
        intent = getIntent();
        TripCount = findViewById(R.id.tripcount);
        report = findViewById(R.id.report);
        ratingBar = findViewById(R.id.rating);
        ratingText = findViewById(R.id.ratingText);
        Name = findViewById(R.id.Name);
        Username = findViewById(R.id.Username);
        Uni = findViewById(R.id.College);
        Type = findViewById(R.id.Type);
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
                        Map ReportInfo = new HashMap();
                        ReportInfo.put("Reason",Report);
                        ReportInfo.put("ReportedBy",CurrentUserID);
                        reportedUsers.push().setValue(ReportInfo);
                        Toast.makeText(UserProfile.this, "Thank you", Toast.LENGTH_SHORT).show();
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
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0){
                    //data originally added is kept in this format
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map.get("Name")!=null){
                        name = map.get("Name").toString();
                        Name.setText(name);

                    }
                    if(map.get("Type")!=null){
                        userType = map.get("Type").toString();
                        Type.setText(userType);

                    }
                    if (map.get("CompletedTrips") != null) {
                        String completed = map.get("CompletedTrips").toString();
                        TripCount.setText(completed + "completed carpools");
                    }
                    if(map.get("University")!=null){
                        college = map.get("University").toString();
                        Uni.setText(college);
                    }
                    if(map.get("Username")!=null){
                        username = map.get("Username").toString();
                        Username.setText(username);
                    }
                    if(map.get("profileImageUrl")!=null){
                        picurl = map.get("profileImageUrl").toString();

                        if(picurl.equals("defaultPic")){
                            profilePic.setImageResource(R.drawable.logo);
                        }
                        else{
                            Glide.with(getApplication()).load(picurl).into(profilePic);
                        }}

                    int ratingSum = 0;
                    float ratingTotal = 0;
                    float ratingAvg =0;
                    for(DataSnapshot rates : dataSnapshot.child("Ratings").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(rates.getValue().toString());
                        ratingTotal++;
                    }

                    //calculate average and set bar
                    if(ratingTotal != 0){
                        ratingAvg  = ratingSum/ratingTotal;
                        ratingBar.setRating(ratingAvg);
                        ratingText.setText(Math.round(ratingTotal) + " total ratings");
                        ratingText.setVisibility(View.VISIBLE);
                    }
                    else{
                        //set the text to visible
                        ratingText.setVisibility(View.VISIBLE);
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
