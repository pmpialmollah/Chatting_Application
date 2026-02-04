package com.nsoft.nchat;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.MyViewHolder> {
    private Activity activity;
    private List<ConversationModel> conversationModelList;

    public ConversationsAdapter(Activity activity, List<ConversationModel> conversationModelList) {
        this.activity = activity;
        this.conversationModelList = conversationModelList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, lastMessage, lastTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            lastTime = itemView.findViewById(R.id.lastTime);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myView = activity.getLayoutInflater().inflate(R.layout.chat_list_layout, parent, false);
        return new MyViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ConversationModel conversation = conversationModelList.get(position);
        holder.nameTextView.setText(conversation.getName());
        holder.lastMessage.setText(conversation.getLast_message());
        holder.lastTime.setText(conversation.getLast_time());

        holder.itemView.setOnClickListener(v -> {
            Intent myIntent = new Intent(activity, ChatActivity.class);
            myIntent.putExtra("receiver_name", conversation.getName());
            myIntent.putExtra("receiver_user_id", conversation.getUser_id());

            activity.startActivity(myIntent);
        });
    }

    @Override
    public int getItemCount() {
        if (conversationModelList.size() > 0) {
            return conversationModelList.size();
        }
        return 0;
    }

}
