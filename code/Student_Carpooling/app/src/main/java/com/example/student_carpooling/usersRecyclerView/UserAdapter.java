package com.example.student_carpooling.usersRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.R;
import com.example.student_carpooling.filterTripsRecyclerView.FilterTrip;
import com.example.student_carpooling.filterTripsRecyclerView.FilterTripViewHolders;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolders> implements Filterable {

    private List<User> list;
    private List<User> listFull;
    private Context context;

    public UserAdapter(List<User> list, Context context){
        this.list = list;
        listFull = new ArrayList<>(list);
        this.context = context;
    }


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
    public void onBindViewHolder(@NonNull UserViewHolders userViewHolders, int i) {
        userViewHolders.UserName.setText(list.get(i).getUserName());

        if(userViewHolders.UserProfilePic.equals("default")){
            userViewHolders.UserProfilePic.setImageResource(R.mipmap.ic_launcher_round);
        }
        else{
            Glide.with(context).load(list.get(i).getProfilePicUrl()).into(userViewHolders.UserProfilePic);}


    }

    @Override
    public int getItemCount() {
       return this.list.size();
    }



    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //return filtered results
            List<User> filtered = new ArrayList<>();

            if(constraint == null ||constraint.length()==0){
                filtered.addAll(listFull);
            }
            else{
                //if something was entered into the search bar
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(User user : listFull){
                    //could change contains to startWith..
                    if (user.getUserName().toLowerCase().contains(filterPattern)){
                        filtered.add(user);
                    }
                }
            }

            FilterResults Results = new FilterResults();
            Results.values = filtered;
            return Results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
           list.clear();
           list.addAll((List)results.values);

           //refresh list
            notifyDataSetChanged();
        }
    };
}
