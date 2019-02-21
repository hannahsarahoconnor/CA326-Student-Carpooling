package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.messagesRecyclerView.Message;
import com.example.student_carpooling.messagesRecyclerView.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    Intent intent;

    ImageView otherProfilePic;

    ImageButton sendMsg;
    EditText Msg;

    String CurrentUserID, OtherUserID;
    FirebaseAuth mAuth;

    TextView otherFullname;

    MessageAdapter messageAdapter;
    List<Message> messageList;
    RecyclerView messageRecyclerView;
    LinearLayoutManager msgLayoutManager;

   DatabaseReference msgDB;

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


        //set up Recycler View
        messageRecyclerView = findViewById(R.id.chatRecycler);
        //?
        messageRecyclerView.setHasFixedSize(true);





        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        intent = getIntent();

        sendMsg = findViewById(R.id.send);
        Msg = findViewById(R.id.message);

        TextView otherUserName = findViewById(R.id.otherUsername);
        otherProfilePic = findViewById(R.id.otherPic);
        otherFullname = findViewById(R.id.otherFullName);
        String StrOtherUserName = intent.getStringExtra("Username");
        String OtherProfilePicUrl = intent.getStringExtra("ProfilePicURL");
        String StrOtherFullName = intent.getStringExtra("Fullname");
        OtherUserID = intent.getStringExtra("ID");
        otherUserName.setText(StrOtherUserName);
        otherFullname.setText(StrOtherFullName);

        //causing crash
       if(!(OtherProfilePicUrl.equals("defaultPic"))){
            Glide.with(ChatActivity.this).load(OtherProfilePicUrl).into(otherProfilePic);
       }

        GetNewMessage(CurrentUserID,OtherUserID);


        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Message
                String Message = Msg.getText().toString();
                if(!Message.equals("")){
                  SendNewMessage(Message,CurrentUserID,OtherUserID);
                  //once sent, reset the EditText Field
                  Msg.setText("");
                }
                  else{
                    Toast.makeText(ChatActivity.this,"Enter your message",Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    private void SendNewMessage(String msg, String id, final String otherid) {

        //create/add to Database -> "Chats"
        //unique msg entry with push()
        //msgDB = FirebaseDatabase.getInstance().getReference().child("Chats").push();

        //Map MessageInfo = new HashMap();
        //MessageInfo.put("Sender", CurrentUserID );
        //MessageInfo.put("Recipient", OtherUserID);
        //MessageInfo.put("Message", msg);


        // msgDB.push().setValue(MessageInfo);

        msgDB = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Sender", id);
        hashMap.put("Recipient", otherid);
        hashMap.put("Message", msg);

        msgDB.child("Chats").push().setValue(hashMap);


        //for the chat fragment
        final DatabaseReference ChatIDs = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(CurrentUserID).child(OtherUserID);

        ChatIDs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ChatIDs.child("id").setValue(otherid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //messages arent showing up straight away?


        //get chats id, get info, create new object


        //Chats -> chatid -> message id

    }

    private void GetNewMessage(final String CurrentId, final String OtherId){
        messageList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        //reference =

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //on resume instead?
                messageList.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    //get one of the message object
                    Message message = dataSnapshot1.getValue(Message.class);





                    //check its attributes
                    if(message.getRecipient().equals(CurrentId) && message.getSender().equals(OtherId)|| message.getRecipient().equals(OtherId) && message.getSender().equals(CurrentId)){
                        messageList.add(message);
                        //messageAdapter.notifyDataSetChanged();
                    }

                    messageAdapter = new MessageAdapter(messageList,ChatActivity.this);
                    messageRecyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
