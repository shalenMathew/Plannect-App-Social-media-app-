package com.example.plannect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plannect.Modals.Chats;
import com.example.plannect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    // the shitty username design is annoyong repeating istself again , it has novelty in the beginning , but as
    // time passes it feels repeated, shitty ui/ux

    private static final int MSG_TYPE_RIGHT = 0;
    private static final int MSG_TYPE_LEFT = 1;

FirebaseUser currUser;

    Context context;
    List<Chats> chatsList;

    public MessageAdapter(Context context, List<Chats> chatsList) {
        this.context = context;
        this.chatsList = chatsList;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType==MSG_TYPE_RIGHT){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_receiver,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_sender,parent,false);
        }

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chats chats = chatsList.get(position);



        holder.message_txt.setText("\"" +chats.getMessage()+"\""+"\n"+"\n"+"-"+chats.getReceiverusername());


        if(position==0){


            if(chats.isSeen()){
                holder.seen_status.setText("seen");
            }else {
                holder.seen_status.setText("delivered");
            }


        }else {
            holder.seen_status.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView message_txt;
        TextView seen_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message_txt = itemView.findViewById(R.id.chat_item_show_message);
            seen_status = itemView.findViewById(R.id.chat_item_seen);

        }
    }


    @Override
    public int getItemViewType(int position) {

        currUser= FirebaseAuth.getInstance().getCurrentUser();

        if(chatsList.get(position).getSender().equals(currUser.getUid())){

            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }

    }
}
