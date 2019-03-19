package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private EditText text_send;
    private String CurrentUserID, OtherUserID, AdminID;
    private DatabaseReference userUpdate;

    // RecyclerView.Adapter
    MessageAdapter messageAdapter;
    List<Message> messageList;
    RecyclerView messageRecyclerView;
    String CurrentUsername, OtherUserKey, Type, NotificationKey;
    DatabaseReference msgDB, reference, ref;
    ArrayList<String> chatters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatters = new ArrayList<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser!=null){
            CurrentUserID = mAuth.getCurrentUser().getUid();
            DatabaseReference UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserID);
            UserDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                        //data originally added is kept in this format
                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                        Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                       if(map != null){
                        if (map.get("Username") != null) {
                            CurrentUsername = (String) map.get("Username");
                        }
                        //Need to get the current user type if they decided to leave the chat -- to changed activity to the driver or passenger home
                        if (map.get("Type") != null) {
                            Type = (String) map.get("Type");
                        }
                        if (map.get("NotificationKey") != null) {
                            NotificationKey = (String) map.get("NotificationKey");
                        }

                    }}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        ImageView back = findViewById(R.id.back);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });


        //set up Recycler View
        messageRecyclerView = findViewById(R.id.chatRecycler);
        messageRecyclerView.setHasFixedSize(true);
        messageAdapter = new MessageAdapter(getData(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messageRecyclerView.setAdapter(messageAdapter);

        Intent intent = getIntent();
        ImageButton btn_send = findViewById(R.id.send);
        text_send = findViewById(R.id.message);

        ImageView delete = findViewById(R.id.delete);
        TextView otherUserName = findViewById(R.id.otherUsername);
        ImageView otherProfilePic = findViewById(R.id.otherPic);
        TextView otherFullname = findViewById(R.id.otherFullName);
        String StrOtherUserName = intent.getStringExtra("Username");
        String OtherProfilePicUrl = intent.getStringExtra("ProfilePicURL");
        String StrOtherFullName = intent.getStringExtra("Fullname");
        OtherUserID = intent.getStringExtra("ID");
        otherUserName.setText(StrOtherUserName);
        otherFullname.setText(StrOtherFullName);
        AdminID = getResources().getString(R.string.AdminID);


        //Other User id is showing the current User Id instead
        try {
            if (!(OtherProfilePicUrl.equals("defaultPic"))) {
                Glide.with(ChatActivity.this).load(OtherProfilePicUrl).into(otherProfilePic);
            }
        }catch (Exception e){
            //
        }

        ref = FirebaseDatabase.getInstance().getReference("users").child(OtherUserID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //get their notification key
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    //data originally added is kept in this format
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if(map != null){
                        if (map.get("NotificationKey") != null) {
                            OtherUserKey = (String) map.get("NotificationKey");
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Are you sure you want to leave this chat?").setMessage("This chat will be deleted from your view and theirs. You wont be able to contact this user again unless in terms of a carpool, are you sure?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int which) {
                        //delete the chat list from this users end only
                        DatabaseReference chats = FirebaseDatabase.getInstance().getReference().child("ChatList").child(CurrentUserID).child(OtherUserID);
                        DatabaseReference chats2 = FirebaseDatabase.getInstance().getReference().child("ChatList").child(OtherUserID).child(CurrentUserID);
                        chats.removeValue();
                        chats2.removeValue();
                        //get current user type
                        if (Type.equals("Driver")) {
                            Intent intent = new Intent(ChatActivity.this, DriverMessage.class);
                            startActivity(intent);
                            finish();
                            dialog.dismiss();
                        } else {
                            Intent intent = new Intent(ChatActivity.this, PassengerMessage.class);
                            startActivity(intent);
                            finish();
                            dialog.dismiss();
                        }
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int which) {
                                dialog.cancel();

                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }

        });

        // if the user started this activity through the contact admin dialog
        if (OtherUserID.equals(AdminID)) {
            AdminContact();
        }

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Message
                String Message = text_send.getText().toString();
                if (!Message.equals("")) {
                    SendNewMessage(Message, CurrentUserID, OtherUserID, CurrentUsername, OtherUserKey);
                    //once sent, reset the EditText Field
                    text_send.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Enter your message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");

            }
        });


        readMessage(CurrentUserID, OtherUserID);

    }

    private void AdminContact() {
        userUpdate = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserID);
        userUpdate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if(map != null){
                    if (map.get("AdminContact") != null) {
                        //check to see if this is the first time they've made contact( if AdminContact value is 0)
                        //if it's 1 don't automatically send this message
                        //only reset this value within the database
                        String AdminContactStr = Objects.requireNonNull(map.get("AdminContact")).toString();
                        int AdminContact = (Integer.parseInt(AdminContactStr));
                        if (AdminContact == 0) {
                            String Message = "This is an automated message. Please send us your query and a member of our team will get back to you shortly!";
                            SendNewMessage(Message, AdminID, CurrentUserID, "StudentCarpooling", NotificationKey);
                            userUpdate.child("AdminContact").setValue(1);
                        }
                    }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void SendNewMessage(String msg, String id, final String otherid, String CurrentUsername, String OtherKey) {
        msgDB = FirebaseDatabase.getInstance().getReference().child("Chats").push();
        //.push() creates a unique id for this message

        HashMap<String, Object> MessageInfo = new HashMap<>();
        MessageInfo.put("Sender", id);
        MessageInfo.put("Recipient", otherid);
        MessageInfo.put("Message", msg);

        //send notification to that user
        String msgFormat = CurrentUsername + ": " + msg;
        new Notification(msgFormat, "Student Carpooling", OtherKey);

        //add value to database
        msgDB.setValue(MessageInfo);

        //clear the current array list, notify the adapter of data change and reset the recycler view with updated message
        resultsMessage.clear();
        messageRecyclerView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();

        //used for within Driver/Passenger Message Activity
        //stores that a chat has been created between the two users
        //rather than having to scan through each message

        //in format -->"ChatList" -> sender id(current) & recipient
        final DatabaseReference ChatIDs = FirebaseDatabase.getInstance().getReference().child("ChatList").child(id).child(otherid);


        ChatIDs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //re-add the other user id as a child so the branch can be created
                    ChatIDs.child("id").setValue(otherid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMessage(final String CurrentId, final String OtherId) {
        messageList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resultsMessage.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if(message != null){
                    if (message.getRecipient().equals(CurrentId) && message.getSender().equals(OtherId) ||
                            message.getSender().equals(CurrentId) && message.getRecipient().equals(OtherId)) {
                        resultsMessage.add(message);
                        messageAdapter.notifyDataSetChanged();
                        //notify adapter
                    }
                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Message> resultsMessage = new ArrayList<>();

    private ArrayList<Message> getData() {
        return resultsMessage;

    }

}
