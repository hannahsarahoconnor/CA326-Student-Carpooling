package com.example.student_carpooling.filterTripsRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.R;
import com.example.student_carpooling.tripRecyclerView.Trip;
import com.example.student_carpooling.tripRecyclerView.TripViewHolders;

import java.util.List;
public class FilterTripAdapter extends RecyclerView.Adapter<FilterTripViewHolders> {
    private List<FilterTrip> list;
    private Context context;


    public FilterTripAdapter(List<FilterTrip> list, Context context){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public FilterTripViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.findtrip_cards, null, false);
        // this forces it to match parent in width and wrap content in its height.
        RecyclerView.LayoutParams lp  = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        FilterTripViewHolders tvh = new FilterTripViewHolders(layoutView);

        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterTripViewHolders filtertripViewHolders, final int i) {
        //populating the cards
        filtertripViewHolders.UserName.setText(list.get(i).getUserName());

        // ProfilePicUrl = map.get("profileImageUrl").toString();
        //                        Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
}





