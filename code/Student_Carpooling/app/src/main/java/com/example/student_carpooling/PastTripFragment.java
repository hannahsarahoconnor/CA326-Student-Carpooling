package com.example.student_carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student_carpooling.tripRecyclerView.Trip;
import com.example.student_carpooling.tripRecyclerView.TripAdapter;
import com.example.student_carpooling.tripRecyclerView.TripViewHolders;
import com.example.student_carpooling.usersRecyclerView.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;


public class PastTripFragment extends Fragment  {

    private int size=0;

    private RecyclerView tripRecyclerView;
    private TripAdapter tripAdapter;
    //private RecyclerView.Adapter tripAdapter;
    private RecyclerView.LayoutManager tripLayoutManager;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String ProfilePicUrl,UserID, Date, Destination, Seats, Starting, LuggageCheck, Time, UserName;
    private FirebaseAuth mAuth;
    private DatabaseReference UserDb, reference;
    Date tripdate, expiredTripdate;
    LinearLayout linearLayout;
    NavigationView navigationView;

    float DstLon, DstLat;

    String Deleted,Completed,Started;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_past_trip, container, false);
        tripRecyclerView = v.findViewById(R.id.tripPastRecycler);
        tripRecyclerView.setNestedScrollingEnabled(false); //not true?
        tripRecyclerView.setHasFixedSize(true);
        resultsTrips.clear();
        tripAdapter = new TripAdapter(getDataTrips(),getActivity());
        tripLayoutManager = new LinearLayoutManager(getActivity());
        tripRecyclerView.setLayoutManager(tripLayoutManager);
        tripRecyclerView.setAdapter(tripAdapter);


        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();
        getTripIds();


        linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout);

        return v;

    }

    private void getTripIds(){
        DatabaseReference TripIDs = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID);
        TripIDs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //if there is any info there
                    for(DataSnapshot id : dataSnapshot.getChildren()){
                        //then get the info under that unique ID
                        //what does .getkey do?
                        String key = id.getKey();
                        UserTripDB(key);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UserTripDB(final String ID) {
        //push().getKey();
        DatabaseReference TripsDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(ID);
        TripsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();


                    //check that none of them are null
                    if(map.get("Date")!=null){
                        Date = map.get("Date").toString();

                    }
                    if(map.get("Time")!=null){
                        Time = map.get("Time").toString();}

                    if(map.get("Seats")!=null){
                        Seats = map.get("Seats").toString();}

                    if(map.get("Luggage")!=null){
                        LuggageCheck = map.get("Luggage").toString().toUpperCase();
                    }

                    if(map.get("Starting")!=null){
                        Starting = map.get("Starting").toString().toUpperCase();
                    }

                    if(map.get("Destination")!=null){
                        Destination = map.get("Destination").toString().toUpperCase();}

                    if(map.get("Username")!=null){
                        UserName = map.get("Username").toString();}


                    if(map.get("DstLon")!=null){
                        DstLon = Float.parseFloat(map.get("DstLon").toString());}

                    if(map.get("DstLat")!=null){
                        DstLat = Float.parseFloat(map.get("DstLat").toString());}


                    if(map.get("Deleted")!=null){
                        Deleted = map.get("Deleted").toString();
                    }
                    if(map.get("Completed")!=null){
                        Completed = map.get("Completed").toString();
                    }
                    if(map.get("Started")!=null){
                        Started = map.get("Started").toString();
                    }

                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                    try {
                     String dateStr = Date + " " + Time;
                     Date TripDate = format.parse(dateStr);
                     long mili = TripDate.getTime();
                     tripdate = new Date(mili);


                   } catch (ParseException e) {
                    e.printStackTrace();
                  }



                Date rightNow = Calendar.getInstance().getTime();

                    // current date is before trip date..
                    if((rightNow.after(tripdate))){
                        // this means its a past date...

                        if(Integer.parseInt(Deleted)!= 1) {
                            Trip object = new Trip(DstLat, DstLon, UserName, ID, UserID, Date, Time, Seats, LuggageCheck, Starting, Destination);
                            //sort the trips based on their date in milisec


                            //the trip is expired after 5 hours after if not started


                            resultsTrips.add(object);
                            resultsTrips.sort(new Comparator<Trip>() {
                                @Override
                                public int compare(Trip o1, Trip o2) {
                                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                                    String date1 = o1.getDate() + " " + o1.getTime();
                                    String date2 = o2.getDate() + " " + o2.getTime();
                                    Date Date1 = null;
                                    Date Date2 = null;
                                    try {
                                        Date1 = format.parse(date1);
                                        Date2 = format.parse(date2);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long mili = Date1.getTime();
                                    long mili2 = Date2.getTime();
                                    Date datenew1 = new Date(mili);
                                    Date datenew2 = new Date(mili2);


                                    return datenew2.compareTo(datenew1);

                                }
                            });
                            tripAdapter.notifyDataSetChanged();
                        }
                   }





                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private ArrayList resultsTrips = new ArrayList<Trip>();

    private ArrayList<Trip> getDataTrips() {
        return resultsTrips;

    }


    //This is required to reset the Recycler View, otherwise each time the trips available will duplicate
    @Override
    public void onResume() {
        super.onResume();
        //resultsTrips.clear();

    }


}
