package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    private EditText Email,Password,Name,Surname,Username,Age,University;
    //private String profilepic;
    private FirebaseAuth mAuth;
    private AutoCompleteTextView Domain;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppCompatCheckBox checkBox;
        //stores login status

        //can save other user information to database from getting the user ID that is assigned from creation using Auth

        mAuth = FirebaseAuth.getInstance();
        AppCompatCheckBox checkbox;
        Button Registration;
        Registration = findViewById(R.id.Register);
        radioGroup= findViewById(R.id.GenderInput);
        checkbox = findViewById(R.id.checkbox);
        Email = findViewById(R.id.EmailInput);
        Age = findViewById(R.id.AgeInput);
        University = findViewById(R.id.UniInput);
        Name = findViewById(R.id.NameInput);
        Username = findViewById(R.id.UsernameInput);
        Surname = findViewById(R.id.SurnameInput);
        Password = findViewById(R.id.PasswordInput);
        Domain = findViewById(R.id.Domain);
        String[] collegeDomains = getResources().getStringArray(R.array.CollegeDomains);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, collegeDomains);
        Domain.setAdapter(adapter);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else{
                    Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }

            }});



        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] CollegeDomains = getResources().getStringArray(R.array.CollegeDomains);
                final String first = Email.getText().toString();
                final String second = Domain.getText().toString();

                final String email = first + "@" + second;
                final String password = Password.getText().toString();
                final String username = Username.getText().toString();
                final String name = Name.getText().toString();
                final String surname = Surname.getText().toString();
                final String age = Age.getText().toString();
                final String university = University.getText().toString();
                final int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                final RadioButton male = findViewById(R.id.Male);
                final RadioButton female = findViewById(R.id.Female);

                final String gender = radioButton.getText().toString();
                Query usernameCheck = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("Username").equalTo(username);
                usernameCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            Toast.makeText(Register.this, "Username is already taken.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //add checks to make sure blanks arent empty

                            if(TextUtils.isEmpty(first) || TextUtils.isEmpty(second) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username) || TextUtils.isEmpty(age) || TextUtils.isEmpty(university)|| TextUtils.isEmpty(surname) || TextUtils.isEmpty(name)) {
                                Toast.makeText(Register.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                            }

                            //add to database
                            if (password.length() < 6 || TextUtils.isEmpty(password)) {
                                Toast.makeText(Register.this, "Password must be 6 or more characters", Toast.LENGTH_SHORT).show();
                            }

                            if (first.contains("@")) {
                                Toast.makeText(Register.this, "Email Error", Toast.LENGTH_SHORT).show();
                            }

                            if (!Arrays.asList(CollegeDomains).contains(second)) {
                                Toast.makeText(Register.this, "Choose a valid Email Domain", Toast.LENGTH_SHORT).show();
                            } else {
                                //creates a specific user account
                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            //not successful
                                            Toast.makeText(Register.this, "Registration Error", Toast.LENGTH_SHORT).show();
                                        } else {
                                            String userId = mAuth.getCurrentUser().getUid();
                                            DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                                            Map UserInfo = new HashMap();


                                            UserInfo.put("Username", username);
                                            UserInfo.put("Name", name);
                                            UserInfo.put("Surname", surname);
                                            UserInfo.put("University", university);
                                            UserInfo.put("Age", age);
                                            UserInfo.put("Gender", gender);
                                            UserInfo.put("Type", true);
                                            UserInfo.put("profileImageUrl", "defaultPic");

                                            currentUser.setValue(UserInfo);
                                            sendVerificationEmail();
                                        }
                                    }
                                });
                            }
                        }
                    }

                    //no use

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            });}


            private void sendVerificationEmail(){
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser!=null){
                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this, "Registration Successful, please now verify your email", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(Register.this, Login.class));
                                finish();
                            //else - no email was sent.
                            }
                        }
                    });
                }
            }
}




