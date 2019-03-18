package com.example.student_carpooling.messagesRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class MessageViewHolders extends RecyclerView.ViewHolder{

    public TextView message;


    MessageViewHolders(View itemView) {
        super(itemView);

        message =  itemView.findViewById(R.id.show);


    }

}
