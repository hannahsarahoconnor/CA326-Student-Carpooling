package com.example.student_carpooling.tripRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.student_carpooling.R;

//this is associated with the layout of the cards of the recycler view


public class TripViewHolders extends RecyclerView.ViewHolder {


    public TextView Starting, Destination, Time, Date, Seats, Luggage, UserName;

    // what about buttons? show when expanded? maybe the seats and luggage info could be hidden also.


    TripViewHolders(@NonNull View itemView) {
        super(itemView);
        Starting = itemView.findViewById(R.id.Starting);
        Destination =  itemView.findViewById(R.id.Destination);
        Time = itemView.findViewById(R.id.Time);
        Date =  itemView.findViewById(R.id.Date);
        Seats = itemView.findViewById(R.id.Seats);
        Luggage = itemView.findViewById(R.id.Luggage);
        //UserName = (TextView) itemView.findViewById(R.id.username);
    }


}
