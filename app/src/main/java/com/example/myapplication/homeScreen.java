package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import org.w3c.dom.Text;

public class homeScreen extends AppCompatActivity {
    public Toolbar myToolbar;
    public FirebaseAuth authorize;
    public RecyclerView contactList;
    public DatabaseReference contactRef, usersRef;
    public String currUID, fullname, school, username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        authorize = FirebaseAuth.getInstance();
        currUID = authorize.getCurrentUser().getUid();
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currUID);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactList = findViewById(R.id.contactList);
        contactList.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<findContacts>()
                .setQuery(contactRef,findContacts.class)
                .build();
        FirebaseRecyclerAdapter<findContacts, ContactsViewHolder > adapter = new FirebaseRecyclerAdapter<findContacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull findContacts model) {
                String UIDs = getRef(position).getKey();
                usersRef.child(UIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("fullname")){
                            fullname = snapshot.child("fullname").getValue().toString();
                            school  = snapshot.child("school").getValue().toString();
                            username  = snapshot.child("username").getValue().toString();
                            holder.myUsername.setText(fullname);
                            holder.mySchool.setText(school);
                            holder.myFullName.setText(username);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.lolView.setOnClickListener(v -> {
                    String UserID = getRef(position).getKey();
                    Intent findIntent =new Intent(homeScreen.this, messages.class);
                    findIntent.putExtra("USERID", UserID);
                    findIntent.putExtra("name", fullname);
                    startActivity(findIntent);
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.searchingforcontacts,parent,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return  viewHolder;
            }
        };
        contactList.setAdapter(adapter);
        adapter.startListening();
        myToolbar = findViewById(R.id.myToolbar);
        myToolbar.setTitle("Welcome");
        setSupportActionBar(myToolbar);
    }
    public static class ContactsViewHolder extends  RecyclerView.ViewHolder{
        TextView myUsername, mySchool, myFullName;
        View lolView;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            lolView = itemView;
            myUsername = itemView.findViewById(R.id.username);
            mySchool = itemView.findViewById(R.id.userSchool);
            myFullName = itemView.findViewById(R.id.userFullName);
        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Add) {

            startActivity(new Intent(homeScreen.this, addContact.class));
            return true;
        }
        if (id == R.id.logout) {
            authorize.signOut();
            startActivity(new Intent(homeScreen.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
