package com.example.student_carpooling.tripRequestsRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class RequestTripViewHolder extends RecyclerView.ViewHolder {


    public TextView Starting, Destination, Note, Time, Date, Username, Fullname, Luggage;
    ImageView profilePic, profileIcon, messageIcon;

    RequestTripViewHolder(@NonNull View itemView) {
        super(itemView);

        Username =  itemView.findViewById(R.id.Dusername);
        Fullname = itemView.findViewById(R.id.Dfullname);
        Time =  itemView.findViewById(R.id.Time);
        Starting = itemView.findViewById(R.id.Starting);
        Destination = itemView.findViewById(R.id.Destination);
        Date = itemView.findViewById(R.id.Date);
        profilePic = itemView.findViewById(R.id.ProfilePic);
        Luggage =  itemView.findViewById(R.id.LuggageCheck);
        Note = itemView.findViewById(R.id.Note);
        profileIcon = itemView.findViewById(R.id.profile);
        messageIcon= itemView.findViewById(R.id.message);

    }

}
