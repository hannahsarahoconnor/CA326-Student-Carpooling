package com.example.student_carpooling;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PassengerHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_help);


        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView back = findViewById(R.id.back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Button Contact;
        Contact = findViewById(R.id.ContactAdmin);
        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PassengerHelp.this,ChatActivity.class);
                intent1.putExtra("Username","StudentCarpooling");
                intent1.putExtra("ID", "tFRougwMUphm8B95q7EAToUoYci1");
                intent1.putExtra("Fullname","Admins");
                intent1.putExtra("ProfilePicURL","defaultPic");
                startActivity(intent1);
                finish();
            }
        });
    }
}
