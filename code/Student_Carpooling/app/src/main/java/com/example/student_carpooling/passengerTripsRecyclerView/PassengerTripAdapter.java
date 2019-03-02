package com.example.student_carpooling.passengerTripsRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.student_carpooling.DriverTripItem;
import com.example.student_carpooling.PassengerTripItem;
import com.example.student_carpooling.R;

import java.util.List;

public class PassengerTripAdapter  extends RecyclerView.Adapter<PassengerTripViewHolders>{

    private List<PassengerTrip> list;
    private Context context;



    public PassengerTripAdapter(List<PassengerTrip> list, Context context) {
        this.list = list;
        this.context = context;}

    public PassengerTripAdapter(){}

    @NonNull
    @Override
    public PassengerTripViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.passenger_trips, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        PassengerTripViewHolders pvh = new PassengerTripViewHolders(layoutView);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerTripViewHolders passengerTripViewHolders, int i) {

        final String Starting = list.get(i).getStarting();
        final String Destination = list.get(i).getDestination();
        final String Time = list.get(i).getTime();
        final String Date = list.get(i).getDate();
        passengerTripViewHolders.Destination.setText(Destination);
        passengerTripViewHolders.Starting.setText(Starting);
        passengerTripViewHolders.Time.setText(Time);
        passengerTripViewHolders.Date.setText(Date);


        final String DriverName = list.get(i).getDriverName();
        final String DriverUsername = list.get(i).getDriverUsername();
        final String TripId = list.get(i).getTripId();
        final String DriverId = list.get(i).getDriverID();
        final String DriverPicUrl = list.get(i).getDriverPicUrl();
        final double lat = list.get(i).getLat();
        final double lon = list.get(i).getLon();
        final double dstlat = list.get(i).getDstlat();
        final double dstlon = list.get(i).getDstlon();
        final String NotificationKey = list.get(i).getNotificationKey();

        passengerTripViewHolders.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PassengerTripItem.class);
                intent.putExtra("DriverUsername", DriverUsername);
                intent.putExtra("DriverName", DriverName);
                intent.putExtra("TripID", TripId);
                intent.putExtra("DriverID", DriverId);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("dstlat", dstlat);
                intent.putExtra("dstlon", dstlon);
                intent.putExtra("PicURL", DriverPicUrl);
                intent.putExtra("Date", Date);
                intent.putExtra("Time", Time);
                intent.putExtra("Starting", Starting);
                intent.putExtra("Destination", Destination);
                intent.putExtra("NotificationKey", NotificationKey);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
}
