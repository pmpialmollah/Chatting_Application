package com.nsoft.nchat;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    private Activity activity;
    private List<String> receiverList;

    public ChatListAdapter(Activity activity, List<String> receiverList) {
        this.activity = activity;
        this.receiverList = receiverList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView iconTextView, nameTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iconTextView = itemView.findViewById(R.id.iconTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
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
        holder.iconTextView.setText("" + (position + 1));
        holder.nameTextView.setText(receiverList.get(position));
    }

    @Override
    public int getItemCount() {
        return receiverList.size();
    }

}
