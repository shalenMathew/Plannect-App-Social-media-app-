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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    EditText email ;
    EditText password;
    Button login;
    Button register;

    FirebaseAuth firebaseAuth;
FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();


        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser!=null){
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email= findViewById(R.id.activity_login_email_edit_txt);
        password=findViewById(R.id.activity_login_password_edit_txt);
        login = findViewById(R.id.activity_login_login_btn);
        register= findViewById(R.id.activity_login_register_btn);
        firebaseAuth=FirebaseAuth.getInstance();



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_string=email.getText().toString().trim();
                String password_string = password.getText().toString().trim();


                if(!TextUtils.isEmpty(email_string) && !TextUtils.isEmpty(password_string)) {
                    LoginUsers(email_string, password_string);
                }else {
                    Toast.makeText(LoginActivity.this, "fields r empty", Toast.LENGTH_SHORT).show();
                }



            }
        });


    }

    private void LoginUsers(String email_string, String password_string) {

        firebaseAuth.signInWithEmailAndPassword(email_string,password_string)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();

                        }else {
                            Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}