package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ChatViewHolder> {
    public List<chat> chatList;
    public FirebaseAuth authorize;
    public DatabaseReference userRef;

    public chatAdapter(List<chat> chatList){
        this.chatList = chatList;
    }
    public class ChatViewHolder extends RecyclerView.ViewHolder{
        public TextView sendText, receiveText;
        public ImageView sendPic, receivePic;
        public ChatViewHolder(@NonNull View itemView){
            super(itemView);
            sendText = itemView.findViewById(R.id.textView9);
            receiveText = itemView.findViewById(R.id.textView7);
            sendPic = itemView.findViewById(R.id.imageView2);
            receivePic = itemView.findViewById(R.id.imageView);
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages,parent,false);
        authorize = FirebaseAuth.getInstance();
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        String currUID = authorize.getCurrentUser().getUid();
        chat chats = chatList.get(position);
        String fromUID = chats.getFrom();
        String fromType= chats.getType();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.receiveText.setVisibility(View.INVISIBLE);
        holder.sendText.setVisibility(View.INVISIBLE);
        holder.sendPic.setVisibility(View.GONE);
        holder.receivePic.setVisibility(View.GONE);
        if(fromType.equals("text")){

            if(fromUID.equals(currUID)){
                String mess = chats.getMessage() + " \n" +"Sent: "+ chats.getDate() + "-"+ chats.getTime();
                holder.sendText.setVisibility(View.VISIBLE);
                holder.sendText.setText(mess);
            }
            else{
                String mess = chats.getMessage() + " \n" +"Sent: "+ chats.getDate() + "-"+ chats.getTime();
                holder.sendText.setVisibility(View.INVISIBLE);
                holder.receiveText.setVisibility(View.VISIBLE);
                holder.receiveText.setText(mess);
            }


        }
        else if(fromType.equals("image")){
            if(fromUID.equals(currUID)){

                holder.sendPic.setVisibility(View.VISIBLE);
                Picasso.get().load(chats.getMessage()).into(holder.sendPic);
            }
            else{
                holder.receivePic.setVisibility(View.VISIBLE);
                Picasso.get().load(chats.getMessage()).into(holder.receivePic);
            }
        }
        else if(fromType.equals("pdf")||fromType.equals("docx")){
            if(fromUID.equals(currUID)){
                holder.sendPic.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/my-application-83a36.appspot.com/o/file.png?alt=media&token=26e76d03-829f-41c6-9019-eb895f78c7e8").into(holder.sendPic);
            }
            else{
                holder.receivePic.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/my-application-83a36.appspot.com/o/file.png?alt=media&token=26e76d03-829f-41c6-9019-eb895f78c7e8").into(holder.receivePic);
            }
        }
        if(fromUID.equals(currUID)){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(chatList.get(position).getType().equals("pdf")||chatList.get(position).getType().equals("docx")){
                       CharSequence options[] = new CharSequence[]{
                               "Delete for me",
                               "Download and view this Document",
                               "Cancel",
                               "Delete for everyone"
                       };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("Message Options");
                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               if(which == 0){
                                   deleteSentMess(position,holder);
                                   Intent intent = new Intent(holder.itemView.getContext(),homeScreen.class);
                                   holder.itemView.getContext().startActivity(intent);
                               }
                               else if(which == 1){
                                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chatList.get(position).getMessage()));
                                   holder.itemView.getContext().startActivity(intent);
                               }

                               else if(which == 3){
                                   deleteMessEveryone(position,holder);
                                   Intent intent = new Intent(holder.itemView.getContext(),homeScreen.class);
                                   holder.itemView.getContext().startActivity(intent);
                               }
                           }
                       });
                       builder.show();
                   }
                   else if(chatList.get(position).getType().equals("text")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete for me",
                                "Cancel",
                                "Delete for everyone"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Message Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    deleteSentMess(position,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(),homeScreen.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                else if(which == 2){
                                    deleteMessEveryone(position,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(),homeScreen.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                   else if(chatList.get(position).getType().equals("image")){
                       CharSequence options[] = new CharSequence[]{
                               "Delete for me",
                               "View this image",
                               "Cancel",
                               "Delete for everyone"
                       };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("Message Options");
                       builder.setItems(options, (dialog, which) -> {
                           if(which == 0){
                               deleteSentMess(position,holder);
                               Intent intent = new Intent(holder.itemView.getContext(),homeScreen.class);
                               holder.itemView.getContext().startActivity(intent);
                           }
                           else if(which ==1){
                               Intent intent = new Intent(holder.itemView.getContext(),imageDisplay.class);
                               intent.putExtra("url", chatList.get(position).getMessage());
                               holder.itemView.getContext().startActivity(intent);
                           }
                           else if(which == 3){
                               deleteMessEveryone(position,holder);
                               Intent intent = new Intent(holder.itemView.getContext(),homeScreen.class);
                               holder.itemView.getContext().startActivity(intent);
                           }
                       });
                       builder.show();
                   }
                }
            });
        }
        else{
            holder.itemView.setOnClickListener(v -> {
                if(chatList.get(position).getType().equals("pdf")||chatList.get(position).getType().equals("docx")){
                    CharSequence options[] = new CharSequence[]{
                            "Delete for me",
                            "Download and view this Document",
                            "Cancel",
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Message Options");
                    builder.setItems(options, (dialog, which) -> {
                        if(which == 0){
                            deleteRecMess(position,holder);
                            Intent intent = new Intent(holder.itemView.getContext(),homeScreen.class);
                            holder.itemView.getContext().startActivity(intent);
                        }
                        else if(which == 1){
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chatList.get(position).getMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }

                    });
                    builder.show();
                }
                else if(chatList.get(position).getType().equals("text")){
                    CharSequence options[] = new CharSequence[]{
                            "Delete for me",
                            "Cancel",
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Message Options");
                    builder.setItems(options, (dialog, which) -> {
                        if(which == 0){
                            deleteRecMess(position,holder);
                            Intent intent = new Intent(holder.itemView.getContext(),homeScreen.class);
                            holder.itemView.getContext().startActivity(intent);
                        }

                    });
                    builder.show();
                }
                else if(chatList.get(position).getType().equals("image")){
                    CharSequence options[] = new CharSequence[]{
                            "Delete for me",
                            "View this image",
                            "Cancel",
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Message Options");
                    builder.setItems(options, (dialog, which) -> {
                        if(which == 0){
                            deleteRecMess(position,holder);
                            Intent intent = new Intent(holder.itemView.getContext(),homeScreen.class);
                            holder.itemView.getContext().startActivity(intent);
                        }
                        else if(which ==1){
                            Intent intent = new Intent(holder.itemView.getContext(),imageDisplay.class);
                            intent.putExtra("url", chatList.get(position).getMessage());
                            holder.itemView.getContext().startActivity(intent);
                        }

                    });
                    builder.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private void deleteSentMess(final int position, final ChatViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Messages").child(chatList.get(position).getFrom())
                .child(chatList.get(position).getTo()).child(chatList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void deleteRecMess(final int position, final ChatViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Messages").child(chatList.get(position).getTo())
                .child(chatList.get(position).getFrom()).child(chatList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void deleteMessEveryone(final int position, final ChatViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Messages").child(chatList.get(position).getTo())
                .child(chatList.get(position).getFrom()).child(chatList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ref.child("Messages").child(chatList.get(position).getFrom())
                                .child(chatList.get(position).getTo()).child(chatList.get(position).getMessageID())
                                .removeValue().addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(holder.itemView.getContext(), "error", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                    else{
                        Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
