package com.example.student_carpooling.findTripsRecyclerView;

import android.app.Dialog;
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
import com.example.student_carpooling.R;
import com.example.student_carpooling.RequestMapActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FindTripAdapter extends RecyclerView.Adapter<FindTripViewHolders> {


    private List<FindTrip> list;
    private Context context;

    Dialog dialog;

    Boolean result=false;


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

        if(!url.equals("defaultPic")) {
            Glide.with(context).load(url).into(findTripViewHolders.profilePic);
        }

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.driver_popup);
        TextView ClosePopUp = dialog.findViewById(R.id.close);
        TextView username = (TextView) dialog.findViewById(R.id.DUserName);
        //TextView Fullname = (TextView) dialog.findViewById(R.id.Dfullname);
        TextView Luggage = (TextView) dialog.findViewById(R.id.Dluggage);
        TextView Note = (TextView) dialog.findViewById(R.id.Dnote);
        TextView Time = (TextView) dialog.findViewById(R.id.Dtime);
        TextView Date = (TextView) dialog.findViewById(R.id.Ddate);
        TextView Seats = (TextView) dialog.findViewById(R.id.Dseats);
        TextView Starting = (TextView) dialog.findViewById(R.id.Dstarting);
        TextView Destination = (TextView) dialog.findViewById(R.id.Ddestination);
        ImageView Ppic = (ImageView) dialog.findViewById(R.id.UserProfilePic);
        ImageView ProfileIcon = (ImageView) dialog.findViewById(R.id.profile);
        ImageView MessageIcon = (ImageView) dialog.findViewById(R.id.message);
        final Button request = (Button) dialog.findViewById(R.id.request);

        username.setText(list.get(findTripViewHolders.getAdapterPosition()).getUsername());
        Starting.setText(list.get(findTripViewHolders.getAdapterPosition()).getStarting());
        Seats.setText(list.get(findTripViewHolders.getAdapterPosition()).getSeats());
        Date.setText(list.get(findTripViewHolders.getAdapterPosition()).getDate());
        Destination.setText(list.get(findTripViewHolders.getAdapterPosition()).getDestination());


        Time.setText(list.get(findTripViewHolders.getAdapterPosition()).getTime());

       // Fullname.setText(list.get(findTripViewHolders.getAdapterPosition()).getFullname());
        Luggage.setText(list.get(findTripViewHolders.getAdapterPosition()).getLuggage());
        Note.setText(list.get(findTripViewHolders.getAdapterPosition()).getNote());

        final String PicUrl = list.get(findTripViewHolders.getAdapterPosition()).getProfilePicUrl();
        if(!(PicUrl.equals("defaultPic"))){
            Glide.with(context).load(PicUrl).into(Ppic);
        }



        ClosePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final String _id = list.get(i).getID();
        final String _username = list.get(i).getUsername();
        final String _fullname = list.get(i).getFullname();
        final String _tripId = list.get(i).getTripID();
        final String CurrentId = list.get(i).getCurrentID();




        MessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send the request to the drawer
                Toast.makeText(context, "Starting new chat..." +_id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("Username",_username);
                intent.putExtra("ID", _id);
                intent.putExtra("Fullname", _fullname);
                intent.putExtra("ProfilePicURL", PicUrl);
                context.startActivity(intent);

            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if user has already requested the trip,stop them from requesting again
              RequestCheck(_id,_tripId,CurrentId);
              if(result){
                  Toast.makeText(context, "You have already sent a request for this trip, please wait until the driver has accepted/declined", Toast.LENGTH_LONG).show();
              }
              else{
                  Intent intent = new Intent(context, RequestMapActivity.class);
                  intent.putExtra("DriverID", _id);
                  intent.putExtra("TripID", _tripId);
                  Toast.makeText(context, _id, Toast.LENGTH_SHORT).show();
                  context.startActivity(intent);
              }
            }
        });


        findTripViewHolders.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
    }

    public void RequestCheck(String driverid, String tripid, final String currentid){
        DatabaseReference requestCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(driverid).child(tripid).child("Requests");
        //requestCheck.
        requestCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        String KeyCheck = id.getKey();
                        if (KeyCheck.equals(currentid)) {
                            result = true;
                        }
                    }
                }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    public int getItemCount() {
        return this.list.size();
    }
}
