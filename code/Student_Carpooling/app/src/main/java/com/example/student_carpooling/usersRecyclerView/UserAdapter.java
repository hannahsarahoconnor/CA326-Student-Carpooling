package com.example.student_carpooling.usersRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.ChatActivity;
import com.example.student_carpooling.R;
import com.example.student_carpooling.messagesRecyclerView.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.os.Build.ID;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolders> {

    private List<User> list;
    private Context context;


    String LastMessage;

    FirebaseUser firebaseUser;


    public UserAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public UserAdapter() {
    }


    @NonNull
    @Override
    public UserViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_cards, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        UserViewHolders uvh = new UserViewHolders(layoutView);
        return uvh;
    }

    public void swipePos(int position){
        //delete from database
        DatabaseReference RequestsDB = FirebaseDatabase.getInstance().getReference().child("ChatList").child(firebaseUser.getUid()).child(ID);
        RequestsDB.removeValue();
        list.remove(position);
        notifyDataSetChanged();
    }





    @Override
    public void onBindViewHolder(@NonNull UserViewHolders userViewHolders, int i) {
        final String Username = list.get(i).getUserName();
        final String ProfilePicUrl = list.get(i).getProfilePicUrl();
        final String ID = list.get(i).getID();
        final String Fullname = list.get(i).getFullname();
        userViewHolders.UserName.setText(Username);
        //userViewHolders.FullName.setText(Fullname);

        if (!(ProfilePicUrl.equals("defaultPic"))) {
            Glide.with(context).load(ProfilePicUrl).into(userViewHolders.UserProfilePic);
        }

        getRecentMessage(ID,userViewHolders.Message,Username );


        userViewHolders.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("Username", Username);
                intent.putExtra("ID", ID);
                intent.putExtra("Fullname", Fullname);
                intent.putExtra("ProfilePicURL", ProfilePicUrl);
                Toast.makeText(context, "Starting Chat with " + Username, Toast.LENGTH_SHORT).show();
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }


    private void getRecentMessage(final String userID, final TextView msg, final String username) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if(message.getRecipient().equals(firebaseUser.getUid())&& message.getSender().equals(userID) ||
                            message.getRecipient().equals(userID)&& message.getSender().equals(firebaseUser.getUid())){
                        if(message.getSender().equals(firebaseUser.getUid())){
                            LastMessage = "You: " + message.getMessage();
                        }
                        else{

                            LastMessage = username + ": " + message.getMessage();
                        }


                    }
                }
               msg.setText(LastMessage);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
