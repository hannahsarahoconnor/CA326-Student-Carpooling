package com.example.student_carpooling;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student_carpooling.tripRequestsRecyclerView.RequestTrip;
import com.example.student_carpooling.tripRequestsRecyclerView.RequestTripAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MyRequestFragment extends Fragment {

    private RequestTripAdapter tripAdapter;
    private String UserID,Starting,Date,Destination,Note,Username, Fullname,Time,LuggageCheck;
    private long mili,mili2;

    public MyRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_request, container, false);

        RecyclerView tripRecyclerView = v.findViewById(R.id.requestsRecycler);
        tripRecyclerView.setNestedScrollingEnabled(false); //not true?
        tripRecyclerView.setHasFixedSize(true);
       //resultsTrips.clear();
        tripAdapter = new RequestTripAdapter(getDataTrips(),getActivity());
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tripRecyclerView.setAdapter(tripAdapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            UserID = firebaseUser.getUid();
        }

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
                if (dataSnapshot.exists()) {

                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if (map != null) {
                        //check that none of them are null
                        if (map.get("Date") != null) {
                            Date = (String) map.get("Date");

                        }
                        if (map.get("Time") != null) {
                            Time = (String) map.get("Time");
                        }

                        if (map.get("Note") != null) {
                            Note = (String) map.get("Note");
                        }

                        if (map.get("Luggage") != null) {
                            String luggagecheck = (String) map.get("Luggage");
                            if (luggagecheck != null) {
                                LuggageCheck = luggagecheck.toUpperCase();
                            }
                        }

                        if (map.get("Starting") != null) {
                            String starting = (String) map.get("Starting");
                            if (starting != null) {
                                Starting = starting.toUpperCase();
                            }
                        }

                        if (map.get("Fullname") != null) {
                            String fullname = (String) map.get("Fullname");
                            if (fullname != null) {
                                Fullname = fullname.toUpperCase();
                            }
                        }

                        if (map.get("Destination") != null) {
                            String destination = (String) map.get("Destination");
                            if (destination != null) {
                                Destination = destination.toUpperCase();
                            }

                        }

                        if (map.get("Username") != null) {
                            Username = (String) map.get("Username");
                        }

                        RequestTrip object = new RequestTrip(ID, "Passenger", Note, Username, UserID, "defaultPic", Date, Time, Fullname, LuggageCheck, Starting, Destination);
                        //sort the trips based on their date in milisec


                        //the trip is expired after 5 hours after if not started
                        resultsTrips.add(object);
                        resultsTrips.sort(new Comparator<RequestTrip>() {
                            @Override
                            public int compare(RequestTrip o1, RequestTrip o2) {
                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                                String date1 = o1.getDate() + " " + o1.getTime();
                                String date2 = o2.getDate() + " " + o2.getTime();
                                try {
                                    Date Date1 = format.parse(date1);
                                    Date Date2 = format.parse(date2);
                                    mili = Date1.getTime();
                                    mili2 = Date2.getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date datenew1 = new Date(mili);
                                Date datenew2 = new Date(mili2);


                                return datenew2.compareTo(datenew1);

                            }
                        });
                        tripAdapter.notifyDataSetChanged();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private ArrayList<RequestTrip> resultsTrips = new ArrayList<>();

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
