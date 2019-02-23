package com.example.student_carpooling.usersRecyclerView;

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

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolders> {

    private List<User> list;
    private Context context;




    public UserAdapter(List<User> list, Context context){
        this.list = list;
        this.context = context;
    }

    public UserAdapter(){}


    @NonNull
    @Override
    public UserViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_cards, null, false);
        RecyclerView.LayoutParams lp  = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        UserViewHolders uvh = new UserViewHolders(layoutView);
        return uvh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolders userViewHolders,int i) {
        final String Username = list.get(i).getUserName();
        final String ProfilePicUrl = list.get(i).getProfilePicUrl();
        final String ID = list.get(i).getID();
        final String Fullname = list.get(i).getFullname();
        userViewHolders.UserName.setText(Username);
        userViewHolders.FullName.setText(Fullname);

        if(!(ProfilePicUrl.equals("defaultPic"))){
            Glide.with(context).load(ProfilePicUrl).into(userViewHolders.UserProfilePic);
        }


            userViewHolders.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("Username",Username);
                    intent.putExtra("ID", ID);
                    intent.putExtra("Fullname", Fullname);
                    intent.putExtra("ProfilePicURL", ProfilePicUrl);
                    Toast.makeText(context,"Starting Chat with " +Username,Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);

                }
            });
    }

    @Override
    public int getItemCount() {
       return this.list.size();
    }


}
