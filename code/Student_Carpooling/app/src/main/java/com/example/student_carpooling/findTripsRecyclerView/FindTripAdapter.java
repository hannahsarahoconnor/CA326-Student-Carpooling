package com.example.student_carpooling.findTripsRecyclerView;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.ChatActivity;
import com.example.student_carpooling.FindTrips;
import com.example.student_carpooling.R;
import com.example.student_carpooling.RequestMapActivity;
import com.example.student_carpooling.SendLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

public class FindTripAdapter extends RecyclerView.Adapter<FindTripViewHolders> {


    private List<FindTrip> list;
    private Context context;

    Dialog dialog;


    public FindTripAdapter(List<FindTrip> list, Context context){
        this.context = context;
        this.list = list;


    }


    @NonNull
    @Override
    public FindTripViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //this controlls the layout
        View vh = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.findtrip_cards, null, false);
        RecyclerView.LayoutParams lp  = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        vh.setLayoutParams(lp);
        FindTripViewHolders rcv = new FindTripViewHolders((vh));
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull FindTripViewHolders findTripViewHolders, int i) {
        findTripViewHolders.username.setText(list.get(i).getUsername());
        findTripViewHolders.fullname.setText(list.get(i).getFullname());
        findTripViewHolders.seats.setText(list.get(i).getSeats());
        findTripViewHolders.date.setText(list.get(i).getDate());
        //String _time = String.valueOf(list.get(i).getTime());
        findTripViewHolders.time.setText(list.get(i).getTime());
        findTripViewHolders.destination.setText(list.get(i).getDestination());
        findTripViewHolders.starting.setText(list.get(i).getStarting());
        String url = list.get(i).getProfilePicUrl();

        //convert time back to string..

        // Integer time = list.get(i).getTime(); // 900
        //  Integer hours = time/60;
        //  Integer mins =  time % 60;
        //  String newTime = String.valueOf(hours) + ":" + String.valueOf(mins);
        // findTripViewHolders.time.setText(newTime);

        if (!url.equals("defaultPic")) {
            Glide.with(context).load(url).into(findTripViewHolders.profilePic);
        }



        findTripViewHolders.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Intent intent = new Intent(context, SendLocation.class);
                    Intent intent = new Intent(context, RequestMapActivity.class);
                    context.startActivity(intent);

                }
            });
        }




    @Override
    public int getItemCount() {
        return this.list.size();
    }
}
