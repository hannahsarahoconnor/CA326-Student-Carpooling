package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.usersRecyclerView.User;
import com.example.student_carpooling.usersRecyclerView.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PassengerMessage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ImageView navProfile;
    private TextView NUsername, Nemail;
    private String UserID;
    private DatabaseReference UserDb;
    private String ProfilePicUrl;
    private String First, Surname, Fullname, profilePicurl, UserName;

    FirebaseUser CurrentUser;

    private RecyclerView.Adapter userAdapter;
    ArrayList<String> chatters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser != null) {
            UserID = mAuth.getCurrentUser().getUid();
            UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
            getUserDB();
        }

        View hView =  navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.usernameNav);
        Nemail = hView.findViewById(R.id.emailNav);
        navProfile = hView.findViewById(R.id.ImageView);

        setupFirebaseListener();

        chatters = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.activechatsrv);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        userAdapter = new UserAdapter(getDataUsers(), PassengerMessage.this);
        recyclerView.setAdapter(userAdapter);


        EditText searchUsers = findViewById(R.id.searchBar);

        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showKeyboard();
                resultsUsers.clear();
                searchUser(s.toString().toLowerCase());
                //closeKeyboard();

            }

            @Override
            public void afterTextChanged(Editable s) {
                //hide the keyboard to yield results
            }
        });

        if(searchUsers.getText().toString().equals("")) {
            //only get if no text in search bar has been entered
            resultsUsers.clear();
            getRecievers();
            getSenders();


        }
    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager inm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inm != null) {
                inm.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        }
    }

    private void showKeyboard() {
        View view = this.getCurrentFocus();
        try {
            if (view != null && view.requestFocus()) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (NullPointerException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void searchUser(String s){
        closeKeyboard();
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("Search").startAt(s).endAt(s +"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //previous list is cleared - works
                resultsUsers.clear();
                //this isnt working
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // if (dataSnapshot.getChildrenCount() > 0){
                    final String id = snapshot.getKey();
                    // only show it if it's a reciever or sender to the currrent user.// how get this info -> function check, pass the id to a func
                    if (id != null && (!id.equals(UserID) && (chatters.contains(id)))) {
                        resultsUsers.clear();
                        DatabaseReference GetUserDB = FirebaseDatabase.getInstance().getReference().child("users").child(id);
                        GetUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    //get the info and create a new user object
                                    //required -> Id, profilepicurl, username
                                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                    };
                                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                                    if (map != null) {
                                        if (map.get("profileImageUrl") != null) {
                                            profilePicurl = (String) map.get("profileImageUrl");

                                        }
                                        if (map.get("Username") != null) {
                                            UserName = (String) map.get("Username");

                                        }

                                        if (map.get("Surname") != null) {
                                            Surname = (String) map.get("Surname");

                                        }

                                        if (map.get("Name") != null) {
                                            First = (String) map.get("Name");

                                        }

                                        Fullname = First + " " + Surname;

                                        User object = new User(id, profilePicurl, UserName, Fullname);
                                        resultsUsers.add(object);
                                        userAdapter.notifyDataSetChanged();

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getRecievers() {
        DatabaseReference Chats = FirebaseDatabase.getInstance().getReference().child("ChatList").child(UserID);
        Chats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resultsUsers.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        //get the id for each user
                        final String key = id.getKey();
                        if (key != null) {
                            DatabaseReference RecieverDB = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                            RecieverDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        //get the info and create a new user object
                                        //required -> Id, profilepicurl, username
                                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                        };
                                        Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                                        if (map != null) {

                                            if (map.get("profileImageUrl") != null) {
                                                profilePicurl = (String) map.get("profileImageUrl");

                                            }
                                            if (map.get("Username") != null) {
                                                UserName = (String) map.get("Username");

                                            }

                                            if (map.get("Surname") != null) {
                                                Surname = (String) map.get("Surname");

                                            }

                                            if (map.get("Name") != null) {
                                                First = (String) map.get("Name");

                                            }

                                            Fullname = First + " " + Surname;
                                            chatters.add(key);
                                            userAdapter.notifyDataSetChanged();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            //add to the User List..

                            //query the user db to get more information


                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void getSenders() {
        //be in the form ChatList -> otheruserid, currentuser id..

        //first get all children
        //within, check to see if any of their children is equal to the current
        //if so, add that user to the list too
        DatabaseReference Chats = FirebaseDatabase.getInstance().getReference().child("ChatList");
        Chats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    //get the id for each user
                    final String key = id.getKey();
                    if(key != null) {
                        DatabaseReference SenderDB = FirebaseDatabase.getInstance().getReference().child("ChatList").child(key);
                        SenderDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                                        final String recieverKey = id.getKey();
                                        if (recieverKey != null && recieverKey.equals(UserID)) {
                                            //add that user to database
                                            //get their other info first
                                            DatabaseReference RecieverDB = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                                            RecieverDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        //get the info and create a new user object
                                                        //required -> Id, profilepicurl, username
                                                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                                        };
                                                        Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                                                        if (map != null) {
                                                            if (map.get("profileImageUrl") != null) {
                                                                profilePicurl = (String) map.get("profileImageUrl");

                                                            }
                                                            if (map.get("Username") != null) {
                                                                UserName = (String) map.get("Username");

                                                            }

                                                            if (map.get("Surname") != null) {
                                                                Surname = (String) map.get("Surname");

                                                            }

                                                            if (map.get("Name") != null) {
                                                                First = (String) map.get("Name");

                                                            }

                                                            Fullname = First + " " + Surname;

                                                            User object = new User(key, profilePicurl, UserName, Fullname);
                                                            resultsUsers.add(object);
                                                            chatters.add(key);
                                                            userAdapter.notifyDataSetChanged();

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

    private ArrayList<User> resultsUsers = new ArrayList<>();

    private List<User> getDataUsers() {
        return resultsUsers;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =findViewById(R.id.drawer_layout);
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

            case R.id.help:
                Intent intent = new Intent(PassengerMessage.this,PassengerHelp.class);
                startActivity(intent);
                break;

            case R.id.contact:
                ContactAdmins();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    private void DeleteAccount() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(PassengerMessage.this).create();
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
                CurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //is deleted
                            Toast.makeText(PassengerMessage.this, "Account Successfully deleted", Toast.LENGTH_LONG).show();
                            UserDb.removeValue();
                            Intent intent = new Intent(PassengerMessage.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            dialogBuilder.dismiss();
                        } else {
                            Toast.makeText(PassengerMessage.this, "Account couldn't be deleted at this time", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void ContactAdmins(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(PassengerMessage.this).create();
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
                String AdminID = getResources().getString(R.string.AdminID);
                Intent intent1 = new Intent(PassengerMessage.this,ChatActivity.class);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.pass_message:
                Intent msg = new Intent(PassengerMessage.this, PassengerMessage.class);
                startActivity(msg);
                break;

            case R.id.pass_profile:
                Intent profile = new Intent(PassengerMessage.this, PassengerProfile.class);
                startActivity(profile );
                break;

            case R.id.pass_sign_out:
                FirebaseAuth.getInstance().signOut();

            case R.id.pass_find_trips:
                Intent create = new Intent(PassengerMessage.this,FindTrips.class);
                startActivity(create);
                break;

            case R.id.pass_trips:
                Intent trips = new Intent(PassengerMessage.this,PassengerTrips.class);
                startActivity(trips);
                break;
            case R.id.create_request:
                Intent requests = new Intent(PassengerMessage.this,PassengerCreateRequests.class);
                startActivity(requests);
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
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0){
                    //data originally added is kept in this format
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                    Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator );

                    if(map != null) {
                        if (map.get("Username") != null) {
                            String DBUsername = (String) map.get("Username");
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
                    Toast.makeText(PassengerMessage.this, "Sign Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PassengerMessage.this, MainActivity.class);
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
}
