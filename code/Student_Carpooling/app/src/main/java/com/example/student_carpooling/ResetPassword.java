package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText Email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ImageView back = findViewById(R.id.back);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
        getSupportActionBar().setTitle("");}
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Email = findViewById(R.id.resetEmail);
        Button ResetPassword = findViewById(R.id.reset_password);
        mAuth = FirebaseAuth.getInstance();


        Button Cancel = findViewById(R.id.cancel);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPassword.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                 try{
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPassword.this, "Email has been sent.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResetPassword.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ResetPassword.this, "Email is not registered successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }catch (Exception e){
                     Toast.makeText(getApplicationContext(), "Please Enter an email", Toast.LENGTH_SHORT).show();
                 }



            }
        });


    }}