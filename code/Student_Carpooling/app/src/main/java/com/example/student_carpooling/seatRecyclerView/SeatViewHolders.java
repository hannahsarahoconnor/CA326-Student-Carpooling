package com.example.student_carpooling.seatRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.student_carpooling.R;

public class SeatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView seatNo;

    SeatViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);


        seatNo = itemView.findViewById(R.id.Seats);
    }

    @Override
    public void onClick(View v) {

    }
}
