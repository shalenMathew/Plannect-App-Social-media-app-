package com.example.plannect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.plannect.Modals.Chats;
import com.example.plannect.Modals.Users;
import com.example.plannect.adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<Chats> chatsList;
    ImageView dp;
    TextView username;
    EditText message_edit;
    ImageButton send_btn;
    String senderId;
    String senderUsername;
    String receiverUsername;

    FirebaseUser currUser;
    DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        dp = findViewById(R.id.activity_message_dp);
        username = findViewById(R.id.activity_message_username);
        message_edit=findViewById(R.id.activity_message_edit_text);
        chatsList = new ArrayList<>();

        currUser= FirebaseAuth.getInstance().getCurrentUser();
        String currUserId= currUser.getUid();
        send_btn=findViewById(R.id.activity_message_btn);

        recyclerView = findViewById(R.id.activity_message_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        Intent i = getIntent();
        senderId = i.getStringExtra("senderId");
        senderUsername = i.getStringExtra("senderUsername");

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(senderId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                username.setText(users.getUsername());

                if(users.getProfileImg().equals("default")){
                    dp.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getApplicationContext()).load(users.getProfileImg()).into(dp);
                }

                ReadMessage(currUserId,senderId,message_edit.getText().toString().trim());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = message_edit.getText().toString();


                if(!message.isEmpty()) {
                    SendMessage(currUserId,senderId,message);
                }

                message_edit.setText("");

            }
        });

        isSeen(senderId);

    }

    private void isSeen(String senderId) {

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Chats chats = dataSnapshot.getValue(Chats.class);

                    if(chats.getReceiver().equals(currUser.getUid()) && chats.getSender().equals(senderId)){

                        HashMap<String,Object> map = new HashMap<>();
                        map.put("seen",true);

                        dataSnapshot.getRef().updateChildren(map);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void ReadMessage(String currUserId, String senderId, String message) {

        DatabaseReference readReference = FirebaseDatabase.getInstance().getReference("Chats");

        readReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatsList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    Chats chats = dataSnapshot.getValue(Chats.class);

                    if(chats.getSender().equals(currUserId) && chats.getReceiver().equals(senderId)
                    ||  chats.getSender().equals(senderId) && chats.getReceiver().equals(currUserId)){

                        chatsList.add(0,chats);

                    }
                }

                messageAdapter = new MessageAdapter(MessageActivity.this,chatsList);
                recyclerView.setAdapter(messageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SendMessage(String currUserId, String senderId, String message) {


      DatabaseReference messageReference = FirebaseDatabase.getInstance().getReference("Chats");

      DatabaseReference currUserReference =  FirebaseDatabase.getInstance().getReference("Users").child(currUserId);

      currUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              Users users = snapshot.getValue(Users.class);

              receiverUsername = users.getUsername();

              HashMap<String, Object> values = new HashMap<>();
              values.put("sender",currUserId);
              values.put("senderusername",senderUsername);
              values.put("receiverusername",receiverUsername);
              values.put("receiver",senderId);
              values.put("seen",false);
              values.put("message",message);

              messageReference.push().setValue(values);
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });


    }

}