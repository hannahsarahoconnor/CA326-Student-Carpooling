package com.example.student_carpooling.ratingRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.R;
import com.example.student_carpooling.usersRecyclerView.User;
import com.example.student_carpooling.usersRecyclerView.UserViewHolders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserRatingAdapter extends RecyclerView.Adapter<UserRatingViewHolders> {

    private List<UserRating> list;
    private Context context;


    String CurrentUserID;

    FirebaseAuth mAuth;


    public UserRatingAdapter(List<UserRating> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public UserRatingAdapter() {
    }

    @NonNull
    @Override
    public UserRatingViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_card, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        UserRatingViewHolders uvh = new UserRatingViewHolders(layoutView);
        return uvh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserRatingViewHolders userRatingViewHolders, int i) {

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        String ProfilePicUrl = list.get(i).getPicUrl();
        if (!(ProfilePicUrl.equals("defaultPic"))) {
            Glide.with(context).load(ProfilePicUrl).into(userRatingViewHolders.UserProfilePic);
        }
        final String ID = list.get(i).getID();
        userRatingViewHolders.UserName.setText(list.get(i).getUsername());
        userRatingViewHolders.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                DatabaseReference ratingDB = FirebaseDatabase.getInstance().getReference().child("users").child(ID).child("Ratings");
                ratingDB.push().setValue(rating);
                ratingBar.setRating(rating);
            }
        });


    }





    @Override
    public int getItemCount() {
        return this.list.size();
    }
}
