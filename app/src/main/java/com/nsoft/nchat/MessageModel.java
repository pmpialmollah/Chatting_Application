package com.nsoft.nchat;

public class MessageModel {
    private String senderId, receiverId, message;
    private boolean isTyping;

    public MessageModel(String senderId, String receiverId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.isTyping = false;
    }

    public MessageModel(boolean isTyping){
        this.isTyping = isTyping;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isTyping() {
        return isTyping;
    }
}
