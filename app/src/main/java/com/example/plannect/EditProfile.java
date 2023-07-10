package com.example.plannect;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    private static final int IMAGE_GALLERY_REQUEST_CODE = 911 ;
    EditText username_edit;
    EditText description_edit;
    ImageView dp_edit;

    Button button_edit;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    Uri imgUri;

    StorageTask uploadTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        username_edit=findViewById(R.id.activity_edit_profile_username);
        description_edit= findViewById(R.id.activity_edit_profile_description);
        dp_edit=findViewById(R.id.activity_edit_profile_dp);
        button_edit = findViewById(R.id.activity_edit_profile_button);

        Intent i = getIntent();
       String username = i.getStringExtra("username");
       String description= i.getStringExtra("description");
       String profileImg = i.getStringExtra("profileImg");

       storageReference = FirebaseStorage.getInstance().getReference("upload_profileImg");
       firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


       String id = firebaseUser.getUid();
       databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(id);
       

       username_edit.setText(username);
       description_edit.setText(description);

       if(profileImg.equals("default")){
           dp_edit.setImageResource(R.mipmap.ic_launcher);
       }else {
           Glide.with(getApplicationContext()).load(profileImg).into(dp_edit);
       }



       dp_edit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               SelectImage();

           }
       });

       button_edit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String updated_username = username_edit.getText().toString().trim();
               String updated_description = description_edit.getText().toString().trim();
               UpdateData(updated_username,updated_description);


               if(imgUri!=null){
                   UploadUpdatedData();
               }else {
                   finish();
               }

           }
       });


    }




    private void SelectImage() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, IMAGE_GALLERY_REQUEST_CODE);

    }

    private void UploadUpdatedData() {



        if(imgUri!=null){

            ProgressDialog progressDialog = new ProgressDialog(EditProfile.this);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imgUri));

            uploadTask = fileReference.putFile(imgUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){

                        Uri updatedImgUri  = (Uri) task.getResult();
                        String updatedImgUrl = updatedImgUri.toString();

                        HashMap<String,Object> updatedValue = new HashMap<>();
                        updatedValue.put("profileImg",updatedImgUrl);

                        databaseReference.updateChildren(updatedValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if ((task.isSuccessful())){
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    finish();
                                }else {
                                    Toast.makeText(EditProfile.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }

                            }
                        });


                    }else {
                        Toast.makeText(EditProfile.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_GALLERY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            imgUri=data.getData();
            dp_edit.setImageURI(imgUri);
        }
    }

    private void UpdateData(String updated_username, String updated_description) {

        if(!updated_username.isEmpty()) {


            HashMap<String, Object> updated_values = new HashMap<>();
            updated_values.put("username", updated_username);
            updated_values.put("description", updated_description);

            databaseReference.updateChildren(updated_values)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {


                            } else {
                                Toast.makeText(EditProfile.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

        }else {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
        }


    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver =getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


}