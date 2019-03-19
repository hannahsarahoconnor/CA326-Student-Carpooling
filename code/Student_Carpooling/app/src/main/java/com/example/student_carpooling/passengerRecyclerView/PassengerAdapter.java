package com.example.student_carpooling.passengerRecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.ChatActivity;
import com.example.student_carpooling.PassengerLocation;
import com.example.student_carpooling.R;
import com.example.student_carpooling.Notification;
import com.example.student_carpooling.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerViewHolders> {


    private List<Passenger> list;
    private Context context;
    private String UserID;


    public PassengerAdapter(List<Passenger> list, Context context){
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public PassengerViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.passenger_cards, viewGroup, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new PassengerViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PassengerViewHolders passengerViewHolders, int i) {
        FirebaseAuth mAuth;
        FirebaseUser firebaseUser;
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            UserID = mAuth.getCurrentUser().getUid();
        }

        final String Fullname = list.get(i).getFullname();
        final String Username = list.get(i).getUserName();
        final String ProfilePicUrl = list.get(i).getProfilePicUrl();
        final String _driverUsername = list.get(i).getDriverUN();
        final String ID = list.get(i).getID();
        final double lat = list.get(i).getLat();
        final double lon = list.get(i).getLon();
        final String _notificationKey = list.get(i).getNotificationKey();
        final double dlat = list.get(i).getDLat();
        final double dlon = list.get(i).getDLon();
        final String TripID = list.get(i).getTripID();

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
                intent.putExtra("Fullname",Fullname);
                intent.putExtra("ProfilePicURL", ProfilePicUrl);
                context.startActivity(intent);
            }
        });

        passengerViewHolders.ProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(context, UserProfile.class);
               intent.putExtra("ID", ID);
               context.startActivity(intent);
            }
        });


       if(list.get(i).getType().equals("Passenger")){
           passengerViewHolders.LocationIcon.setVisibility(View.GONE);
           passengerViewHolders.DeleteIcon.setVisibility(View.GONE);
       }else {


           passengerViewHolders.LocationIcon.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   //Toast.makeText(context, "retrieving locations and forming route...", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(context, PassengerLocation.class);
                   intent.putExtra("Username", Username);
                   intent.putExtra("ID", ID);
                   intent.putExtra("TripID",TripID);
                   intent.putExtra("Lat", lat);
                   intent.putExtra("Lon", lon);
                   intent.putExtra("DLat", dlat);
                   intent.putExtra("DLon", dlon);
                   //intent.putExtra("Lat", lat);
                   //Toast.makeText(context, ""+lat, Toast.LENGTH_SHORT).show();
                   //intent.putExtra("Lon", lon);
                   intent.putExtra("NotificationKey", _notificationKey);
                   context.startActivity(intent);

               }
           });


           passengerViewHolders.DeleteIcon.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   //show a dialog..
                   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                   dialog.setTitle("Are you sure you want to remove this passenger?");
                   dialog.setMessage("By Doing this, all the passengers will be notified of their removal, will be not longer be apart of your carpool and will not be able to request this trip again");
                   dialog.setPositiveButton("Remove Passenger", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           DatabaseReference DeclineDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID).child("Declined").child(ID);
                           Map<String, String> DeclineInfo = new HashMap<>();
                           DeclineInfo.put("ID", ID);
                           DeclineDB.setValue(DeclineInfo);

                           DatabaseReference PassDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID).child("Passengers").child(ID);
                           PassDB.removeValue();
                           //update the passenger view
                           DatabaseReference PassTripInfo = FirebaseDatabase.getInstance().getReference().child("users").child(ID).child("Trips").child(UserID).child(TripID);
                           PassTripInfo.child("Removed").setValue(1);
                           Toast.makeText(context, Username + " has been deleted from your trip", Toast.LENGTH_LONG).show();

                           new Notification(_driverUsername + " has removed you from their carpool", "Student Carpooling", _notificationKey);
                           //refresh the activity
                           list.remove(passengerViewHolders.getAdapterPosition());
                           notifyDataSetChanged();

                           //send notification to passengers of the cancellation

                           //update seat count
                           DatabaseReference TripDb = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID);
                           //get the current seat no, converter to int and -1
                           TripDb.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   if (dataSnapshot.exists()) {
                                       GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                       };
                                       Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                                       if (map != null) {
                                           if (map.get("Seats") != null) {
                                               String SeatStr = Objects.requireNonNull(map.get("Seats")).toString();
                                               int seats = (Integer.parseInt(SeatStr));
                                               seats++;
                                               FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(TripID).child("Seats").setValue(seats);
                                           }
                                       }
                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });



                       }


                   });

                   dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   });

                   AlertDialog alertDialog = dialog.create();
                   alertDialog.show();

               }
           });

       }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
}
