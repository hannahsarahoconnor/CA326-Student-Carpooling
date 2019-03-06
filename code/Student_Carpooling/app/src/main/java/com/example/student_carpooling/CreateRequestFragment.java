package com.example.student_carpooling;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


 public class CreateRequestFragment extends Fragment {

        private FragmentActivity myContext;
        FirebaseAuth mAuth;
        private DatabaseReference ref,UserDB;
        FirebaseUser CurrentUser;
        String UserID,destination,starting,fullname,first,surname,ProfilePicUrl,DBUsername;
        private TextView DateInput, Time;
        TimePickerDialog timePickerDialog;
        DatePickerDialog datePickerDialog;
        Calendar calendar;
        int hour;
        int minutes;
        int year;
        int month;
        Date trip;
        Button Submit;
        int dayOfMonth;
        private RadioGroup radioGroup;
        private RadioButton radioButton;
        private EditText TripNote;
        int AUTOCOMPLETE_REQUEST_CODE = 1;
        private static final String TAG = "CreateRequestFragment";
        private String apiKey;

        public CreateRequestFragment() {
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
            View v = inflater.inflate(R.layout.fragment_create_request, container, false);
            mAuth = FirebaseAuth.getInstance();
            CurrentUser = mAuth.getCurrentUser();
            UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            UserDB = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
            getUserDB();

            apiKey = getResources().getString(R.string.google_maps_places_key);
            Places.initialize(Objects.requireNonNull(getActivity()), apiKey);

            //Create a new Places client instance.
            PlacesClient placesClient = Places.createClient(getActivity());
            // Initialize the AutocompleteSupportFragment.

            //PlaceAutocompleteFragment autocompleteFragment1  = (PlaceAutocompleteFragment)getActivity().getFragmentManager().findFragmentById(R.id.autocomplete_fragment1);
            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

            AutocompleteSupportFragment autocompleteFragmentDST = (AutocompleteSupportFragment)
                    getChildFragmentManager().findFragmentById(R.id.autocomplete_fragmentDST);



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

                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(getContext(),"error",Toast.LENGTH_SHORT).show();
                }
            });

            autocompleteFragmentDST.setHint("Destination?");

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

                @Override
                public void onPlaceSelected(Place place) {
                    starting = (String) place.getName();
                }

                @Override
                public void onError(Status status) {

                    Toast.makeText(getContext(),"error",Toast.LENGTH_SHORT).show();
                }
            });

            autocompleteFragment.setHint("Starting Point?");


            //onActivityResult, you must call super.onActivityResult, otherwise the fragment will not function properly.

            Time = v.findViewById(R.id.TimeInput);
            Time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //show the current time by defaultPic rather than 12:00
                    calendar = Calendar.getInstance();
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minutes = calendar.get(Calendar.MINUTE);


                    timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
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

            DateInput = v.findViewById(R.id.DateInput);
            DateInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String date = dayOfMonth + "/" + (month+1) + "/" + year;
                            DateInput.setText(date);
                        }
                    },year,month,dayOfMonth);
                    datePickerDialog.show();
                }
            });



            //Getting the User Input
            radioGroup = v.findViewById(R.id.LuggageInput);
            TripNote = v.findViewById(R.id.Note);
            int radioId = radioGroup.getCheckedRadioButtonId();
            radioButton = v.findViewById(radioId);
            Submit = v.findViewById(R.id.submitt);

            Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String luggageCheck = "";
                    Date rightNow = Calendar.getInstance().getTime();
                    final String startingDate = DateInput.getText().toString();
                    final String startingTime = Time.getText().toString();
                    luggageCheck = radioButton.getText().toString();
                    final String Tripnote = TripNote.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                    try {

                        String dateStr = startingDate + " " + startingTime;
                        Date Datee = format.parse(dateStr);
                        long mili = Datee.getTime();
                        trip = new Date(mili);
                        //Toast.makeText(FilteredTrips.this, "t:"+tripdate, Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(rightNow.after(trip)){
                        Toast.makeText(getContext(), "past date", Toast.LENGTH_SHORT).show();
                    }

                    if(TextUtils.isEmpty(starting) || TextUtils.isEmpty(destination) || TextUtils.isEmpty(startingDate) || TextUtils.isEmpty(startingTime) || TextUtils.isEmpty(Tripnote)) {
                        Toast.makeText(getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        ref = FirebaseDatabase.getInstance().getReference().child("TripRequests").child(UserID);
                        Map RequestInfo = new HashMap();
                        //add driver username and maybe name to the form too
                        RequestInfo.put("Username", DBUsername);
                        RequestInfo.put("Fullname", fullname);
                        RequestInfo.put("ProfilePic",ProfilePicUrl);
                        RequestInfo.put("Starting", starting);
                        RequestInfo.put("Destination",destination);
                        RequestInfo.put("Date", startingDate);
                        RequestInfo.put("Time", startingTime);
                        RequestInfo.put("Luggage", luggageCheck);
                        RequestInfo.put("Note", Tripnote);

                        ref.push().setValue(RequestInfo);
                        Toast.makeText(getContext(), "new trip request has been listed", Toast.LENGTH_SHORT).show();
                        //start activity to new fragment?
                        startActivity(new Intent(getContext(), PassengerCreateRequests.class));

                    }

                }
            });







            return v;
        }

        private void getUserDB(){
            UserDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //makes sure the data is present, else the app will crash if not
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0){
                        //data originally added is kept in this format
                        Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                        if(map.get("Username")!=null){
                            DBUsername = map.get("Username").toString();
                        }
                        if(map.get("profileImageUrl")!=null){
                            ProfilePicUrl = map.get("profileImageUrl").toString();
                        }
                        if(map.get("Name")!=null){
                            first = map.get("Name").toString();
                        }
                        if(map.get("Surname")!=null){
                            surname = map.get("Surname").toString();
                        }
                        fullname = first + " " + surname;


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //not needed
                }
            });
        }





        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
