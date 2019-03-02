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
import com.example.student_carpooling.PassengerLocation;
import com.example.student_carpooling.R;
import com.example.student_carpooling.SendNotification;
import com.example.student_carpooling.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsViewHolders> {

    private List<Requests> list;
    private Context context;


    public RequestsAdapter(List<Requests> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public RequestsAdapter() {

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
    public void onBindViewHolder(@NonNull final RequestsViewHolders requestsViewHolders, int i) {
        requestsViewHolders.UserName.setText(list.get(i).getUsername());

        final String url = list.get(i).getProfilePicUrl();
        if (!url.equals("defaultPic")) {
            Glide.with(context).load(url).into(requestsViewHolders.ProfilePic);
        }

        final String _notificationKey = list.get(i).getNotificationKey();
        final String _id = list.get(i).getUserID();
        final String _driverUsername = list.get(i).getDriverUsername();
        final String _username = list.get(i).getUsername();
        final String _fullname = list.get(i).getFullname();
        final float lat = list.get(i).getLatitude();
        final float lon = list.get(i).getLongitude();
        final String TripID = list.get(i).getTripID();
        requestsViewHolders.UserName.setText(_username);


        requestsViewHolders.ProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //view their profile
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("ID", _id);
                context.startActivity(intent);

            }
        });

        requestsViewHolders.DeclineIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                //remove request and add to declined list, to prevent user from constantly requesting
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference DeclineDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(firebaseUser.getUid()).child(TripID).child("Declined").child(_id);
                Map DeclineInfo = new HashMap();
                DeclineInfo.put("ID", _id);
                DeclineDB.setValue(DeclineInfo);

                //remove from request list
                DatabaseReference RequestsDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(firebaseUser.getUid()).child(TripID).child("TripRequests").child(_id);
                RequestsDB.removeValue();
                Toast.makeText(context, "Request declined for  " + _username, Toast.LENGTH_SHORT).show();
                new SendNotification(_driverUsername + " declined your request", "Student Carpooling", _notificationKey);
                //refresh the activity
                list.remove(requestsViewHolders.getAdapterPosition());
                notifyDataSetChanged();

                // get reference and remove UserDb.removeValue();
            }
        });

        requestsViewHolders.MessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Starting new chat..." + _username, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("Username", _username);
                intent.putExtra("ID", _id);
                intent.putExtra("Fullname", _fullname);
                intent.putExtra("ProfilePicURL", url);
                context.startActivity(intent);
            }
        });

        requestsViewHolders.LocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //view their location on the map
                Toast.makeText(context, "retrieving locations and forming route...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, PassengerLocation.class);
                intent.putExtra("Username", _username);
                intent.putExtra("ID", _id);
                intent.putExtra("ProfilePicURL", url);
                intent.putExtra("Lat", lat);
                intent.putExtra("Lon", lon);
                intent.putExtra("NotificationKey", _notificationKey);
                context.startActivity(intent);
            }
        });

        requestsViewHolders.AcceptIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //remove from requests and add to trip passengers
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference SeatCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(firebaseUser.getUid()).child(TripID);
                SeatCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //data originally added is kept in this format
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if (map.get("Seats") != null) {
                                int seatsNo = Integer.parseInt(map.get("Seats").toString());
                                if (seatsNo == 0) {
                                    Toast.makeText(context, "You have no seats left, remove an existing passenger to accept this request", Toast.LENGTH_LONG).show();
                                    return;
                                } else {
                                    DatabaseReference PassengersDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(firebaseUser.getUid()).child(TripID).child("Passengers").child(_id);
                                    Map PassengerInfo = new HashMap();
                                    PassengerInfo.put("Username", _username);
                                    PassengerInfo.put("Fullname",_fullname);
                                    PassengerInfo.put("profileImageUrl", url);
                                    PassengerInfo.put("NotificationKey", _notificationKey);
                                    PassengerInfo.put("lat", lat);
                                    PassengerInfo.put("lon", lon);
                                    PassengersDB.setValue(PassengerInfo);
                                    //remove from requested
                                    DatabaseReference RequestsDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(firebaseUser.getUid()).child(TripID).child("TripRequests").child(_id);
                                    RequestsDB.removeValue();
                                    Toast.makeText(context, "Request accepted for  " + _username, Toast.LENGTH_SHORT).show();
                                    //send notification to passenger
                                    new SendNotification(_driverUsername + " accepted your request", "Student Carpooling", _notificationKey);

                                    //add the trip info to passenger user info ...
                                    DatabaseReference PassengerInfoDB = FirebaseDatabase.getInstance().getReference().child("users").child(_id).child("Trips").child(firebaseUser.getUid()).child(TripID);
                                    Map TripInfo = new HashMap();
                                    TripInfo.put("TripID",TripID);
                                    PassengerInfoDB.setValue(TripInfo);


                                    //update the seat count...
                                    DatabaseReference TripDb = FirebaseDatabase.getInstance().getReference().child("TripForms").child(firebaseUser.getUid()).child(TripID);
                                    //get the current seat no, converter to int and -1
                                    TripDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                                if (map.get("Seats") != null) {
                                                    int seats = Integer.parseInt(map.get("Seats").toString());
                                                    seats--;
                                                    FirebaseDatabase.getInstance().getReference().child("TripForms").child(firebaseUser.getUid()).child(TripID).child("Seats").setValue(seats);
                                                    Toast.makeText(context, "" + seats, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    //refresh the activity
                                    list.remove(requestsViewHolders.getAdapterPosition());
                                    notifyDataSetChanged();


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

    }
    @Override
    public int getItemCount() {
        return this.list.size();
    }


}
