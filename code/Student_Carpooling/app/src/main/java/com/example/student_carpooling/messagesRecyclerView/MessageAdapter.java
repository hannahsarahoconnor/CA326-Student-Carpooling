package com.example.student_carpooling.messagesRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student_carpooling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolders> {

    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    private List<Message> list;
    private Context context;

    FirebaseUser firebaseUser;


    public MessageAdapter(List<Message> list, Context context){
        this.list = list;
        this.context = context;
    }



    @NonNull
    @Override
    public MessageViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i == MSG_RIGHT){

            View layoutView = LayoutInflater.from(context).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageViewHolders(layoutView);


        }
        else{

            View layoutView = LayoutInflater.from(context).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageViewHolders(layoutView);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolders messageViewHolders, int i) {
        //final String Sender = list.get(i).getSender();
        //final String Recipient = list.get(i).getRecipient();

        Message msg = list.get(i);

        messageViewHolders.message.setText(msg.getMessage());




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // check to see if receiver or sender

        if(list.get(position).getSender().equals(firebaseUser.getUid())){
           return MSG_RIGHT;
        }
        else{
            return MSG_LEFT;
        }


        //return super.getItemViewType(position);

    }
}

