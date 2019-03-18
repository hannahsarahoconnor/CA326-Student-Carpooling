package com.example.student_carpooling;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student_carpooling.tripRecyclerView.Trip;
import com.example.student_carpooling.tripRecyclerView.TripAdapter;
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
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class FutureTripFragment extends Fragment  {
    private TripAdapter tripAdapter;
    private String UserID, Date, Destination, Seats, Starting, LuggageCheck, Time, UserName;
    private Date tripdate;
    private float DstLon, DstLat;
    private long mili,mili2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_future_trip, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!= null) {
            UserID = firebaseUser.getUid();
        }
        RecyclerView tripRecyclerView = v.findViewById(R.id.tripFutureRecycler);
        resultsTrips.clear();
        getTripIds();
        tripRecyclerView.setNestedScrollingEnabled(false); //not true?
        tripRecyclerView.setHasFixedSize(true);
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tripAdapter = new TripAdapter(getDataTrips(),getContext());
        tripRecyclerView.setAdapter(tripAdapter);

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
            TripsDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                        Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator );
                        if(map != null) {
                            //check that none of them are null
                            if (map.get("Date") != null) {
                                Date = (String) map.get("Date");
                            }
                            if (map.get("Time") != null) {
                                Time = (String) map.get("Time");
                            }

                            if (map.get("Seats") != null) {
                               Seats = Objects.requireNonNull(map.get("Seats")).toString();
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

                            if (map.get("Destination") != null) {
                                String destination = (String) map.get("Destination");
                                if (destination != null) {
                                    Destination = destination.toUpperCase();
                                }
                            }


                            if (map.get("Username") != null) {
                                UserName = (String) map.get("Username");
                            }


                            if (map.get("DstLon") != null) {
                                String lon = Objects.requireNonNull(map.get("DstLon")).toString();
                                DstLon = Float.parseFloat(lon);
                            }

                            if (map.get("DstLat") != null) {
                                String lat = Objects.requireNonNull(map.get("DstLat")).toString();
                                DstLat = Float.parseFloat(lat);
                            }


                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                            try {

                                String dateStr = Date + " " + Time;
                                Date Datee = format.parse(dateStr);
                                long mili = Datee.getTime();
                                tripdate = new Date(mili);
                                //Toast.makeText(FilteredTrips.this, "t:"+tripdate, Toast.LENGTH_SHORT).show();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            Date rightNow = Calendar.getInstance().getTime();

                            Calendar cal1 = Calendar.getInstance();
                            Calendar cal2 = Calendar.getInstance();
                            cal1.setTime(tripdate);
                            cal2.setTime(rightNow);
                            boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

                            if (tripdate.after(rightNow) && !sameDay) {
                                // this means its a past date...
                                Trip object = new Trip(DstLat, DstLon, UserName, ID, UserID, Date, Time, Seats, LuggageCheck, Starting, Destination);
                                resultsTrips.add(object);
                                resultsTrips.sort(new Comparator<Trip>() {
                                    @Override
                                    public int compare(Trip o1, Trip o2) {
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


                                        return datenew1.compareTo(datenew2);

                                    }
                                });
                                if (tripAdapter != null) {
                                    tripAdapter.notifyDataSetChanged();
                                }

                            }

                        }

                    }


                }



                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    private ArrayList<Trip> resultsTrips = new ArrayList<>();
        private ArrayList<Trip> getDataTrips() {

            return resultsTrips;
        }

    @Override
    public void onResume() {
        super.onResume();
    }
}
