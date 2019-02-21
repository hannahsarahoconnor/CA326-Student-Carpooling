package com.example.student_carpooling.messagesRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.ChatActivity;
import com.example.student_carpooling.R;
import com.example.student_carpooling.usersRecyclerView.User;
import com.example.student_carpooling.usersRecyclerView.UserAdapter;
import com.example.student_carpooling.usersRecyclerView.UserViewHolders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolders> {

    public static final int SENDER = 0;
    public static final int RECEIVER = 1;

    private List<Message> list;
    private Context context;

    String CurrentUser;


    public MessageAdapter(List<Message> list, Context context){
        this.list = list;
        this.context = context;
    }



    @NonNull
    @Override
    public MessageViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i == 1){

            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sender_card, viewGroup, false);
            return new MessageViewHolders(layoutView);


        }
        else{

            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reciever_card, viewGroup, false);
            return new MessageViewHolders(layoutView);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolders messageViewHolders, int i) {
        //final String Sender = list.get(i).getSender();
        //final String Recipient = list.get(i).getRecipient();
        final String Message = list.get(i).getMessage();

        messageViewHolders.message.setText(Message);



    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @Override
    public int getItemViewType(int position) {
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // check to see if receiver or sender

        if(list.get(position).getSender().equals(CurrentUser)){
           return 0;
        }
        else{
            return 1;
        }


        //return super.getItemViewType(position);

    }
}

