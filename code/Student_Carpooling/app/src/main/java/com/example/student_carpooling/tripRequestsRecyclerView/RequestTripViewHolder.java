package com.example.student_carpooling.tripRequestsRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class RequestTripViewHolder extends RecyclerView.ViewHolder {


    public TextView Starting, Destination, Note, Time, Date, Username, Fullname, Luggage;
    public ImageView profilePic, profileIcon, messageIcon,delete;

    public RequestTripViewHolder(@NonNull View itemView) {
        super(itemView);

        Username = (TextView) itemView.findViewById(R.id.Dusername);
        Fullname = (TextView) itemView.findViewById(R.id.Dfullname);;
        Time = (TextView) itemView.findViewById(R.id.Time);
        Starting = (TextView) itemView.findViewById(R.id.Starting);
        Destination = (TextView) itemView.findViewById(R.id.Destination);
        Date = (TextView) itemView.findViewById(R.id.Date);
        profilePic = (ImageView) itemView.findViewById(R.id.ProfilePic);
        Luggage = (TextView) itemView.findViewById(R.id.LuggageCheck);
        Note = (TextView) itemView.findViewById(R.id.Note);
        profileIcon = (ImageView) itemView.findViewById(R.id.profile);
        messageIcon= (ImageView) itemView.findViewById(R.id.message);
        delete= (ImageView) itemView.findViewById(R.id.delete);

    }

}
