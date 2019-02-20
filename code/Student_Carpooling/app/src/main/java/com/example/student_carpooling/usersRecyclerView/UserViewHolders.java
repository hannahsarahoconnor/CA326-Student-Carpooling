package com.example.student_carpooling.usersRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class UserViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView UserProfilePic;
    public TextView UserName;


    public UserViewHolders(@NonNull View itemView, final UserAdapter.onUserListener UserList) {
        super(itemView);
        itemView.setOnClickListener(this);

        UserProfilePic = (ImageView) itemView.findViewById(R.id.UserProfilePic);
        UserName = (TextView) itemView.findViewById(R.id.UserName);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(UserList != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        UserList.onUserClick(position);
                    }}
            }
        });

    }


    @Override
    public void onClick(View v) {

    }


}
