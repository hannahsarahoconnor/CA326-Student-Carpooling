package com.example.student_carpooling.requestsRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class RequestsViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView ProfilePic,ProfileIcon,MessageIcon, LocationIcon,AcceptIcon,DeclineIcon;
    public TextView UserName;


    public RequestsViewHolders(@NonNull View itemView) {
        super(itemView);
        ProfilePic = (ImageView) itemView.findViewById(R.id.UserProfilePic);
        UserName = (TextView) itemView.findViewById(R.id.UserName);

        ProfileIcon = (ImageView) itemView.findViewById(R.id.profile);
        MessageIcon =  (ImageView) itemView.findViewById(R.id.message);
        LocationIcon = (ImageView) itemView.findViewById(R.id.location);
        AcceptIcon = (ImageView) itemView.findViewById(R.id.accept);
        DeclineIcon = (ImageView) itemView.findViewById(R.id.decline);
    }

    @Override
    public void onClick(View v) {

    }
}
