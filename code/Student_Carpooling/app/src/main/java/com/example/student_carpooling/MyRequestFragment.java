package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.student_carpooling.tripRecyclerView.Trip;
import com.example.student_carpooling.tripRequestsRecyclerView.RequestTrip;
import com.example.student_carpooling.tripRequestsRecyclerView.RequestTripAdapter;
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

public class MyRequestFragment extends Fragment {

    private RecyclerView tripRecyclerView;
    private RequestTripAdapter tripAdapter;
    private RecyclerView.LayoutManager tripLayoutManager;

    private FirebaseAuth mAuth;
    private String UserID,Starting,Date,Destination,Note,Username, Fullname, First, Surname,Time,LuggageCheck;

    private Date tripdate;
    public MyRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_request, container, false);

        tripRecyclerView = v.findViewById(R.id.requestsRecycler);
        tripRecyclerView.setNestedScrollingEnabled(false); //not true?
        tripRecyclerView.setHasFixedSize(true);
       //resultsTrips.clear();
        tripAdapter = new RequestTripAdapter(getDataTrips(),getActivity());
        tripLayoutManager = new LinearLayoutManager(getActivity());
        tripRecyclerView.setLayoutManager(tripLayoutManager);
        tripRecyclerView.setAdapter(tripAdapter);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();

        getTripRequestsIDs();


        return v;
    }


    private void getTripRequestsIDs(){
        DatabaseReference TripIDs = FirebaseDatabase.getInstance().getReference().child("TripRequests").child(UserID);
        TripIDs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //if there is any info there
                    for(DataSnapshot id : dataSnapshot.getChildren()){
                        //then get the info under that unique ID
                        //what does .getkey do?
                        String key = id.getKey();
                        RequestTrips(key);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RequestTrips(final String ID){
        DatabaseReference TripsDB = FirebaseDatabase.getInstance().getReference().child("TripRequests").child(UserID).child(ID);
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

                    if(map.get("Note")!=null){
                        Note = map.get("Note").toString();}

                    if(map.get("Luggage")!=null){
                        LuggageCheck = map.get("Luggage").toString().toUpperCase();
                    }

                    if(map.get("Starting")!=null){
                        Starting = map.get("Starting").toString().toUpperCase();
                    }

                    if(map.get("Fullname")!=null){
                        Fullname = map.get("Fullname").toString().toUpperCase();
                    }

                    if(map.get("Destination")!=null){
                        Destination = map.get("Destination").toString().toUpperCase();}

                    if(map.get("Username")!=null){
                        Username = map.get("Username").toString();}


                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                    try {
                        String dateStr = Date + " " + Time;
                        Date TripDate = format.parse(dateStr);
                        long mili = TripDate.getTime();
                        tripdate = new Date(mili);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                     RequestTrip object = new RequestTrip(ID,"Passenger",Note, Username, UserID,"defaultPic", Date, Time,Fullname, LuggageCheck, Starting, Destination);
                            //sort the trips based on their date in milisec


                            //the trip is expired after 5 hours after if not started
                       resultsTrips.add(object);
                       resultsTrips.sort(new Comparator<RequestTrip>() {
                                @Override
                                public int compare(RequestTrip o1, RequestTrip o2) {
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


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showDialog() {
    }


    private ArrayList resultsTrips = new ArrayList<RequestTrip>();

    private ArrayList<RequestTrip> getDataTrips() {
        return resultsTrips;

    }


    //This is required to reset the Recycler View, otherwise each time the trips available will duplicate
    @Override
    public void onResume() {
        super.onResume();
        //resultsTrips.clear();

    }


}
