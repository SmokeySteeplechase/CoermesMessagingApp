package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class register extends AppCompatActivity {
    public EditText username;
    public EditText school;
    public EditText fullName;
    public Button register;
    public FirebaseAuth authorize;
    public DatabaseReference refUser;
    String userID;
    public Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        authorize = FirebaseAuth.getInstance();
        userID = authorize.getCurrentUser().getUid();
        refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        username = findViewById(R.id.editTextTextPersonName6);
        school = findViewById(R.id.editTextTextPersonName7);
        fullName = findViewById(R.id.editTextTextPersonName8);
        register = findViewById(R.id.button7);
        myToolbar = findViewById(R.id.myToolbar);
        myToolbar.setTitle("Register Account");
        setSupportActionBar(myToolbar);
        register.setOnClickListener(v -> finishRegistration());
    }

    private void finishRegistration() {
        String user = username.getText().toString();
        String userSchool = school.getText().toString();
        String userFullName = fullName.getText().toString();
        if(TextUtils.isEmpty(user)){
            Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userSchool)){
            Toast.makeText(this, "Enter a school", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userFullName)){
            Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap mapUsers = new HashMap();
            mapUsers.put("username", user);
            mapUsers.put("school", userSchool);
            mapUsers.put("fullname", userFullName);
            refUser.updateChildren(mapUsers).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(register.this, "Account successfully created" ,Toast.LENGTH_LONG).show();
                    startActivity(new Intent(register.this, MainActivity.class));
                }
                else{
                    String errorMess = task.getException().getMessage();
                    Toast.makeText(register.this, "An error has occurred: " + errorMess ,Toast.LENGTH_LONG).show();
                }
            });

        }
    }
    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
    }
}