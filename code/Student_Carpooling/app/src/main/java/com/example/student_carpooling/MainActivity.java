package com.example.student_carpooling;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    //dont think needed


    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth2;
    private String UserID, UserType;

    private Dialog LoginDialog;
    private LinearLayout main2;
    private LinearLayout main;
    private ProgressBar bar;
    private Boolean emailCheck;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            main2.setVisibility(View.VISIBLE);
            main.setVisibility(View.GONE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main2 = findViewById(R.id.main2);
        main =  findViewById(R.id.main);

        handler.postDelayed(runnable, 2500);

        mAuth2 = FirebaseAuth.getInstance();


        checkIfLoggedIn();

        Button SignIn;
        Button Registration;
        Button ResetPassword;

        bar = findViewById(R.id.indeterminateBar);
        LoginDialog = new Dialog(this);
        SignIn = findViewById(R.id.Login);
        Registration = findViewById(R.id.Register);
        ResetPassword = findViewById(R.id.Reset);

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginPopUp();
                //LoginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });

        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegister();
            }
        });

        ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResetPassword.class);
                startActivity(intent);
            }
        });

    }

    public void LoginPopUp() {
        final EditText Email, Password;
        TextView close;
        Button Login;
        final FirebaseAuth mAuth;

        mAuth = FirebaseAuth.getInstance();
        LoginDialog.setContentView(R.layout.login_popup);
        close = LoginDialog.findViewById(R.id.close);
        Login = LoginDialog.findViewById(R.id.Login);
        //radioGroup = LoginDialog.findViewById(R.id.TypeInput);
        Email = LoginDialog.findViewById(R.id.EmailInput);
        Password = LoginDialog.findViewById(R.id.PasswordInput);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup = LoginDialog.findViewById(R.id.TypeInput);
                int radioId = radioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = LoginDialog.findViewById(radioId);
                final String email = Email.getText().toString().trim();
                final String password = Password.getText().toString().trim();
                final String type = radioButton.getText().toString();


                try {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                LoginDialog.dismiss();
                                checkEmailVerfication(mAuth, type);
                            } else {
                                Toast.makeText(MainActivity.this, "Wrong Email or Password, Try again", Toast.LENGTH_SHORT).show();
                                Email.setText("");
                                Password.setText("");
                                bar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }catch(Exception e){
                    Toast.makeText(MainActivity.this, "Please enter both your email and password", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //put it in as if statement.
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginDialog.dismiss();
                //finish();
            }
        });

        LoginDialog.show();
    }


    private void moveToRegister() {
        Intent intent = new Intent(MainActivity.this, Register.class);
        startActivity(intent);
    }


    private void checkEmailVerfication(FirebaseAuth mAuth,String type) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            emailCheck = firebaseUser.isEmailVerified();
        }
        DatabaseReference UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        if (emailCheck) {
            finish();
            bar.setVisibility(View.VISIBLE);
            if(type.equals("Driver")){
               UserDb.child("Type").setValue("Driver");
               startActivity(new Intent(MainActivity.this,DriverMain.class));
            }
           else{
                UserDb.child("Type").setValue("Passenger");
                startActivity(new Intent(MainActivity.this, PassengerMain.class));
           }

        } else {
            Toast.makeText(MainActivity.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }

    }

    public void checkIfLoggedIn(){
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    UserID = user.getUid();
                    DatabaseReference UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
                    //check to see what user type they previously were
                    UserDb.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                //data originally added is kept in this format
                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                };
                                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                                if (map != null) {
                                    if (map.get("Type") != null) {
                                        UserType = (String) map.get("Type");
                                    }
                                }


                                if (UserType != null) {
                                    if (UserType.equals("Driver")) {
                                        Intent intent = new Intent(MainActivity.this, DriverMain.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (UserType.equals("Passenger")) {
                                        Intent intent = new Intent(MainActivity.this, PassengerMain.class);
                                        startActivity(intent);
                                        finish();
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


        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth2.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAuth2.addAuthStateListener(mAuthListener);
    }




    @Override
    protected void onStop() {
        super.onStop();
        mAuth2.removeAuthStateListener(mAuthListener);
    }
}

