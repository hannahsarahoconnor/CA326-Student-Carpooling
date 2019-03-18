package com.example.student_carpooling;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class FindTrips extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ImageView navProfile;
    private TextView NUsername, Nemail;
    private DatabaseReference UserDb;
    private String ProfilePicUrl;
    private TextView DateInput;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private int year;
    private int month;
    private Date date,rightNow;
    int dayOfMonth;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String StartingPt="", DestinationPt="";
    private String DBUsername;
    private EditText DriverName;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "FindTrips";

    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trips);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.usernameNav);
        Nemail = hView.findViewById(R.id.emailNav);
        navProfile = hView.findViewById(R.id.ImageView);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String UserID = firebaseUser.getUid();
            UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
            getUserDB();
        }


        DriverName = findViewById(R.id.DriverName);

        setupFirebaseListener();

        String apiKey = getResources().getString(R.string.google_maps_places_key);
        Places.initialize(getApplicationContext(), apiKey);



        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentSTART);
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
            autocompleteFragment.setCountry("IE");
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    StartingPt = place.getName();
                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(FindTrips.this,"error",Toast.LENGTH_SHORT).show();
                }
            });

            autocompleteFragment.setHint("Starting Point?");
        }



        AutocompleteSupportFragment autocompleteFragmentDST = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentDEST);

        if (autocompleteFragmentDST != null) {
            autocompleteFragmentDST.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
            autocompleteFragmentDST.setCountry("IE");

            autocompleteFragmentDST.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    DestinationPt = place.getName();
                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(FindTrips.this,"error",Toast.LENGTH_SHORT).show();
                }
            });

            autocompleteFragmentDST.setHint("Destination?");
        }

// Set up a PlaceSelectionListener to handle the response.

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


        Button filter = findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startingDate;
                String driverName = DriverName.getText().toString();
                startingDate = DateInput.getText().toString();
                //covert startingDate to mili secs

                if (!TextUtils.isEmpty(startingDate)) {

                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                    try {
                        Date Datee = format.parse(startingDate + " 00:00");
                        long mili = Datee.getTime();
                        date = new Date(mili);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    rightNow = Calendar.getInstance().getTime();
                    int radioId = radioGroup.getCheckedRadioButtonId();
                    radioButton = findViewById(radioId);
                    String luggage = radioButton.getText().toString();
                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(date);
                    cal2.setTime(rightNow);


                    boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

                    if (rightNow.before(date) || sameDay) {
                        Intent intent = new Intent(FindTrips.this, FilteredTrips.class);
                        intent.putExtra("Starting", StartingPt);
                        intent.putExtra("Destination", DestinationPt);
                        intent.putExtra("Date", startingDate);
                        intent.putExtra("Luggage", luggage);
                        intent.putExtra("Driver","");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(FindTrips.this, "Please don't select a past date", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!TextUtils.isEmpty(driverName)) {
                        Intent intent = new Intent(FindTrips.this, FilteredTrips.class);
                        intent.putExtra("Driver", driverName);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(FindTrips.this, "Enter a date or search by driver instead", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });


    }

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
        getMenuInflater().inflate(R.menu.passenger, menu);
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

            case R.id.contact:
                ContactAdmins();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void DeleteAccount() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(FindTrips.this).create();
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
                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //is deleted
                            Toast.makeText(FindTrips.this, "Account Successfully deleted", Toast.LENGTH_LONG).show();
                            UserDb.removeValue();
                            Intent intent = new Intent(FindTrips.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            dialogBuilder.dismiss();
                        } else {
                            Toast.makeText(FindTrips.this, "Account couldn't be deleted at this time", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void ContactAdmins(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(FindTrips.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
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
                Intent intent1 = new Intent(FindTrips.this,ChatActivity.class);
                intent1.putExtra("Username","StudentCarpooling");
                intent1.putExtra("ID", "tFRougwMUphm8B95q7EAToUoYci1");
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void getUserDB(){
        UserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //makes sure the data is present, else the app will crash if not
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0) {
                    //data originally added is kept in this format
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator );

                    if (map != null) {
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
            }
        }
    }
}
