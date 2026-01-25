package com.nsoft.nchat;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter {
    private Activity context;
    private List<MessageModel> messages;
    private String myId;
    private static final int SEND_MESSAGE = 1;
    private static final int RECEIVED_MESSAGE = 0;

    public MyAdapter(Activity context, List<MessageModel> messages, String myId) {
        this.context = context;
        this.messages = messages;
        this.myId = myId;
    }

    public class sendMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public sendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
        }
    }

    public class receivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public receivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SEND_MESSAGE) {
            View myView = context.getLayoutInflater().inflate(R.layout.send_message_layout, parent, false);
            return new sendMessageViewHolder(myView);
        } else {
            View myView = context.getLayoutInflater().inflate(R.layout.received_message_layout, parent, false);
            return new receivedMessageViewHolder(myView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == SEND_MESSAGE) {
            sendMessageViewHolder myHolder = (sendMessageViewHolder) holder;
            String message = messages.get(position).getMessage();

            myHolder.message.setText(message);


        } else if (getItemViewType(position) == RECEIVED_MESSAGE) {
            receivedMessageViewHolder myHolder = (receivedMessageViewHolder) holder;
            String message = messages.get(position).getMessage();

            myHolder.message.setText(message);

        }
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        String senderId = messages.get(position).getSenderId();

        if (senderId.equals(myId)) {
            return SEND_MESSAGE;
        } else {
            return RECEIVED_MESSAGE;
        }
    }
}
