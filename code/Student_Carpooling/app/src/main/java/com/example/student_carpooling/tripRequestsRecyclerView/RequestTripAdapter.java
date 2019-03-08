package com.example.student_carpooling.tripRequestsRecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.ChatActivity;
import com.example.student_carpooling.R;
import com.example.student_carpooling.SendNotification;
import com.example.student_carpooling.UserProfile;
import com.example.student_carpooling.passengerTripsRecyclerView.PassengerTripViewHolders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestTripAdapter extends RecyclerView.Adapter<RequestTripViewHolder>{

    private List<RequestTrip> list;
    private Context context;

    public static final int PASSENGER = 0;
    public static final int DRIVER = 1;

    public RequestTripAdapter(List<RequestTrip> list, Context context){
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public RequestTripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i == DRIVER){
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.request_trip_cards, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
            return new RequestTripViewHolder(layoutView);


        }
        else{
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_request_trips_cards, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
            return new RequestTripViewHolder(layoutView);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestTripViewHolder requestTripViewHolders, int i) {

        final String _Fullname = list.get(i).getFullname();
        final String _Username = list.get(i).getUsername();
        final String ProfileUrl = list.get(i).getProfilePicUrl();
        final String _Destination = list.get(i).getDestination();
        final String _Time = list.get(i).getTime();
        final String _Date = list.get(i).getDate();
        final String _Note = list.get(i).getNote();
        final String _Starting = list.get(i).getStarting();
        final String _ID = list.get(i).getID();
        final String _Luggage = list.get(i).getLuggageCheck();
        final String RequestID = list.get(i).getRequestID();

        requestTripViewHolders.Destination.setText(_Destination);
        requestTripViewHolders.Starting.setText(_Starting);
        requestTripViewHolders.Time.setText(_Time);
        requestTripViewHolders.Date.setText(_Date);
        requestTripViewHolders.Luggage.setText(_Luggage);

        requestTripViewHolders.Note.setText(_Note);


        if(list.get(i).getType().equals("Driver")){
            //more options

            requestTripViewHolders.Username.setText(_Username);
            requestTripViewHolders.Fullname.setText(_Fullname);

            if (!(ProfileUrl.equals("defaultPic"))) {
                Glide.with(context).load(ProfileUrl).into(requestTripViewHolders.profilePic);

            }



            requestTripViewHolders.profileIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfile.class);
                    intent.putExtra("ID", _ID);
                    context.startActivity(intent);
                }
            });

            requestTripViewHolders.messageIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("Username", _Username);
                    intent.putExtra("ID", _ID);
                    intent.putExtra("Fullname",_Fullname);
                    intent.putExtra("ProfilePicURL", ProfileUrl);
                    Toast.makeText(context, "Starting Chat with " + _Username, Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @Override
    public int getItemViewType(int position) {
        // check to see if receiver or sender

        if(list.get(position).getType().equals("Driver")){
            return DRIVER;
        }
        else{
            return PASSENGER;
        }


        //return super.getItemViewType(position);

    }
}
