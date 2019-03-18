package com.example.student_carpooling.findTripsRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class FindTripViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView username, fullname, seats, time, starting, destination, date;
    ImageView profilePic;

    FindTripViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        username = itemView.findViewById(R.id.Dusername);
        fullname = itemView.findViewById(R.id.Dfullname);
        seats = itemView.findViewById(R.id.Seats);
        time =  itemView.findViewById(R.id.Time);
        starting = itemView.findViewById(R.id.Starting);
        destination =  itemView.findViewById(R.id.Destination);
        date = itemView.findViewById(R.id.Date);
        profilePic = itemView.findViewById(R.id.ProfilePic);
    }

    @Override
    public void onClick(View v) {

    }
}
