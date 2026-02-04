package com.nsoft.nchat;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PrivateMessageAdapter extends RecyclerView.Adapter {
    private Activity context;
    private List<MessageModel> messages;
    private String myId;
    private static final int SEND_MESSAGE = 1;
    private static final int RECEIVED_MESSAGE = 0;
    private static final int TYPING_MESSAGE = 2;

    public PrivateMessageAdapter(Activity context, List<MessageModel> messages, String myId) {
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
        } else if (viewType == RECEIVED_MESSAGE) {
            View myView = context.getLayoutInflater().inflate(R.layout.received_message_layout, parent, false);
            return new receivedMessageViewHolder(myView);
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
        } else {
            receivedMessageViewHolder myHolder = (receivedMessageViewHolder) holder;
            myHolder.message.setText("Typing...");
        }

    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel messageModel = messages.get(position);

        if (messageModel.isTyping()) {
            return TYPING_MESSAGE;
        }

        if (messageModel.getSenderId().equals(myId)) {
            return SEND_MESSAGE;
        } else {
            return RECEIVED_MESSAGE;
        }
    }

    // my custom method
    public void showTypingIndication(boolean isTyping) {
        if (isTyping) {
            boolean alreadyHasTyping = false;
            if (!messages.isEmpty()) {

                if (messages.get(getItemCount() - 1).isTyping()) {
                    alreadyHasTyping = true;
                }
            }

            if (!alreadyHasTyping) {
                messages.add(new MessageModel(true));
                notifyItemInserted(messages.size() - 1);
            }
        } else {
            for (int i = messages.size() - 1; i >= 0; i--) {
                if (messages.get(i).isTyping()) {
                    messages.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }
}
