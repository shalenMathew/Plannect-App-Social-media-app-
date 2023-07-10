package com.example.plannect.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plannect.MessageActivity;
import com.example.plannect.Modals.Users;
import com.example.plannect.R;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    List<Users> usersList;
    boolean isChat;

    public UserAdapter(Context context, List<Users> usersList, boolean isChat) {
        this.context = context;
        this.usersList = usersList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);

return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {

        Users users = usersList.get(position);

        holder.username.setText(users.getUsername());

        if(users.getProfileImg().equals("default")){
            holder.dp.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(users.getProfileImg()).into(holder.dp);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, MessageActivity.class);
                    i.putExtra("senderId",users.getId());
                    i.putExtra("senderUsername",users.getUsername());
                    context.startActivity(i);
                }
            });
        }


        if(isChat){

            if(users.getStatus().equals("online")){
                holder.status_icon.setVisibility(View.VISIBLE);
            }else {
               holder.status_icon.setVisibility(View.GONE);
            }

        }else {
            holder.status_icon.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView dp;
        TextView username;

        ImageView status_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dp=itemView.findViewById(R.id.chat_item_card_view_dp_img_view);
            username=itemView.findViewById(R.id.chat_item_username);
            status_icon = itemView.findViewById(R.id.chat_item_status_icon);

        }
    }
}
