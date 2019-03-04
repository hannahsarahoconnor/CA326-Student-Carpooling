package com.example.student_carpooling;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;



import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;


import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;


import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import java.util.HashMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class DriverCreate extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView NUsername, Nemail;
    private String UserID,ProfilePicUrl;
    private FirebaseAuth mAuth;
    private DatabaseReference UserDb, ref;
    NavigationView navigationView;
    private ImageView navProfile;
    private TextView DateInput, Time, Destination;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    int hour;
    int minutes;
    int year;
    int month;
    int dayOfMonth;
    private String startingDate, startingTime;
    private RadioGroup radioGroup;;
    private RadioButton radioButton;
    private EditText TripNote;
    private String DBUsername, numberSeats,oldDate, oldTime;
    private String starting ,destination;
    private boolean result = false;
    FirebaseUser CurrentUser;

    private LatLng latLng;



    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "DriverCreate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        getUserDB();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.UsernameNav);
        Nemail = hView.findViewById(R.id.EmailNav);
        navProfile = hView.findViewById(R.id.imageView);

        setupFirebaseListener();


        // Initialize Places.

        String apiKey = "AIzaSyBTJ-pUGMT8ypVgiyWqy0T_nSoT6z0bOIA";
        Places.initialize(getApplicationContext(), apiKey);

        //Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        AutocompleteSupportFragment autocompleteFragmentDST = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentDST);



        autocompleteFragmentDST.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setCountry("IE");
        autocompleteFragmentDST.setCountry("IE");

        autocompleteFragmentDST.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //LatLng string_location = place.getLatLng();
                String address = (String) place.getAddress();
                destination = (String) place.getName();
                geoLocate();

            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(DriverCreate.this,"error",Toast.LENGTH_SHORT).show();
            }
        });

        autocompleteFragmentDST.setHint("Destination?");

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                //LatLng string_location = place.getLatLng();
                //String address = (String) place.getAddress();
                starting = (String) place.getName();
                //Toast.makeText(DriverCreate.this,""+starting,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                // Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(DriverCreate.this,"error",Toast.LENGTH_SHORT).show();
            }
        });

        autocompleteFragment.setHint("Starting Point?");


        //onActivityResult, you must call super.onActivityResult, otherwise the fragment will not function properly.

        Time = findViewById(R.id.TimeInput);
        Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show the current time by defaultPic rather than 12:00
                calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minutes = calendar.get(Calendar.MINUTE);


                timePickerDialog = new TimePickerDialog(DriverCreate.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //how to show if time is am or pm, rather than 24 hours
                        // FORMAT THIS TO BE IN FORM HH:MM
                        String format = (String.format(Locale.US,"%02d:%02d", hourOfDay, minute));
                        Time.setText(format);
                    }

                },hour,minutes,false);
                timePickerDialog.show();
            }
        });

        //Getting time input

        DateInput = findViewById(R.id.DateInput);
        DateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(DriverCreate.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (month+1) + "/" + year;
                        DateInput.setText(date);
                    }
                },year,month,dayOfMonth);
                datePickerDialog.show();
            }
        });



        //no_seats
        Spinner spinner = (Spinner) findViewById(R.id.seats_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.no_seats, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                numberSeats = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Getting the User Input
        radioGroup = findViewById(R.id.LuggageInput);
        TripNote = findViewById(R.id.Note);

        Button Create;
        Create = findViewById(R.id.CreateId);





        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final String StartingPoint = StartingPt.getText().toString();
                //final String DstPoint = DestinationPt.getText().toString();
                final String startingDate = DateInput.getText().toString();
                final String startingTime = Time.getText().toString();
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                final String luggageCheck = radioButton.getText().toString();
                final String Tripnote = TripNote.getText().toString();

                if(TextUtils.isEmpty(starting) || TextUtils.isEmpty(destination) || TextUtils.isEmpty(startingDate) || TextUtils.isEmpty(startingTime) || TextUtils.isEmpty(Tripnote)) {
                    Toast.makeText(DriverCreate.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    Date_TimeCheck(startingDate,startingTime);
                    if(result){

                        ref = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID);
                        Map TripInfo = new HashMap();
                        //add driver username and maybe name to the form too
                        TripInfo.put("Username", DBUsername);
                        TripInfo.put("Starting", starting);
                        TripInfo.put("Destination",destination);
                        TripInfo.put("DstLat", latLng.latitude);
                        TripInfo.put("DstLon", latLng.longitude);
                        TripInfo.put("Date", startingDate);
                        TripInfo.put("Seats", numberSeats);
                        TripInfo.put("Time", startingTime);
                        TripInfo.put("Luggage", luggageCheck);
                        TripInfo.put("Note", Tripnote);
                        TripInfo.put("Started", 0);
                        TripInfo.put("Completed", 0);
                        TripInfo.put("Cancelled",0);
                        TripInfo.put("Deleted",0);

                        ref.push().setValue(TripInfo);
                        Toast.makeText(DriverCreate.this, "new trip has been added", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(DriverCreate.this, DriverTrips.class));
                        finish();



                    }

                    }


            }

        });


    }




    private void Date_TimeCheck(final String Date,final String Time){
        DatabaseReference TripIDCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID);
        TripIDCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        //get the id for trip
                        final String key = id.getKey();
                        DatabaseReference TripDetailsCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(key);
                        TripDetailsCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                    if (map.get("Date") != null) {
                                        oldDate = map.get("Date").toString();
                                    }
                                    if (map.get("Time") != null) {
                                        oldTime = map.get("Time").toString();
                                    }

                                    if(oldDate.equals(Date) && oldTime.equals(Time)){
                                        Toast.makeText(DriverCreate.this, "This conflicts with another trip, please change the time/date or delete other trip", Toast.LENGTH_LONG).show();
                                        result =false;
                                    }
                                    else {

                                        result = true;

                                    }




                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void geoLocate(){
    Geocoder geocoder = new Geocoder(DriverCreate.this);
        Toast.makeText(this, ""+destination, Toast.LENGTH_SHORT).show();

    List<Address> list = new ArrayList<>();
        try {
        list = geocoder.getFromLocationName(destination, 1);

    } catch (
    IOException e) {
        Log.e(TAG, "IOException:" + e.getMessage());
    }
        if (list.size() > 0) {
        Address address = list.get(0);
        //Toast.makeText(RequestMapActivity.this, "" + address.toString(), Toast.LENGTH_LONG).show();
        //moveCamera(new LatLng(address.getLatitude(),address.getLongitude(),address.getAddressLine(0));
        // Toast.makeText(RequestMapActivity.this, "" + address.getAddressLine(0), Toast.LENGTH_LONG).show();

        latLng = new LatLng(address.getLatitude(), address.getLongitude());
            //Toast.makeText(this, ""+address.getLatitude(), Toast.LENGTH_SHORT).show();

        }}

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {

            case R.id.action_settings:
                AlertDialog.Builder dialog = new AlertDialog.Builder(DriverCreate.this);
                dialog.setTitle("Are you sure you want to delete your account?");
                dialog.setMessage("By Doing this.....");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //is deleted
                                    Toast.makeText(DriverCreate.this,"Account Successfully deleted",Toast.LENGTH_LONG).show();
                                    UserDb.removeValue();
                                    Intent intent = new Intent(DriverCreate.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(DriverCreate.this,"Account couldn't be deleted at this time",Toast.LENGTH_LONG).show();
                                }
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
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_message:
                Intent msg = new Intent(DriverCreate.this, DriverMessage.class);
                startActivity(msg);
                break;

            case R.id.nav_profile:
                Intent profile = new Intent(DriverCreate.this, DriverProfile.class);
                startActivity(profile);
                break;

            case R.id.nav_sign_out:
                FirebaseAuth.getInstance().signOut();

            case R.id.nav_create_trips:
                Intent create = new Intent(DriverCreate.this, DriverCreate.class);
                startActivity(create);
                break;

            case R.id.nav_my_trips:
                Intent trips = new Intent(DriverCreate.this, DriverTrips.class);
                startActivity(trips);
                break;

            case R.id.nav_find_trips_requests:
                Intent requests = new Intent(DriverCreate.this, DriverFindRequests.class);
                startActivity(requests);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void getUserDB(){
        UserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //makes sure the data is present, else the app will crash if not
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0){
                    //data originally added is kept in this format
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map.get("Username")!=null){
                        DBUsername = map.get("Username").toString();
                        NUsername.setText(DBUsername);
                    }
                    if(map.get("profileImageUrl")!=null){
                        ProfilePicUrl = map.get("profileImageUrl").toString();
                        if(!ProfilePicUrl.equals("defaultPic")) {
                            Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);};
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //not needed
            }
        });
    }

    private void setupFirebaseListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    Nemail.setText(email);
                } else {
                    Toast.makeText(DriverCreate.this, "Sign Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DriverCreate.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}

