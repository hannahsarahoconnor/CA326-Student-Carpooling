package com.example.student_carpooling.usersRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class UserViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView UserProfilePic;
    public TextView UserName, FullName;


    public UserViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        UserProfilePic = (ImageView) itemView.findViewById(R.id.UserProfilePic);
        UserName = (TextView) itemView.findViewById(R.id.UserName);
        FullName = (TextView) itemView.findViewById(R.id.Fullname);


    }


    @Override
    public void onClick(View v) {

    }


}
