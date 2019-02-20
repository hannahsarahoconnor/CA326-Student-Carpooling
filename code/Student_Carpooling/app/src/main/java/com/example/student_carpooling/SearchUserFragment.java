package com.example.student_carpooling;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.student_carpooling.usersRecyclerView.User;
import com.example.student_carpooling.usersRecyclerView.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


//Try add a search bar to search based on username...


public class SearchUserFragment extends Fragment {

    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager userLayoutManager;

    private LinearLayout linearLayout;

    private FirebaseAuth mAuth;

    private EditText searchBar;

    private String UserID, UserName, ProfilePicUrl, CurrentUserName;

    private SearchView searchView = null;

    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_search_user, container, false);

        userRecyclerView = v.findViewById(R.id.searchUserRecycler);
        userRecyclerView.setNestedScrollingEnabled(false); //not true?
        userRecyclerView.setHasFixedSize(true);
        userAdapter = new UserAdapter(getDataUsers(), getActivity());
        userLayoutManager = new LinearLayoutManager(getActivity());
        userRecyclerView.setLayoutManager(userLayoutManager);

        userRecyclerView.setAdapter(userAdapter);



        linearLayout = (LinearLayout) v.findViewById(R.id.linearLayoutSearch);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();
        getCurrentUserInfo();




        //set up a User click listener to launch message with them when clicked..



        getUsersId();

        return v;
    }


    //overwrite the activity to show the search bar instead..

    private void getCurrentUserInfo() {
        DatabaseReference CurrentUser = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        CurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();

                    //get username and profile pic url

                    if(map.get("Username")!=null){
                        CurrentUserName = map.get("Username").toString();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUsersId(){
        DatabaseReference UserIDs = FirebaseDatabase.getInstance().getReference().child("users");
        UserIDs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //if there is any info there
                    for(DataSnapshot id : dataSnapshot.getChildren()){
                        //get the id for each user
                        String key = id.getKey();
                        UserDB(key);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UserDB(String ID) {
        //push().getKey();
        DatabaseReference UsersDB = FirebaseDatabase.getInstance().getReference().child("users").child(ID);
        UsersDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();

                    //get username and profile pic url

                    if(map.get("profileImageUrl")!=null){
                        ProfilePicUrl = map.get("profileImageUrl").toString();
                    }

                    //add usertype too and display

                    if(map.get("Username")!=null){
                        UserName = map.get("Username").toString();}


                    //make sure that the current user isnt shown..

                   if(!(UserName.equals(CurrentUserName))){

                        // this means its a past date...

                      User user = new User(ProfilePicUrl,UserName);

                      resultsUsers.add(user);
                      userAdapter.notifyDataSetChanged();}



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private ArrayList resultsUsers = new ArrayList<User>();

    private ArrayList<User> getDataUsers() {

        return resultsUsers;

    }

    //when clicks back on tab, the users will be retrieved, stops duplication

    @Override
    public void onResume() {
        super.onResume();
        resultsUsers.clear();

    }

}
