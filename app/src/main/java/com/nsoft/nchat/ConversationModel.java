package com.nsoft.nchat;

public class ConversationModel {
    private String user_id, name, online_status, last_time, last_message;

    public ConversationModel(String user_id, String name, String online_status, String last_time, String last_message) {
        this.user_id = user_id;
        this.name = name;
        this.online_status = online_status;
        this.last_time = last_time;
        this.last_message = last_message;
    }

    public String getName() {
        return name;
    }

    public String getOnline_status() {
        return online_status;
    }

    public String getLast_time() {
        return last_time;
    }

    public String getLast_message() {
        return last_message;
    }

    public String getUser_id() {
        return user_id;
    }
}
