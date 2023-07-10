package com.example.plannect;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.plannect.Modals.Users;
import com.example.plannect.Notification.NotificationHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE =123 ;
    private static final int POST_NOTIFICATION_ID = 69;
    ImageView imageView;
    EditText description;
    ProgressBar progressBar;
    Button save_button;

    NotificationHelper notificationHelper;
    

    FirebaseUser currUser;
    StorageReference savePostStorageReference;
    DatabaseReference databaseReference;
    DatabaseReference postReference;

    String currUsername;
    String currUserDp;
    Uri imageUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        imageView = findViewById(R.id.activity_add_post_img);
        description = findViewById(R.id.activity_add_post_description);
        progressBar=findViewById(R.id.activity_add_post_progressbar);
        save_button=findViewById(R.id.activity_add_post_save_button);
        
        progressBar.setVisibility(View.INVISIBLE);

        notificationHelper = new NotificationHelper(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        
        
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageUri!=null) {
                    SavePost();
                }else {
                    Toast.makeText(AddPostActivity.this, "Image is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavePost() {

        progressBar.setVisibility(View.VISIBLE);

        currUser = FirebaseAuth.getInstance().getCurrentUser();
        savePostStorageReference = FirebaseStorage.getInstance().getReference("upload_post");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currUser.getUid());
        postReference=FirebaseDatabase.getInstance().getReference("Post");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);
                currUsername = users.getUsername();
                currUserDp =users.getProfileImg();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UploadImage();

sendNotification();


    }

    private void sendNotification() {

        NotificationCompat.Builder notification = notificationHelper.getPostNotification();
        notificationHelper.getNotificationManager().notify(POST_NOTIFICATION_ID,notification.build());
    }

    private void UploadImage() {

       StorageReference imageLocationReference =savePostStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

       imageLocationReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

               imageLocationReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {

                       String downloadImgUrl = uri.toString();
                       String description_string = description.getText().toString();
                       String currId = currUser.getUid();

                       DatabaseReference postIdReference= postReference.push();
                       String postId = postIdReference.getKey();

                       HashMap<String,Object> values = new HashMap<>();
                       values.put("id",currId);
                       values.put("username",currUsername);
                       values.put("profileImg",currUserDp);
                       values.put("description",description_string);
                       values.put("post",downloadImgUrl);
                       values.put("postId",postId);

                       postIdReference.setValue(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {

                               progressBar.setVisibility(View.INVISIBLE);

                               startActivity(new Intent(getApplicationContext(), MainActivity.class));
                               finish();

                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });


                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               });

           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });

    }

    private void SelectImage(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){

            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }

    }


    private String getFileExtension(Uri uri){

        ContentResolver contentResolver =getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
}