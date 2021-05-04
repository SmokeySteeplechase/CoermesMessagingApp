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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public Button btn;
    public Button btn2;
    public EditText emailAddress;
    public EditText password;
    public FirebaseAuth authorize;
    public Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authorize = FirebaseAuth.getInstance();
        btn = findViewById(R.id.button);
        btn2 = findViewById(R.id.button3);
        emailAddress = findViewById(R.id.editTextTextPersonName);
        password = findViewById(R.id.editTextTextPassword);
        myToolbar = findViewById(R.id.myToolbar);
        myToolbar.setTitle("Coermes: Login");
        setSupportActionBar(myToolbar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btn2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, createAccount.class)));

    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = authorize.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(MainActivity.this, homeScreen.class));
        }
    }
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void login() {
        String email = emailAddress.getText().toString();
        String pass = password.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        }
        else{
            authorize.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            startActivity(new Intent(MainActivity.this, homeScreen.class));
                        }
                        else{
                          String errorMess = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, "Error: "+ errorMess, Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }


}