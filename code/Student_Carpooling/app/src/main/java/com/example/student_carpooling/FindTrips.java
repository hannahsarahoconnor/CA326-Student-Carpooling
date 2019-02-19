package com.example.student_carpooling;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.database.ValueEventListener;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class FindTrips extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;

    private ImageView navProfile;
    private TextView NUsername, Nemail;
    private String email,UserID;
    private DatabaseReference UserDb;
    private String ProfilePicUrl;

    private TextView DateInput, Time;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    int hour;
    int minutes;
    int year;
    int month;
    int dayOfMonth;
    private RadioGroup radioGroup;;
    private RadioButton radioButton;
    private String StartingPt, DestinationPt;
    private Button filter;
    private String DBUsername;

    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "FindTrips";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        getUserDB();

        View hView =  navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.usernameNav);
        Nemail = hView.findViewById(R.id.emailNav);
        navProfile = hView.findViewById(R.id.ImageView);


        setupFirebaseListener();

        String apiKey = "AIzaSyBTJ-pUGMT8ypVgiyWqy0T_nSoT6z0bOIA";
        Places.initialize(getApplicationContext(), apiKey);

        //Create a new Places client instance.
        if (!Places.isInitialized()) {
        PlacesClient placesClient = Places.createClient(this);}

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentSTART);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setCountry("IE");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //LatLng string_location = place.getLatLng();
                //String address = (String) place.getAddress();
                StartingPt = (String) place.getName();
                Toast.makeText(FindTrips.this,""+StartingPt,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                // Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(FindTrips.this,"error",Toast.LENGTH_SHORT).show();
            }
        });

        autocompleteFragment.setHint("Starting Point?");
       // autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));


        AutocompleteSupportFragment autocompleteFragmentDST = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentDEST);

        autocompleteFragmentDST.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        //autocompleteFragmentDST.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragmentDST.setCountry("IE");

        autocompleteFragmentDST.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng string_location = place.getLatLng();
                //("destinationLat", destinationLatLng.latitude);
                //("destinationLng", destinationLatLng.longitude);
                //CharSequence address = place.getAddress();
                final CharSequence address = place.getAddress();
                DestinationPt = (String) place.getName();
                Toast.makeText(FindTrips.this,""+ address,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(FindTrips.this,"error",Toast.LENGTH_SHORT).show();
            }
        });

        autocompleteFragmentDST.setHint("Destination?");

// Set up a PlaceSelectionListener to handle the response.


        Time = findViewById(R.id.TimeInput);
        Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show the current time by default rather than 12:00
                calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minutes = calendar.get(Calendar.MINUTE);


                timePickerDialog = new TimePickerDialog(FindTrips.this, new TimePickerDialog.OnTimeSetListener() {
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
                datePickerDialog = new DatePickerDialog(FindTrips.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (month+1) + "/" + year;
                        DateInput.setText(date);
                    }
                },year,month,dayOfMonth);
                datePickerDialog.show();
            }
        });


        radioGroup = findViewById(R.id.LuggageInput);


        filter = findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // final String StartingPoint = StartingPt.getText().toString();
               // final String DstPoint = DestinationPt.getText().toString();
                final String startingDate = DateInput.getText().toString();
                final String startingTime = Time.getText().toString();
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                final String luggage = radioButton.getText().toString();


                //pass this info to the next activity
                Intent intent = new Intent(FindTrips.this, FilteredTrips.class);
                //intent.putExtra("Starting", StartingPoint);
               //intent.putExtra("Destination", DstPoint);
                intent.putExtra("Date", startingDate);
                intent.putExtra("Time", startingTime);
                intent.putExtra("Luggage", luggage);
                startActivity(intent);
                finish();
            }
        });


    }

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
        getMenuInflater().inflate(R.menu.passenger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.pass_message:
                Intent msg = new Intent(FindTrips.this, PassengerMessage.class);
                startActivity(msg);
                break;

            case R.id.pass_profile:
                Intent profile = new Intent(FindTrips.this, PassengerProfile.class);
                startActivity(profile );
                break;

            case R.id.pass_sign_out:
                FirebaseAuth.getInstance().signOut();

            case R.id.pass_find_trips:
                Intent create = new Intent(FindTrips.this,FindTrips.class);
                startActivity(create);
                break;

            case R.id.pass_trips:
                Intent trips = new Intent(FindTrips.this,PassengerTrips.class);
                startActivity(trips);
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
                        Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //not needed
            }
        });
    }

    private void setupFirebaseListener(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    String email = user.getEmail();
                    Nemail.setText(email);
                }
                else{
                    Toast.makeText(FindTrips.this, "Sign Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FindTrips.this, MainActivity.class);
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
        if(mAuthStateListener != null){
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
