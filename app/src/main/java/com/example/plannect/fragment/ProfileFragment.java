package com.example.plannect.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.plannect.EditProfile;
import com.example.plannect.Modals.Users;
import com.example.plannect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;


public class ProfileFragment extends Fragment {



    RelativeLayout relativeLayout;

    TextView username;
    TextView description;
    ImageView dp;
//    StorageReference storageReference;
    DatabaseReference databaseReference;
    Uri imgUri;
    FirebaseUser currUser;
//    StorageTask uploadTask;
    Context context;

    Users users;




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context=null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


       View view =inflater.inflate(R.layout.fragment_profile, container, false);

        relativeLayout = view.findViewById(R.id.fragment_profile_card_view_relative);

        currUser = FirebaseAuth.getInstance().getCurrentUser();

//        storageReference = FirebaseStorage.getInstance().getReference("profile_images");



        username = view.findViewById(R.id.fragment_profile_username);
        description = view.findViewById(R.id.fragment_profile_description);
        dp=view.findViewById(R.id.fragment_profile_img);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(context==null || !isAdded()){
                    return;
                }

               users = snapshot.getValue(Users.class);

                username.setText(users.getUsername());
                description.setText(users.getDescription());

                if(users.getProfileImg().equals("default")){
                    dp.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(context).load(users.getProfileImg()).into(dp);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), EditProfile.class);


                    i.putExtra("username", username.getText().toString());
                    i.putExtra("description", description.getText().toString());
                    i.putExtra("profileImg", users.getProfileImg());
                    startActivity(i);


            }
        });

        return  view;
    }


}