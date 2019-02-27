package com.example.student_carpooling.passengerRecyclerView;

import android.content.Context;
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
import com.example.student_carpooling.usersRecyclerView.User;
import com.example.student_carpooling.usersRecyclerView.UserViewHolders;

import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerViewHolders> {


    private List<Passenger> list;
    private Context context;


    public PassengerAdapter(List<Passenger> list, Context context){
        this.list = list;
        this.context = context;
    }

    public PassengerAdapter(){

    }


    @NonNull
    @Override
    public PassengerViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.passenger_cards, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        PassengerViewHolders pvh = new PassengerViewHolders(layoutView);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerViewHolders passengerViewHolders, int i) {
        final String Username = list.get(i).getUserName();
        final String ProfilePicUrl = list.get(i).getProfilePicUrl();
        final String ID = list.get(i).getID();

        //userViewHolders.FullName.setText(Fullname);

        if (!(ProfilePicUrl.equals("defaultPic"))) {
            Glide.with(context).load(ProfilePicUrl).into(passengerViewHolders.ProfilePic);
        }
        passengerViewHolders.UserName.setText(Username);

        passengerViewHolders.MessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("Username", Username);
                intent.putExtra("ID", ID);
                intent.putExtra("ProfilePicURL", ProfilePicUrl);
                Toast.makeText(context, "Starting Chat with " + Username, Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        });

        passengerViewHolders.ProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(context, UserProfile.class);
                //intent.putExtra("ID", ID);
               // context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
}