package com.example.student_carpooling.messagesRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student_carpooling.R;
import com.example.student_carpooling.usersRecyclerView.UserAdapter;

public class MessageViewHolders extends RecyclerView.ViewHolder{

    public TextView message;


    public MessageViewHolders(View itemView) {
        super(itemView);

        message =  itemView.findViewById(R.id.show);


    }

}
