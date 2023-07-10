package com.example.plannect.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plannect.Modals.Users;
import com.example.plannect.R;
import com.example.plannect.adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<Users> usersList;

    FirebaseUser currUser;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        usersList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.fragment_chat_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ReadUsers();

        return  view;
    }

    private void ReadUsers() {

        currUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currUser != null;
        String currUserId = currUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                   Users users = dataSnapshot.getValue(Users.class);

                    if(!users.getId().equals(currUserId)){
                        usersList.add(users);
                    }

                    userAdapter = new UserAdapter(getContext(),usersList,true);
                    recyclerView.setAdapter(userAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}