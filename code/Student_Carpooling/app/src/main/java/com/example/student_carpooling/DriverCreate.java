package com.example.student_carpooling;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;



import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DriverCreate extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView NUsername, NEmail;
    private String UserID,ProfilePicUrl;
    private DatabaseReference UserDb, ref;
    private ImageView navProfile;
    private TextView DateInput, TimeInput;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private int hour;
    private int minutes;
    private int year;
    private int month;
    private int dayOfMonth;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText TripNote;
    private String DBUsername, numberSeats,oldDate, oldTime;
    private String starting ,destination;
    private FirebaseUser CurrentUser;
    private Date trip,oldTripDate;
    private LatLng latLng;


    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "DriverCreate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_create);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.UsernameNav);
        NEmail = hView.findViewById(R.id.EmailNav);
        navProfile = hView.findViewById(R.id.imageView);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser!=null){
            UserID = mAuth.getCurrentUser().getUid();
            UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
            getUserDB();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //add listener to check when user signs out
        addListener();

        // Initialize auto-place dialog.
        String apiKey = getResources().getString(R.string.google_maps_places_key);
        Places.initialize(getApplicationContext(), apiKey);
        // Initialize the AutocompleteSupportFragment.

        //Origin Search Dialog
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setCountry("IE");


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                starting = place.getName();
            }

            @Override
            public void onError(@NonNull Status status) {
                // Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(DriverCreate.this,"error",Toast.LENGTH_SHORT).show();
            }
        });

        autocompleteFragment.setHint("Starting Point?");


        //Destination Search Dialog

        AutocompleteSupportFragment autocompleteFragmentDST = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentDST);

        assert autocompleteFragmentDST != null;
        autocompleteFragmentDST.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragmentDST.setCountry("IE");
        autocompleteFragmentDST.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destination = place.getName();
                geoLocate();

            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(DriverCreate.this,"error",Toast.LENGTH_SHORT).show();
            }
        });
        autocompleteFragmentDST.setHint("Destination?");


        //Time Dialog

        TimeInput = findViewById(R.id.TimeInput);
        TimeInput.setOnClickListener(new View.OnClickListener() {
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
                        TimeInput.setText(format);
                    }

                },hour,minutes,false);
                timePickerDialog.show();
            }
        });

        //Date Dialog

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


        //Drop down (spinner) for number of seats
        Spinner spinner = findViewById(R.id.seats_spinner);
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


        //Getting the user input for note and luggage button check
        radioGroup = findViewById(R.id.LuggageInput);
        TripNote = findViewById(R.id.Note);


        // when user clicks create -- convert to string
        Button Create;
        Create = findViewById(R.id.CreateId);
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get time right now to compare that it's not a past date
                Date rightNow = Calendar.getInstance().getTime();

                final String startingDate = (String) DateInput.getText();
                final String startingTime = (String) TimeInput.getText();
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                final String luggageCheck = radioButton.getText().toString();
                final String Trip_note = TripNote.getText().toString();

                //check that all fields are entered
                if(TextUtils.isEmpty(starting) || TextUtils.isEmpty(destination) || TextUtils.isEmpty(startingDate) || TextUtils.isEmpty(startingTime) || TextUtils.isEmpty(Trip_note)) {
                    Toast.makeText(DriverCreate.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }else{


                //convert the inputted trip date to the same format as the 'rightnow' date object so we can compare them
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                try {
                    String dateStr = startingDate + " " + startingTime;
                    Date Date = format.parse(dateStr);
                    long mil = Date.getTime();
                    trip = new Date(mil);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(rightNow.after(trip)){
                    Toast.makeText(DriverCreate.this, "You cannot choose a past date", Toast.LENGTH_SHORT).show();
                }

                else{

                       //First check that the driver doesnt have a trip created at the same date and time..

                        DatabaseReference TripIDCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID);
                        TripIDCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                                        //get the id for trip
                                        final String key = id.getKey();
                                        if(key != null){
                                        DatabaseReference TripDetailsCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID).child(key);
                                        TripDetailsCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                                                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                                                    if(map != null) {
                                                        if (map.get("Date") != null) {
                                                            oldDate = (String) map.get("Date");
                                                        }
                                                        if (map.get("Time") != null) {
                                                            oldTime = (String) map.get("Time");
                                                        }

                                                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                                                        try {
                                                            String dateStr2 = oldDate + " " + oldTime;
                                                            Date OldTripDate = format.parse(dateStr2);
                                                            long seconds2 = OldTripDate.getTime();
                                                            oldTripDate = new Date(seconds2);
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }

                                                        if (oldTripDate.equals(trip)) {
                                                            Toast.makeText(DriverCreate.this, "This conflicts with another trip, please change the time/date or delete other trip", Toast.LENGTH_LONG).show();
                                                            //refresh the activity if its a conflicting time
                                                            //add fields will be reset
                                                            Intent intent = getIntent();
                                                            finish();
                                                            startActivity(intent);

                                                        }
                                                    }
                                                }}


                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }
                            }}

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                            //Create new trip and add to database
                            ref = FirebaseDatabase.getInstance().getReference().child("TripForms").child(UserID);
                            Map<String, Object> TripInfo = new HashMap<>();
                            TripInfo.put("Username", DBUsername);
                            TripInfo.put("Starting", starting);
                            TripInfo.put("Destination",destination);
                            TripInfo.put("DstLat", latLng.latitude);
                            TripInfo.put("DstLon", latLng.longitude);
                            TripInfo.put("Date", startingDate);
                            TripInfo.put("Seats", numberSeats);
                            TripInfo.put("Time", startingTime);
                            TripInfo.put("Luggage", luggageCheck);
                            TripInfo.put("Note", Trip_note);
                            TripInfo.put("Started", 0);
                            TripInfo.put("Completed", 0);
                            TripInfo.put("Cancelled",0);
                            TripInfo.put("Deleted",0);

                            ref.push().setValue(TripInfo);
                            Toast.makeText(DriverCreate.this, "new trip has been added successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DriverCreate.this, DriverTrips.class));
                            finish();
                        }
                    }


            }

        });

    }

    private void geoLocate(){
        //convert the destination name into geographical coordinates
        Geocoder geocoder = new Geocoder(DriverCreate.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(destination, 1);

        } catch (IOException e) {
            Toast.makeText(this, "IOException:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
        }}


    //Navigation Drawer Functions

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
                DeleteAccount();
                break;


            case R.id.help:
                Intent intent = new Intent(DriverCreate.this,DriverHelp.class);
                startActivity(intent);
                break;

            case R.id.contact:
                ContactAdmins();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    // Delete the Users' Account

    private void DeleteAccount() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(DriverCreate.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        TextView Text = dialogView.findViewById(R.id.Text);
        TextView Title = dialogView.findViewById(R.id.Title);
        Title.setText("Delete Account");
        Text.setText("By deleting your account, you will no longer be able to sign in and all of your user data will be deleted. If you wish to you use the app again in the future, you must re-register. Are you sure you wish to continue? ");
        Button Submit = dialogView.findViewById(R.id.Submit);


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //auth library func -- email and password removed
                CurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //is deleted
                            Toast.makeText(DriverCreate.this, "Account Successfully deleted", Toast.LENGTH_LONG).show();
                            //Remove corresponding user info stored in the database
                            DatabaseReference User = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
                            User.removeValue();

                            //sign out user
                            Intent intent = new Intent(DriverCreate.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            dialogBuilder.dismiss();
                        } else {
                            Toast.makeText(DriverCreate.this, "Account couldn't be deleted at this time", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void ContactAdmins(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(DriverCreate.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        //adjust the dialog text and image view from warning dialog to admin
        TextView Text = dialogView.findViewById(R.id.Text);
        ImageView warn = dialogView.findViewById(R.id.warn);
        ImageView admin = dialogView.findViewById(R.id.admin);
        TextView Title = dialogView.findViewById(R.id.Title);
        warn.setVisibility(View.GONE);
        admin.setVisibility(View.VISIBLE);
        Title.setText("Contact Admins");
        Text.setText("If you have any further issues or queries regarding this app, please click yes to start a private chat with the admins");
        Button Submit = dialogView.findViewById(R.id.Submit);


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DriverCreate.this,ChatActivity.class);
                String AdminID = getResources().getString(R.string.AdminID);
                intent1.putExtra("Username","StudentCarpooling");
                intent1.putExtra("ID", AdminID);
                intent1.putExtra("Fullname","Admins");
                intent1.putExtra("ProfilePicURL","defaultPic");
                startActivity(intent1);
                finish();
                dialogBuilder.dismiss();

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    private void getUserDB(){
        // Use this info to set the user info in the navigation drawer header
        UserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //makes sure the data is present, else the app will crash if not
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0){
                    //data originally added is kept in this format
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                    if(map!= null) {
                        if (map.get("Username") != null) {
                            DBUsername = (String) map.get("Username");
                            NUsername.setText(DBUsername);
                        }
                        if (map.get("profileImageUrl") != null) {
                            ProfilePicUrl = (String) map.get("profileImageUrl");
                            if (ProfilePicUrl != null && !ProfilePicUrl.equals("defaultPic")) {
                                Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //not needed
            }
        });
    }

    private void addListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    NEmail.setText(email);
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
        //add listener when activity is created
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        //remove listener when activity is stopped
        super.onStop();
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }

    }

    //Places API

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //onActivityResult, you must call super.onActivityResult, otherwise the fragment will not function properly
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            }
    }
    }
}
