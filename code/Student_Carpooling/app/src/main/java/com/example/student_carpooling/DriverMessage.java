package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.student_carpooling.findTripsRecyclerView.FindTripAdapter;
import com.example.student_carpooling.messagesRecyclerView.Message;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DriverMessage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView Name, Username, Uni;

    private FirebaseAuth mAuth;
    private ImageView profilePic;
    private DatabaseReference reference, UserDb;
    private String DBName, DBUsername, DBUni, UserID, ProfilePicUrl, First, Surname, Fullname;
    private TextView NUsername, Nemail;

    private LinearLayoutManager userLayoutManager;

    private ImageView navProfile;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    private String UserName, profilePicurl;

    private EditText searchUsers;

    FirebaseUser CurrentUser;


    ArrayList<String> chatters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        UserID = mAuth.getCurrentUser().getUid();
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        getUserDB();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View hView = navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.UsernameNav);
        Nemail = hView.findViewById(R.id.EmailNav);
        navProfile = hView.findViewById(R.id.imageView);


        chatters = new ArrayList<>();

        setupFirebaseListener();

        recyclerView = findViewById(R.id.activechatsrv);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        userAdapter = new UserAdapter(getDataUsers(), DriverMessage.this);
        userLayoutManager = new LinearLayoutManager(DriverMessage.this);
        recyclerView.setAdapter(userAdapter);



        searchUsers = findViewById(R.id.searchBar);

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


        }}
                private void closeKeyboard(){
                    View view = this.getCurrentFocus();
                    if(view != null){
                        InputMethodManager inm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        inm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }
                }

                private void showKeyboard(){
                    View view = this.getCurrentFocus();
                    if (view.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                    }

                }
                private void searchUser(String s){
                    closeKeyboard();
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("Search").startAt(s).endAt(s +"\uf8ff");

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //previous list is cleared - works
                            resultsUsers.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                // if (dataSnapshot.getChildrenCount() > 0){
                                final String id = snapshot.getKey();
                                // only show it if it's a reciever or sender to the currrent user.// how get this info -> function check, pass the id to a func
                                if((!id.equals(CurrentUser.getUid()) && (chatters.contains(id)))){
                                    resultsUsers.clear();
                                    DatabaseReference GetUserDB = FirebaseDatabase.getInstance().getReference().child("users").child(id);
                                    GetUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                //get the info and create a new user object
                                                //required -> Id, profilepicurl, username
                                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                                if (map.get("profileImageUrl") != null) {
                                                    profilePicurl = map.get("profileImageUrl").toString();

                                                }
                                                if (map.get("Username") != null) {
                                                    UserName = map.get("Username").toString();

                                                }

                                                if (map.get("Surname") != null) {
                                                    Surname = map.get("Surname").toString();

                                                }

                                                if (map.get("Name") != null) {
                                                    First = map.get("Name").toString();

                                                }

                                                Fullname = First + " " + Surname;

                                                User object = new User(id, profilePicurl, UserName, Fullname);
                                                resultsUsers.add(object);
                                                userAdapter.notifyDataSetChanged();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });





                                }}}

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
                                    DatabaseReference RecieverDB = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                                    RecieverDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                //get the info and create a new user object
                                                //required -> Id, profilepicurl, username
                                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                                if (map.get("profileImageUrl") != null) {
                                                    profilePicurl = map.get("profileImageUrl").toString();

                                                }
                                                if (map.get("Username") != null) {
                                                    UserName = map.get("Username").toString();

                                                }

                                                if (map.get("Surname") != null) {
                                                    Surname = map.get("Surname").toString();

                                                }

                                                if (map.get("Name") != null) {
                                                    First = map.get("Name").toString();

                                                }

                                                Fullname = First + " " + Surname;

                                                User object = new User(key, profilePicurl, UserName, Fullname);
                                                //resultsUsers.add(object);
                                                chatters.add(key);
                                                userAdapter.notifyDataSetChanged();

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
                            for (DataSnapshot id : dataSnapshot.getChildren()) { ;
                                //get the id for each user
                                final String key = id.getKey();
                                DatabaseReference SenderDB = FirebaseDatabase.getInstance().getReference().child("ChatList").child(key);
                                SenderDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot id : dataSnapshot.getChildren()) {
                                                final String recieverKey = id.getKey();
                                                if (recieverKey.equals(UserID)) {
                                                    //add that user to database
                                                    //get their other info first
                                                    DatabaseReference RecieverDB = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                                                    RecieverDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                //get the info and create a new user object
                                                                //required -> Id, profilepicurl, username
                                                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                                                if (map.get("profileImageUrl") != null) {
                                                                    profilePicurl = map.get("profileImageUrl").toString();

                                                                }
                                                                if (map.get("Username") != null) {
                                                                    UserName = map.get("Username").toString();

                                                                }

                                                                if (map.get("Surname") != null) {
                                                                    Surname = map.get("Surname").toString();

                                                                }

                                                                if (map.get("Name") != null) {
                                                                    First = map.get("Name").toString();

                                                                }

                                                                Fullname = First + " " + Surname;

                                                                User object = new User(key, profilePicurl, UserName, Fullname);
                                                                resultsUsers.add(object);
                                                                chatters.add(key);
                                                                userAdapter.notifyDataSetChanged();

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                } }
                                        }
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                private ArrayList resultsUsers = new ArrayList<User>();

                private List<User> getDataUsers() {
                    return resultsUsers;
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
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMessage.this);
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
                                                Toast.makeText(DriverMessage.this,"Account Successfully deleted",Toast.LENGTH_LONG).show();
                                                UserDb.removeValue();
                                                Intent intent = new Intent(DriverMessage.this,MainActivity.class);
                                                startActivity(intent);
                                            }
                                            else{
                                                Toast.makeText(DriverMessage.this,"Account couldn't be deleted at this time",Toast.LENGTH_LONG).show();
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
                            Intent msg = new Intent(DriverMessage.this, DriverMessage.class);
                            startActivity(msg);
                            break;

                        case R.id.nav_profile:
                            Intent profile = new Intent(DriverMessage.this, DriverProfile.class);
                            startActivity(profile);
                            break;

                        case R.id.nav_sign_out:
                            FirebaseAuth.getInstance().signOut();

                        case R.id.nav_create_trips:
                            Intent create = new Intent(DriverMessage.this, DriverCreate.class);
                            startActivity(create);
                            break;

                        case R.id.nav_my_trips:
                            Intent trips = new Intent(DriverMessage.this, DriverTrips.class);
                            startActivity(trips);
                            break;

                        case R.id.nav_find_trips_requests:
                            Intent requests = new Intent(DriverMessage.this, DriverFindRequests.class);
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
                                if(map.get("Name")!=null){
                                    DBName = map.get("Name").toString();
                                    // Name.setText(DBName);

                                }
                                if(map.get("Username")!=null){
                                    DBUsername = map.get("Username").toString();
                                    NUsername.setText(DBUsername);
                                }
                                if(map.get("profileImageUrl")!=null){
                                    ProfilePicUrl = map.get("profileImageUrl").toString();
                                    if(!ProfilePicUrl.equals("defaultPic")) {
                                        Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);}
                                }}


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
                                Toast.makeText(DriverMessage.this, "Sign Out", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DriverMessage.this, MainActivity.class);
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

            }

