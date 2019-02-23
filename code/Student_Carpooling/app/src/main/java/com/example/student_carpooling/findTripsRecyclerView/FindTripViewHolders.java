package com.example.student_carpooling.findTripsRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class FindTripViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView username, fullname, seats, time, starting, destination, date;
    public ImageView profilePic;

    public FindTripViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        username = (TextView) itemView.findViewById(R.id.Dusername);
        fullname = (TextView) itemView.findViewById(R.id.Dfullname);
        seats = (TextView) itemView.findViewById(R.id.Seats);
        time = (TextView) itemView.findViewById(R.id.Time);
        starting = (TextView) itemView.findViewById(R.id.Starting);
        destination = (TextView) itemView.findViewById(R.id.Destination);
        date = (TextView) itemView.findViewById(R.id.Date);
        profilePic = (ImageView) itemView.findViewById(R.id.ProfilePic);
    }

    @Override
    public void onClick(View v) {

    }
}
