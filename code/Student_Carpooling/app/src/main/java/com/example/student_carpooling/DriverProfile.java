package com.example.student_carpooling;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DriverProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView Name,Username,Uni;

    private FirebaseAuth mAuth;
    private ImageView profilePic;
    private DatabaseReference UserDb;
    private String DBName, DBSurname, DBUsername, DBUni,UserID;
    private TextView NUsername, Nemail, ratingText,Ratings,Completed;

    private RatingBar ratingBar;

    private Uri ResultUri;
    private String ProfilePicUrl;
    private Button Confirm,Switch;
    private ImageView navProfile;
    FirebaseUser CurrentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Ratings = findViewById(R.id.RatingNo);
        Completed = findViewById(R.id.CompletedNo);
        ratingBar = findViewById(R.id.rating);
        Name = findViewById(R.id.Name);
        Username = findViewById(R.id.Username);
        Uni = findViewById(R.id.College);
        Confirm = findViewById(R.id.ConfirmPic);
        profilePic = findViewById(R.id.ProfilePic);
        Switch = findViewById(R.id.SwitchMode);


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
        View hView =  navigationView.getHeaderView(0);
        NUsername = hView.findViewById(R.id.UsernameNav);
        Nemail = hView.findViewById(R.id.EmailNav);
        navProfile = hView.findViewById(R.id.imageView);


        setupFirebaseListener();


        profilePic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(Intent.ACTION_PICK);
          intent.setType("image/*");
          startActivityForResult(intent,1);
            }
            });

        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserDB();
            }
        });

        Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDb.child("Type").setValue("Passenger");
                startActivity(new Intent(DriverProfile.this,PassengerActivity.class));
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(DriverProfile.this);
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
                                    Toast.makeText(DriverProfile.this,"Account Successfully deleted",Toast.LENGTH_LONG).show();
                                    UserDb.removeValue();
                                    Intent intent = new Intent(DriverProfile.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(DriverProfile.this,"Account couldn't be deleted at this time",Toast.LENGTH_LONG).show();
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


            case R.id.help:
                //go to new activity
                //tFRougwMUphm8B95q7EAToUoYci1
                Intent intent = new Intent(DriverProfile.this,DriverHelp.class);
                startActivity(intent);
                break;

            case R.id.contact:
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(DriverProfile.this);
                dialog1.setTitle("Contact Admins");
                dialog1.setMessage("If you have any further issues or queries regarding this app, please click yes to start a private chat with the admins");
                dialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(DriverProfile.this,ChatActivity.class);
                        intent1.putExtra("Username","StudentCarpooling");
                        intent1.putExtra("ID", "tFRougwMUphm8B95q7EAToUoYci1");
                        intent1.putExtra("Fullname","Admins");
                        intent1.putExtra("ProfilePicURL","defaultPic");
                        startActivity(intent1);
                    }
                });

                dialog1.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog1 = dialog1.create();
                alertDialog1.show();
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
                Intent msg = new Intent(DriverProfile.this, DriverMessage.class);
                startActivity(msg);
                break;

            case R.id.nav_profile:
                Intent profile = new Intent(DriverProfile.this, DriverProfile.class);
                startActivity(profile);
                break;

            case R.id.nav_sign_out:
                //user shouldnt get notification if logged out
                OneSignal.setSubscription(false);
                FirebaseAuth.getInstance().signOut();

            case R.id.nav_create_trips:
                Intent create = new Intent(DriverProfile.this, DriverCreate.class);
                startActivity(create);
                break;

            case R.id.nav_my_trips:
                Intent trips = new Intent(DriverProfile.this, DriverTrips.class);
                startActivity(trips);
                break;

            case R.id.nav_find_trips_requests:
                Intent requests = new Intent(DriverProfile.this, DriverFindRequests.class);
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
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0) {
                    //data originally added is kept in this format
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Name") != null) {
                        DBName = map.get("Name").toString();


                    }
                    if (map.get("Surname") != null) {
                        DBSurname = map.get("Surname").toString();
                        Name.setText(DBName + " "+ DBSurname);

                    }
                    if (map.get("University") != null) {
                        DBUni = map.get("University").toString();
                        Uni.setText(DBUni);
                    }
                    if (map.get("CompletedTrips") != null) {
                        String completed = map.get("CompletedTrips").toString();
                        Completed.setText(completed);
                        //TripCount.setText(completed + " completed carpools");
                    }
                    if (map.get("Username") != null) {
                        DBUsername = map.get("Username").toString();
                        Username.setText("@" + DBUsername);
                        NUsername.setText(DBUsername);
                    }
                    if (map.get("profileImageUrl") != null) {
                        ProfilePicUrl = map.get("profileImageUrl").toString();
                        if (!ProfilePicUrl.equals("defaultPic")) {
                            Glide.with(getApplication()).load(ProfilePicUrl).into(profilePic);
                            Glide.with(getApplication()).load(ProfilePicUrl).into(navProfile);
                        }
                    }
                    int ratingSum = 0;
                    float ratingTotal = 0;
                    float ratingAvg =0;
                    for(DataSnapshot rates : dataSnapshot.child("Ratings").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(rates.getValue().toString());
                        ratingTotal++;
                    }

                    //calculate average and set bar
                    if(ratingTotal != 0){
                        ratingAvg  = ratingSum/ratingTotal;
                        ratingBar.setRating(ratingAvg);
                        ratingBar.setIsIndicator(true);
                        Ratings.setText(""+Math.round(ratingTotal));
                    }

                }


                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //not needed
            }
        });
    }

    private void saveUserDB(){
        //possibily allow the option to update details about themselves too
        if (ResultUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Image").child(UserID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), ResultUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream Outputstream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, Outputstream);
            byte[] data = Outputstream.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            UserDb.updateChildren(newImage);
                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });

        };}

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
                //user has picked an image

                //its reference is kept within the Uri variable
                final Uri imageUri = data.getData();
                profilePic.setImageURI(imageUri);

                ResultUri = imageUri;
                profilePic.setImageURI(ResultUri);
                Confirm.setVisibility(View.VISIBLE);
            }
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
                    Toast.makeText(DriverProfile.this, "Sign Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DriverProfile.this, MainActivity.class);
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
