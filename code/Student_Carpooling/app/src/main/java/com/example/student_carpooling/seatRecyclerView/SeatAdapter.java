package com.example.student_carpooling.seatRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.student_carpooling.R;

import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatViewHolders> {

    private List<Seat> list;
    private Context context;


    public SeatAdapter(List<Seat> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public SeatAdapter(){
    }

    @NonNull
    @Override
    public SeatViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seat_card, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutView.setLayoutParams(lp);
        SeatViewHolders svh = new SeatViewHolders(layoutView);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolders seatViewHolders, int i) {

        seatViewHolders.seatNo.setText(list.get(i).getNumber());
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

}
