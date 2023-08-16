package com.example.plannect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.plannect.fragment.ChatFragment;
import com.example.plannect.fragment.HomeFragment;
import com.example.plannect.fragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // this is update from trial branch

    // this patch 2.0 from trial_branch

    // Activating patch_branch DNA ... Activation may take some time

    ChipNavigationBar chipNavigationBar;
   Toolbar toolbar;

   FirebaseAuth firebaseAuth;
   DatabaseReference databaseReference;
   FirebaseUser currUser;

//   FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onResume() {
        super.onResume();
        CheckStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        CheckStatus("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CheckStatus("offline");
    }

    private void CheckStatus(String status) {

        HashMap<String,Object> map = new HashMap<>();
        map.put("status",status);

        databaseReference.updateChildren(map);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chipNavigationBar=findViewById(R.id.bottom_nav_menu);

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        chipNavigationBar.setItemSelected(R.id.home,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        firebaseAuth= FirebaseAuth.getInstance();
        currUser = firebaseAuth.getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(currUser.getUid());


        CheckStatus("online");

        bottomMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.logout_menu,menu);

        return  true;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.logout){
            firebaseAuth.signOut();

            HashMap<String,Object> map = new HashMap<>();
            map.put("status","offline");
            databaseReference.updateChildren(map);


            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // makes sure all activity is removed from stack
            startActivity(i);
        } else if (item.getItemId()==R.id.add_post) {
            startActivity(new Intent(MainActivity.this,AddPostActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                Fragment fragment = null;

              if(i==R.id.home){
                  fragment=new HomeFragment();
              } else if (i==R.id.chat) {
                  fragment = new ChatFragment();
              }else if(i==R.id.profile){
                  fragment= new ProfileFragment();
              }

              getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

    }
}