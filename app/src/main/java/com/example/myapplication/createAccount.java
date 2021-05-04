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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class createAccount extends AppCompatActivity {
    public Button btn;
    public EditText emailAddress;
    public EditText password;
    public EditText confirmPassword;
    public FirebaseAuth authorize;
    public Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        authorize = FirebaseAuth.getInstance();
        btn = findViewById(R.id.button5);
        emailAddress = findViewById(R.id.editTextTextPersonName3);
        password = findViewById(R.id.editTextTextPassword2);
        confirmPassword = findViewById(R.id.editTextTextPassword3);
        myToolbar = findViewById(R.id.myToolbar);
        myToolbar.setTitle("Create An Account");
        setSupportActionBar(myToolbar);
        btn.setOnClickListener(v -> accountCreation());

    }


    private void accountCreation() {
        String email = emailAddress.getText().toString();
        String pass = password.getText().toString();
        String confirmPass = confirmPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter an email address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPass)){
            Toast.makeText(this, "Confirm password", Toast.LENGTH_SHORT).show();
        }
        else if(!pass.equals(confirmPass)){
            Toast.makeText(this, "Password fields do not match", Toast.LENGTH_SHORT).show();
        }
        else{

            authorize.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            startActivity(new Intent(createAccount.this, register.class));
                        }
                        else{
                            String errorMess = task.getException().getMessage();
                            Toast.makeText(createAccount.this, "An error has occurred: " + errorMess ,Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}