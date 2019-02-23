package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    //dont think needed

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //else nothing is done.

        // need a way of keeping the user signed in after closing app.
        Button SignIn;
        Button Registration;


        SignIn = findViewById(R.id.Login);
        Registration = findViewById(R.id.Register);

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLogin();
            }
        });

        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegister();
            }
        });

    }

    private void moveToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);

    }

    private void moveToRegister() {
        Intent intent = new Intent(MainActivity.this, Register.class);
        startActivity(intent);
    }
}
