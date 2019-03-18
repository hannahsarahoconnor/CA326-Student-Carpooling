package com.example.student_carpooling.tripRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.student_carpooling.ChatActivity;
import com.example.student_carpooling.DriverTripItem;
import com.example.student_carpooling.R;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripViewHolders>{
    private List<Trip> list;
    private Context context;

    public TripAdapter(List<Trip> list, Context context){
        this.list = list;
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TripViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_cards, viewGroup, false);
       // this forces it to match parent in width and wrap content in its height.
        RecyclerView.LayoutParams lp  = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new TripViewHolders(layoutView);

    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolders tripViewHolders, final int i) {
     //populating the cards
        tripViewHolders.Destination.setText(list.get(i).getDestination());
        //tripViewHolders.UserName.setText(list.get(i).getUserName());
        tripViewHolders.Starting.setText(list.get(i).getStarting());
        tripViewHolders.Time.setText(list.get(i).getTime());
        tripViewHolders.Date.setText(list.get(i).getDate());
        tripViewHolders.Seats.setText(list.get(i).getSeats());
        tripViewHolders.Luggage.setText(list.get(i).getLuggageCheck());


        tripViewHolders.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, ""+list.get(i).getTripID(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DriverTripItem.class);
                intent.putExtra("TripID", list.get(i).getTripID());
                intent.putExtra("DriverID", list.get(i).getDriverID());
                intent.putExtra("Destination", list.get(i).getDestination());
                intent.putExtra("Starting", list.get(i).getStarting());
                intent.putExtra("Time", list.get(i).getTime());
                intent.putExtra("Date", list.get(i).getDate());
                intent.putExtra("Seats", list.get(i).getSeats());
                intent.putExtra("Luggage", list.get(i).getLuggageCheck());
                intent.putExtra("Username", list.get(i).getUserName());
                intent.putExtra("DstLat",list.get(i).getDstLat());
                intent.putExtra("DstLon", list.get(i).getDstLon());
                context.startActivity(intent);
            }
        });
        }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
}
