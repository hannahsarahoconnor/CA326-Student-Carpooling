package com.example.student_carpooling.requestsRecyclerView;

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

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsViewHolders> {

    private List<Requests> list;
    private Context context;


    public RequestsAdapter(List<Requests> list, Context context){
        this.list = list;
        this.context = context;
    }

    public RequestsAdapter(){

    }

    @NonNull
    @Override
    public RequestsViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.requests_card, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        RequestsViewHolders rvh = new RequestsViewHolders(layoutView);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsViewHolders requestsViewHolders, int i) {
        requestsViewHolders.UserName.setText(list.get(i).getUsername());
        final String url = list.get(i).getProfilePicUrl();

        if(!url.equals("defaultPic")) {
            Glide.with(context).load(url).into(requestsViewHolders.ProfilePic);
        }

        final String _id = list.get(i).getUserID();
        final String _username = list.get(i).getUsername();
        final String _fullname = list.get(i).getFullname();


        requestsViewHolders.LocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //view their location on the map

            }
        });

        requestsViewHolders.AcceptIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //remove from requests and add to trip passengers

            }
        });

        requestsViewHolders.ProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //view their profile

            }
        });

        requestsViewHolders.DeclineIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove request and add to declined list, to prevent user from constantly requesting
            }
        });

        requestsViewHolders.MessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Starting new chat..." +_username, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("Username",_username);
                intent.putExtra("ID", _id);
                intent.putExtra("Fullname", _fullname);
                intent.putExtra("ProfilePicURL", url);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }


}
