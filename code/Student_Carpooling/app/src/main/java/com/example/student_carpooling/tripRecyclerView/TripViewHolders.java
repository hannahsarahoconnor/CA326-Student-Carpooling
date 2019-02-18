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


    public TripViewHolders(@NonNull View itemView, final TripAdapter.onTripListener TripList) {
        super(itemView);
        Starting = (TextView) itemView.findViewById(R.id.Starting);
        Destination = (TextView) itemView.findViewById(R.id.Destination);
        Time = (TextView) itemView.findViewById(R.id.Time);
        Date = (TextView) itemView.findViewById(R.id.Date);
        Seats = (TextView) itemView.findViewById(R.id.Seats);
        Luggage = (TextView) itemView.findViewById(R.id.Luggage);
        //UserName = (TextView) itemView.findViewById(R.id.username);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TripList != null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    TripList.onTripClick(position);
                }}
            }
        });

    }



}
