package com.example.student_carpooling.passengerTripsRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class PassengerTripViewHolders extends RecyclerView.ViewHolder{

    public TextView Starting, Destination, Time, Date;

    public PassengerTripViewHolders(@NonNull View itemView) {
        super(itemView);

        Starting = itemView.findViewById(R.id.Starting);
        Destination = itemView.findViewById(R.id.Destination);
        Time = itemView.findViewById(R.id.Time);
        Date = itemView.findViewById(R.id.Date);


    }
}
