package com.example.student_carpooling;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ChatActivity extends AppCompatActivity {

    Intent intent;

    ImageView otherProfilePic;

    ImageButton sendMsg;
    EditText Msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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

        sendMsg = findViewById(R.id.send);
        Msg = findViewById(R.id.message);

        TextView otherUserName = findViewById(R.id.otherUsername);
        otherProfilePic = findViewById(R.id.otherPic);
        String StrUserName = intent.getStringExtra("Username");
        String ProfilePicUrl = intent.getStringExtra("ProfilePicURL");
        otherUserName.setText(StrUserName);



        if(ProfilePicUrl.equals("defaultPic")){
            otherProfilePic.setImageResource(R.drawable.logo);
        }
        else {
            Glide.with(ChatActivity.this).load(ProfilePicUrl).into(otherProfilePic);
        }


        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });



    }

    private void SendMessage(){}

}
