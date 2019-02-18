package com.example.student_carpooling.filterTripsRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class FilterTripViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView Starting, Destination, Time, Date, Seats, Luggage, UserName;

    // what about buttons? show when expanded? maybe the seats and luggage info could be hidden also.


    public FilterTripViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        Starting = (TextView) itemView.findViewById(R.id.Starting);
        Destination = (TextView) itemView.findViewById(R.id.Destination);
        Time = (TextView) itemView.findViewById(R.id.Time);
        Date = (TextView) itemView.findViewById(R.id.Date);
        Seats = (TextView) itemView.findViewById(R.id.Seats);
        //Luggage = (TextView) itemView.findViewById(R.id.Luggage);
        UserName = (TextView) itemView.findViewById(R.id.username);


    }

    @Override
    public void onClick(View v) {
        // this tells which card was clicked
        // intent to then go inside that activity where we can get more detail on that trip...


    }
}

