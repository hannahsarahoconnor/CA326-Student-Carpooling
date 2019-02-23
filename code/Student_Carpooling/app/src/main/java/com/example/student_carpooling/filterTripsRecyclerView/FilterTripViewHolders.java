package com.example.student_carpooling.filterTripsRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class FilterTripViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView profilePic;
    //could use the layout to attach the on click listener
    public TextView Starting, Destination, Time, Date, Seats, Luggage, UserName, FullName, Note;
    Button request;


    // what about buttons? show when expanded? maybe the seats and luggage info could be hidden also.


    public FilterTripViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        Starting = (TextView) itemView.findViewById(R.id.Starting);
        Destination = (TextView) itemView.findViewById(R.id.Destination);
        Time = (TextView) itemView.findViewById(R.id.Time);
        Date = (TextView) itemView.findViewById(R.id.Date);
        Seats = (TextView) itemView.findViewById(R.id.Seats);
        profilePic = (ImageView) itemView.findViewById(R.id.ProfilePic);
<<<<<<< HEAD
        //Luggage = (TextView) itemView.findViewById(R.id.Luggage);
=======
        UserName = (TextView) itemView.findViewById(R.id.Dusername);
        FullName = (TextView) itemView.findViewById(R.id.Dfullname);



>>>>>>> a55d60a77aa0c4f8f9f61a406944db63df7d98b2


    }

    @Override
    public void onClick(View v) {
        // this tells which card was clicked
        // intent to then go inside that activity where we can get more detail on that trip...

    }
}