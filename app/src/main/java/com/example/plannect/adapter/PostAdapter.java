package com.example.plannect.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plannect.Modals.Post;
import com.example.plannect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    List<Post> postList;
    DatabaseReference likeReference;
 DatabaseReference databaseReference;
 DatabaseReference checkDeleteReference;
boolean testClick=false;
    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);

        return  new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {

        Post post = postList.get(position);

        holder.username.setText(post.getUsername());
        Glide.with(context).load(post.getProfileImg()).into(holder.user_dp);
        Glide.with(context).load(post.getPost()).into(holder.post);
        holder.description.setText(post.getDescription());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();

        String postId = post.getPostId(); // getting currPost id


       holder.getLikeStatus(postId,userId);// reading the likes count

       databaseReference=FirebaseDatabase.getInstance().getReference("Likes");

       holder.like.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               testClick=true;
               likeReference.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {

                       if(testClick){
                           // if someone clicked the like button

                           if(snapshot.child(postId).hasChild(userId)){

                               likeReference.child(postId).child(userId).removeValue();
                               testClick=false;
                           }else {

                               likeReference.child(postId).child(userId).setValue(true); // add the value in the like node
                               testClick=false;

                           }

                       }

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
           }
       });

       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {

               checkDeleteReference = FirebaseDatabase.getInstance().getReference("Post").child(postId);

               checkDeleteReference.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {

                       Post post = snapshot.getValue(Post.class);

                       if (post !=null && post.getId()!=null && post.getId().equals(userId)){

                           alertDialogBox(position,postId);
                       }

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });



               return  true;

           }
       });


    }

    private void alertDialogBox(int position, String postId) {


        AlertDialog.Builder alertDialogBox = new AlertDialog.Builder(context);

        alertDialogBox.setTitle("Delete Post ?");
        alertDialogBox.setMessage("r u sure u wanna delete this post?");
        alertDialogBox.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Delete(position,postId);
            }
        });
        alertDialogBox.setNegativeButton("no",null);

        alertDialogBox.show();



    }

    private void Delete(int position, String postId) {

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Post").child(postId);

      databaseReference1.removeValue()
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                      Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();

                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                  }
              });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView user_dp;
        ImageView post;
        TextView username;
        TextView description;
        TextView like_count;

        ImageView like;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_dp = itemView.findViewById(R.id.post_item_card_view_dp_img_view);
            post = itemView.findViewById(R.id.post_item_main_post_img);
            username = itemView.findViewById(R.id.post_item_username);
            description = itemView.findViewById(R.id.post_item_description);
            like_count = itemView.findViewById(R.id.post_item_like_count);
            like=itemView.findViewById(R.id.post_item_like);

        }

        public void getLikeStatus(String postId, String userId) {

            //using this userId to check if user with id has liked the post with this postId

            likeReference = FirebaseDatabase.getInstance().getReference("Likes");


            likeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child(postId).hasChild(userId)){

                        // here postId node contains the id of user who have liked the post
                        // so here under the postId we just need to search our currUser id to find did our the user liked this
                        // particular post

                        // and counting the n of child will give total no of likes

                        int likeCount = (int) snapshot.child(postId).getChildrenCount();
                        like_count.setText(likeCount + " likes");
                        like.setImageResource(R.drawable.heart_fill);

                    }else {


                        int likeCount = (int) snapshot.child(postId).getChildrenCount();
                        like_count.setText(likeCount + " likes");
                        like.setImageResource(R.drawable.heart);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


    }
}
