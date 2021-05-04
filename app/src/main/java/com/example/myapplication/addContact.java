package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class addContact extends AppCompatActivity {
    public Button btn;
    public EditText findContact;
    public RecyclerView searchList;
    public DatabaseReference searchUsers;
    public DatabaseReference ContactReqRef;
    public FirebaseAuth authorize;
    String currentUser;
    public Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        authorize = FirebaseAuth.getInstance();
        currentUser = authorize.getCurrentUser().getUid();
        searchUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactReqRef = FirebaseDatabase.getInstance().getReference().child("ContactRequests").child(currentUser);
        btn = findViewById(R.id.button6);
        findContact = findViewById(R.id.editTextTextPersonName5);
        searchList = findViewById(R.id.searchList);
        searchList.setHasFixedSize(true);
        searchList.setLayoutManager(new LinearLayoutManager(this));
        myToolbar = findViewById(R.id.myToolbar);
        myToolbar.setTitle("Enter Username");
        setSupportActionBar(myToolbar);
        Query searchPeopleAndFriendsQuery = ContactReqRef.orderByChild("request_type")
                .startAt("received").endAt("received" + "\uf8ff");;
        super.onStart();
        FirebaseRecyclerOptions<findContacts> options =
                new FirebaseRecyclerOptions.Builder<findContacts>()
                        .setQuery(searchPeopleAndFriendsQuery, findContacts.class)
                        .build();
        FirebaseRecyclerAdapter adapter  = new FirebaseRecyclerAdapter<findContacts, FindFriendsViewHolder>(options) {
            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.searchingforcontacts,parent,false);
                FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                return  viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull findContacts model) {
                String UIDs = getRef(position).getKey();
                searchUsers.child(UIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("fullname")){
                            String fullname = snapshot.child("fullname").getValue().toString();
                            String school  = snapshot.child("school").getValue().toString();
                            holder.myUsername.setText("New contact request");
                            holder.mySchool.setText(fullname);
                            holder.myFullName.setText(school);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.lolView.setOnClickListener(v -> {
                    String UserID = getRef(position).getKey();
                    Intent findIntent =new Intent(addContact.this, contactPage.class);
                    findIntent.putExtra("USERID", UserID);
                    startActivity(findIntent);
                });

            }



        };
        searchList.setAdapter(adapter);
        adapter.startListening();
        btn.setOnClickListener(v -> {
            String lookingFor = findContact.getText().toString();
           searchDbForContacts(lookingFor);
        });
    }

    private void searchDbForContacts(String lookingFor) {
        Query searchPeopleAndFriendsQuery = searchUsers.orderByChild("username")
                .startAt(lookingFor).endAt(lookingFor + "\uf8ff");
        super.onStart();
        FirebaseRecyclerOptions<findContacts> options =
                new FirebaseRecyclerOptions.Builder<findContacts>()
                        .setQuery(searchPeopleAndFriendsQuery, findContacts.class)
                        .build();
        FirebaseRecyclerAdapter adapter  = new FirebaseRecyclerAdapter<findContacts, FindFriendsViewHolder>(options) {
            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.searchingforcontacts,parent,false);
                FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                return  viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull findContacts model) {
                holder.myUsername.setText(model.getUsername());
                holder.mySchool.setText(model.getSchool());
                holder.myFullName.setText(model.getFullname());
                holder.lolView.setOnClickListener(v -> {
                    String UserID = getRef(position).getKey();
                    Intent findIntent =new Intent(addContact.this, contactPage.class);
                    findIntent.putExtra("USERID", UserID);
                    startActivity(findIntent);
                });

            }



        };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class  FindFriendsViewHolder extends RecyclerView.ViewHolder
    {
        TextView myUsername, mySchool, myFullName;
        View lolView;

        public FindFriendsViewHolder (@NonNull View itemView)
        {
            super(itemView);
            lolView = itemView;
            myUsername = itemView.findViewById(R.id.username);
            mySchool = itemView.findViewById(R.id.userSchool);
            myFullName = itemView.findViewById(R.id.userFullName);
        }
    }
    }
