package com.example.plannect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText email_edit_txt;
    EditText password_edit_txt;
    EditText username_edit_txt;
    Button register;

    FirebaseAuth firebaseAuth;
    FirebaseUser currUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username_edit_txt = findViewById(R.id.activity_register_username_edit_txt);
        email_edit_txt =   findViewById(R.id.activity_register_email_edit_txt);
        password_edit_txt =  findViewById(R.id.activity_register_password_edit_txt);
        register =  findViewById(R.id.activity_register_register_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = username_edit_txt.getText().toString().trim();
                String email = email_edit_txt.getText().toString().trim();
                String password = password_edit_txt.getText().toString().trim();


                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    RegisterUser(username,email,password);
                }else{
                    Toast.makeText(RegisterActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void RegisterUser(String username, String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            currUser= firebaseAuth.getCurrentUser();
                            String id = currUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id);

                            HashMap<String,Object> map = new HashMap<>();
                            map.put("username",username);
                            map.put("profileImg","default");
                            map.put("status","offline");
                            map.put("description","(description,keep it short)");
                            map.put("id",id);

                            databaseReference.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });



                        }else{

                            Toast.makeText(RegisterActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }
                });

    }
}