package com.example.student_carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student_carpooling.tripRecyclerView.Trip;
import com.example.student_carpooling.tripRecyclerView.TripAdapter;
import com.example.student_carpooling.tripRecyclerView.TripViewHolders;
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
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;


public class presentTripsFragment extends Fragment  {

    private RecyclerView tripRecyclerView;
    private TripAdapter PresentTripAdapter;
    private RecyclerView.LayoutManager tripLayoutManager;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView NUsername, Nemail;
    private String ProfilePicUrl,UserID, Date, Destination, Seats, Starting, LuggageCheck, Time,UserName,TripID;
    private FirebaseAuth mAuth;
    private DatabaseReference UserDb, reference;
    Date tripdate;
    LinearLayout linearLayout;
    private int counter=0;
    TextView textView1, textView2;
    Button createRequest;

    float DstLon, DstLat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_present_trips, container, false);
        tripRecyclerView = v.findViewById(R.id.trippresentRecycler);
        tripRecyclerView.setNestedScrollingEnabled(false); //not true?
        tripRecyclerView.setHasFixedSize(true);
        resultsTrips.clear();
        PresentTripAdapter = new TripAdapter(getDataTrips(),getActivity());
        tripLayoutManager = new LinearLayoutManager(getActivity());
        tripRecyclerView.setLayoutManager(tripLayoutManager);

        tripRecyclerView.setAdapter(PresentTripAdapter);


        //toolbar


        linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout);
        textView1 = v.findViewById(R.id.Text);
        textView2 = v.findViewById(R.id.Text2);
        createRequest = v.findViewById(R.id.Request);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();



        getTripIds();

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


                    //do the same with profile image

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

                    if(sameDay && tripdate.after(rightNow)){
                        TripID = ID;

                        //

                        // this means its a past date...
                        Trip object = new Trip(DstLat,DstLon,UserName, TripID, UserID,Date, Time, Seats, LuggageCheck, Starting, Destination);
                        resultsTrips.add(object);
                        PresentTripAdapter.notifyDataSetChanged();
                        counter++;

                        if(PresentTripAdapter.getItemCount() > 0){
                           //disable the recycler view
                           //add dynamic text and button


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

    @Override
    public void onResume() {
        super.onResume();
        //resultsTrips.clear();

    }
}
