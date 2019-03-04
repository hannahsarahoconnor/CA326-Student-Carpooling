package com.example.student_carpooling.ratingRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.student_carpooling.R;


public class UserRatingViewHolders  extends RecyclerView.ViewHolder {

    public ImageView UserProfilePic;
    public TextView UserName;
    public RatingBar ratingBar;


    public UserRatingViewHolders(@NonNull View itemView) {
        super(itemView);


        UserProfilePic = (ImageView) itemView.findViewById(R.id.UserProfilePic);
        UserName = (TextView) itemView.findViewById(R.id.UserName);
        ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);

    }
}
