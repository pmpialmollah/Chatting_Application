package com.nsoft.nchat;

public class ChatMessageModel {
    private String message_id, conversation_id, sender_id, message, status, time;

    public ChatMessageModel(String message_id, String conversation_id, String sender_id, String message, String status, String time) {
        this.message_id = message_id;
        this.conversation_id = conversation_id;
        this.sender_id = sender_id;
        this.message = message;
        this.status = status;
        this.time = time;
    }

    public String getMessage_id() {
        return message_id;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }
}
