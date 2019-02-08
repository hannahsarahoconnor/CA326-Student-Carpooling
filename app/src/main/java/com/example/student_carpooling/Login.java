package com.example.student_carpooling;



import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {

    private EditText Email, Password;
    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        Button LoginButton, ResetButton;

        ResetButton = findViewById(R.id.Reset);
        LoginButton = findViewById(R.id.Login);
        Email = findViewById(R.id.EmailInput);
        Password = findViewById(R.id.PasswordInput);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Email.getText().toString();
                final String password = Password.getText().toString();
                //put it in as if statement.


                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkEmailVerfication();
                            //not successful
                        } else {
                            Toast.makeText(Login.this, "Wrong Email or Password, Try again", Toast.LENGTH_SHORT).show();
                            Email.setText("");
                            Password.setText("");
                        }


                    }

                });
            }
        });


        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ResetPassword.class);
                startActivity(intent);
                finish();
            }
        });

    }


   // @Override
  //  protected void onStart() {
    //    super.onStart();
      //  mAuth.addAuthStateListener(firebaseAuthListener);
   // }

   // @Override
    //protected void onStop() {
       // super.onStop();
        //mAuth.removeAuthStateListener(firebaseAuthListener);
   // }

    private void checkEmailVerfication(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Boolean emailCheck = firebaseUser.isEmailVerified();
        if(emailCheck){
            finish();
            startActivity(new Intent(Login.this,UserInstance.class));
        }else{
            Toast.makeText(Login.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }

    }
}




