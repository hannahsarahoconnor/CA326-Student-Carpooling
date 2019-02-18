package com.example.student_carpooling.tripRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student_carpooling.R;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripViewHolders>{
    private List<Trip> list;
    private Context context;
    private onTripListener TripListener;

    public interface onTripListener {
        void onTripClick(int position);
    }

    public void setTripListener(onTripListener tripListener){
        TripListener = tripListener;
    }


    public TripAdapter(List<Trip> list, Context context){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public TripViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_cards, null, false);
       // this forces it to match parent in width and wrap content in its height.
        RecyclerView.LayoutParams lp  = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new TripViewHolders(layoutView,TripListener);

    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolders tripViewHolders, final int i) {
     //populating the cards
        tripViewHolders.Destination.setText(list.get(i).getDestination());
        //tripViewHolders.UserName.setText(list.get(i).getUserName());
        //tripViewHolders.TripID.setText(list.get(i).getTripID());
        tripViewHolders.Starting.setText(list.get(i).getStarting());
        tripViewHolders.Time.setText(list.get(i).getTime());
        tripViewHolders.Date.setText(list.get(i).getDate());
        tripViewHolders.Seats.setText(list.get(i).getSeats());
        tripViewHolders.Luggage.setText(list.get(i).getLuggageCheck());
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
}
