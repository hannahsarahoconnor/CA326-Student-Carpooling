package com.example.student_carpooling.filterTripsRecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
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
import com.example.student_carpooling.FilteredTrips;
import com.example.student_carpooling.R;
import com.example.student_carpooling.tripRecyclerView.Trip;
import com.example.student_carpooling.tripRecyclerView.TripViewHolders;

import org.w3c.dom.Text;

import java.util.List;
public class FilterTripAdapter extends RecyclerView.Adapter<FilterTripViewHolders> {
    private List<FilterTrip> list;
    private Context context;

    Dialog dialog;




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

        final String _id = list.get(i).getID();
        final String _username = list.get(i).getUserName();
        final String _fullname = list.get(i).getName();
        final String _starting = list.get(i).getStarting();
        final String _destination = list.get(i).getDestination();
        final String _time = list.get(i).getTime();
        final String _date = list.get(i).getDate();
        final String _seats = list.get(i).getSeats();

        //populating the cards
        filtertripViewHolders.Destination.setText(_destination);
        filtertripViewHolders.UserName.setText(_username);
        //tripViewHolders.TripID.setText(list.get(i).getTripID());
        filtertripViewHolders.Starting.setText(_starting);
        filtertripViewHolders.Time.setText(_time);
        filtertripViewHolders.Date.setText(_date);
        filtertripViewHolders.Seats.setText(_seats);
        filtertripViewHolders.FullName.setText(_fullname);

        final String ProfilePicUrl = list.get(i).getProfilePicUrl();
        if(!(ProfilePicUrl.equals("defaultPic"))){
            Glide.with(context).load(ProfilePicUrl).into(filtertripViewHolders.profilePic);
        }


        dialog = new Dialog(context);
        dialog.setContentView(R.layout.driver_popup);
        TextView ClosePopUp = dialog.findViewById(R.id.close);
        TextView username = (TextView) dialog.findViewById(R.id.Dusername);
        TextView Fullname = (TextView) dialog.findViewById(R.id.Dfullname);
        TextView Luggage = (TextView) dialog.findViewById(R.id.Dluggage);
        TextView Note = (TextView) dialog.findViewById(R.id.Dnote);
        TextView Time = (TextView) dialog.findViewById(R.id.Dtime);
        TextView Date = (TextView) dialog.findViewById(R.id.Ddate);
        TextView Seats = (TextView) dialog.findViewById(R.id.Dseats);
        TextView Starting = (TextView) dialog.findViewById(R.id.Dstarting);
        TextView Destination = (TextView) dialog.findViewById(R.id.Ddestination);
        ImageView Ppic = (ImageView) dialog.findViewById(R.id.profilepic);

        Button message = (Button) dialog.findViewById(R.id.message);
        Button request = (Button) dialog.findViewById(R.id.request);

        username.setText(list.get(filtertripViewHolders.getAdapterPosition()).getUserName());
        Starting.setText(list.get(filtertripViewHolders.getAdapterPosition()).getStarting());
        Seats.setText(list.get(filtertripViewHolders.getAdapterPosition()).getSeats());
        Date.setText(list.get(filtertripViewHolders.getAdapterPosition()).getDate());
        Destination.setText(list.get(filtertripViewHolders.getAdapterPosition()).getDestination());
        Time.setText(list.get(filtertripViewHolders.getAdapterPosition()).getTime());

        Fullname.setText(list.get(filtertripViewHolders.getAdapterPosition()).getName());
        Luggage.setText(list.get(filtertripViewHolders.getAdapterPosition()).getLuggageCheck());
        Note.setText(list.get(filtertripViewHolders.getAdapterPosition()).getNote());

        final String PicUrl = list.get(filtertripViewHolders.getAdapterPosition()).getProfilePicUrl();
        if(!(PicUrl.equals("defaultPic"))){
            Glide.with(context).load(PicUrl).into(Ppic);
        }



        ClosePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send the request to the drawer
                Toast.makeText(context, "Starting new chat...", Toast.LENGTH_SHORT).show();
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
                //start chat activity with driver
                Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show();
            }
        });





        filtertripViewHolders.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
    }





    @Override
    public int getItemCount() {
        return this.list.size();
    }
}

