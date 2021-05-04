package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class contactPage extends AppCompatActivity {
    public TextView Username, FullName, School;
    public Button sendRequest, DeclineRequest;
    public DatabaseReference ContactReqRef, ProfileRef, ContactRef;
    public FirebaseAuth authorize;
    public String senderUID, receiveUID, state, saveCurDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_page);
        authorize = FirebaseAuth.getInstance();
        senderUID = authorize.getCurrentUser().getUid();
        receiveUID = getIntent().getExtras().get("USERID").toString();
        ProfileRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactReqRef = FirebaseDatabase.getInstance().getReference().child("ContactRequests");
        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        putInfo();
        ProfileRef.child(receiveUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String fullName = snapshot.child("fullname").getValue().toString();
                    String theSchool = snapshot.child("school").getValue().toString();
                    String theUsername = snapshot.child("username").getValue().toString();
                    Username.setText("Username: " +theUsername);
                    FullName.setText("Full name: "+fullName);
                    School.setText("School: "+theSchool);
                    persistUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DeclineRequest.setVisibility(View.INVISIBLE);
        DeclineRequest.setEnabled(false);
        if(!senderUID.equals(receiveUID)){
            sendRequest.setOnClickListener(v -> {
                sendRequest.setEnabled(false);
                if(state.equals("notfriends")){
                    SendContactRequest();
                }
                if(state.equals("requested")){
                    cancelReq();
                }
                if(state.equals("requestRec")){
                    acceptContactReq();
                }
                if(state.equals("friends")){
                    deleteContact();
                }

            });
        }
        else{
            DeclineRequest.setVisibility(View.INVISIBLE);
            sendRequest.setVisibility(View.INVISIBLE);
        }
    }

    private void deleteContact() {
        ContactRef.child(senderUID).child(receiveUID)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ContactRef.child(receiveUID).child(senderUID)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            sendRequest.setEnabled(true);
                                            state = "notfriends";
                                            sendRequest.setText("Send request");
                                            DeclineRequest.setVisibility(View.INVISIBLE);
                                            DeclineRequest.setEnabled(false);
                                        }
                                    }
                                });
                    }
                });
    }

    private void acceptContactReq() {
        Calendar calDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurDate = currentDate.format(calDate.getTime());

        ContactRef.child(senderUID).child(receiveUID).child("date").setValue(saveCurDate)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ContactRef.child(receiveUID).child(senderUID).child("date").setValue(saveCurDate)
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        ContactReqRef.child(senderUID).child(receiveUID)
                                                .removeValue()
                                                .addOnCompleteListener(task11 -> {
                                                    if(task11.isSuccessful()){
                                                        ContactReqRef.child(receiveUID).child(senderUID)
                                                                .removeValue()
                                                                .addOnCompleteListener(task111 -> {
                                                                    if(task111.isSuccessful()){
                                                                        sendRequest.setEnabled(true);
                                                                        state = "friends";
                                                                        sendRequest.setText("Remove contact");
                                                                        DeclineRequest.setVisibility(View.INVISIBLE);
                                                                        DeclineRequest.setEnabled(false);
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });

    }

    private void cancelReq() {
        ContactReqRef.child(senderUID).child(receiveUID)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ContactReqRef.child(receiveUID).child(senderUID)
                                .removeValue()
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        sendRequest.setEnabled(true);
                                        state = "notfriends";
                                        sendRequest.setText("Send request");
                                        DeclineRequest.setVisibility(View.INVISIBLE);
                                        DeclineRequest.setEnabled(false);
                                    }
                                });
                    }
                });
    }

    private void persistUI() {
        ContactReqRef.child(senderUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(receiveUID)){
                            String req = snapshot.child(receiveUID).child("request_type").getValue().toString();
                            if(req.equals("sent")){
                                state = "requested";
                                sendRequest.setText("Cancel request");
                                DeclineRequest.setVisibility(View.INVISIBLE);
                                DeclineRequest.setEnabled(false);
                            }
                            else if(req.equals("received")){
                                state = "requestRec";
                                sendRequest.setText("Accept request");
                                DeclineRequest.setVisibility(View.VISIBLE);
                                DeclineRequest.setEnabled(true);
                                DeclineRequest.setOnClickListener(v -> cancelReq());
                            }

                        }
                        else{
                            ContactRef.child(senderUID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.hasChild(receiveUID)){
                                                state = "friends";
                                                sendRequest.setText("Remove Contact");
                                                DeclineRequest.setVisibility(View.INVISIBLE);
                                                DeclineRequest.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendContactRequest() {
        ContactReqRef.child(senderUID).child(receiveUID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ContactReqRef.child(receiveUID).child(senderUID)
                                .child("request_type").setValue("received")
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        sendRequest.setEnabled(true);
                                        state = "requested";
                                        sendRequest.setText("Cancel request");
                                        DeclineRequest.setVisibility(View.INVISIBLE);
                                        DeclineRequest.setEnabled(false);
                                    }
                                });
                    }
                });
    }

    private void putInfo() {
        Username = findViewById(R.id.textView8);
        FullName = findViewById(R.id.textView11);
        School = findViewById(R.id.textView13);
        sendRequest = findViewById(R.id.button4);
        DeclineRequest = findViewById(R.id.button8);
        state = "notfriends";
    }
}