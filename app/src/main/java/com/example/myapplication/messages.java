package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class messages extends AppCompatActivity {
    public String recMessID, recName, senderID;
    public Button messSendButton,sendOther;
    public EditText textMess;
    public FirebaseAuth authorize;
    public DatabaseReference ref;
    public String time, date;
    public String check = "", myUrl = "";
    public final List<chat> chatlist = new ArrayList<>();
    public LinearLayoutManager linearLayoutManager;
    public chatAdapter chatAdapter;
    public RecyclerView messagesDisplay;
    public Uri fileURI;
    public UploadTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        authorize = FirebaseAuth.getInstance();
        senderID = authorize.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference();
        messSendButton = findViewById(R.id.button2);
        sendOther = findViewById(R.id.button9);
        textMess = findViewById(R.id.editTextTextPersonName2);
        recMessID = getIntent().getExtras().get("USERID").toString();
        recName = getIntent().getExtras().get("name").toString();
        startStuff();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        date = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        time = currentTime.format(calendar.getTime());
        messSendButton.setOnClickListener(v -> sendMess());
        sendOther.setOnClickListener(v -> {
            CharSequence options[] = new CharSequence[]{
              "Images",
              "PDF Files",
              "MS Word"
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(messages.this);
            builder.setTitle("Select file type");
            builder.setItems(options, (dialog, which) -> {
                if(which == 0){
                    check = "image";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent,"Select Image"), 438);
                }
                if(which == 1){
                    check = "pdf";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent.createChooser(intent,"Select PDF file"), 438);
                }
                if(which == 2){
                    check = "docx";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    startActivityForResult(intent.createChooser(intent,"Select MS Word file"), 438);
                }
            });
            builder.show();
        });

        ref.child("Messages").child(senderID).child(recMessID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        chat chats = snapshot.getValue(chat.class);
                        chatlist.add(chats);
                        chatAdapter.notifyDataSetChanged();
                        messagesDisplay.smoothScrollToPosition(chatAdapter.getItemCount()-1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void startStuff() {
        chatAdapter = new chatAdapter(chatlist);
        messagesDisplay = findViewById(R.id.messageDisplay);
        linearLayoutManager = new LinearLayoutManager(this);
        messagesDisplay.setLayoutManager(linearLayoutManager);
        messagesDisplay.setAdapter(chatAdapter);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null){
            fileURI = data.getData();
            if(!check.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
                String messSenderRef = "Messages/" + senderID + "/" + recMessID;
                String messRecRef = "Messages/" + recMessID + "/" + senderID;
                DatabaseReference userKeyRef = ref.child("Messages")
                        .child(senderID).child(recMessID).push();
                String messID = userKeyRef.getKey();
                StorageReference filepath = storageReference.child(messID+"."+check);
                filepath.putFile(fileURI).addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                    String DownloadURL = uri.toString();
                    HashMap text = new HashMap();
                    text.put("message", DownloadURL);
                    text.put("NOF", fileURI.getLastPathSegment());
                    text.put("type", check);
                    text.put("from", senderID);
                    text.put("to", recMessID);
                    text.put("messageID", messID);
                    text.put("time", time);
                    text.put("date", date);
                    HashMap messDetails = new HashMap();
                    messDetails.put(messSenderRef + "/" + messID, text);
                    messDetails.put(messRecRef + "/" + messID, text);
                    ref.updateChildren(messDetails);
                }).addOnFailureListener(e -> Toast.makeText(messages.this, e.getMessage(), Toast.LENGTH_SHORT).show()));
            }
            else if(check.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                String messSenderRef = "Messages/" + senderID + "/" + recMessID;
                String messRecRef = "Messages/" + recMessID + "/" + senderID;
                DatabaseReference userKeyRef = ref.child("Messages")
                        .child(senderID).child(recMessID).push();
                String messID = userKeyRef.getKey();
                StorageReference filepath = storageReference.child(messID+"."+"jpeg");
                uploadTask = filepath.putFile(fileURI);
                uploadTask.continueWithTask((Continuation) task -> {
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return filepath.getDownloadUrl();
                }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                    if(task.isSuccessful()){
                        Uri downloadURL = task.getResult();
                        myUrl = downloadURL.toString();
                        String messID1 = userKeyRef.getKey();
                        HashMap text = new HashMap();
                        text.put("message", myUrl);
                        text.put("NOF", fileURI.getLastPathSegment());
                        text.put("type", check);
                        text.put("from", senderID);
                        text.put("to", recMessID);
                        text.put("messageID", messID1);
                        text.put("time", time);
                        text.put("date", date);
                        HashMap messDetails = new HashMap();
                        messDetails.put(messSenderRef + "/" + messID1, text);
                        messDetails.put(messRecRef + "/" + messID1, text);
                        ref.updateChildren(messDetails).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(messages.this, "Message sent", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(messages.this, "Error: message not sent", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                });
            }
            else{
                Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMess(){
        String messText = textMess.getText().toString();
        if (TextUtils.isEmpty(messText)) {
            Toast.makeText(this, "Write a message", Toast.LENGTH_SHORT).show();
        }
        else {
            String messSenderRef = "Messages/" + senderID + "/" + recMessID;
            String messRecRef = "Messages/" + recMessID + "/" + senderID;
            DatabaseReference userKeyRef = ref.child("Messages")
                    .child(senderID).child(recMessID).push();
            String messID = userKeyRef.getKey();
            HashMap text = new HashMap();
            text.put("message", messText);
            text.put("type", "text");
            text.put("from", senderID);
            text.put("to", recMessID);
            text.put("messageID", messID);
            text.put("time", time);
            text.put("date", date);
            text.put("NOF", "");
            HashMap messDetails = new HashMap();
            messDetails.put(messSenderRef + "/" + messID, text);
            messDetails.put(messRecRef + "/" + messID, text);
            ref.updateChildren(messDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(messages.this, "Sent", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(messages.this, "Error: message not sent", Toast.LENGTH_SHORT).show();
                }
                textMess.setText("");
            });
        }
    }
}
