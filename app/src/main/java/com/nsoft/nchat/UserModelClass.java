package com.nsoft.nchat;

public class UserModelClass {
    private String user_id, name, online_status, last_seen, bio, verification_badge;

    public UserModelClass(String user_id, String name, String online_status, String last_seen, String bio, String verification_badge) {
        this.user_id = user_id;
        this.name = name;
        this.online_status = online_status;
        this.last_seen = last_seen;
        this.bio = bio;
        this.verification_badge = verification_badge;
    }

    public String getName() {
        return name;
    }

    public String getOnline_status() {
        return online_status;
    }

    public String getLast_seen() {
        return last_seen;
    }

    public String getBio() {
        return bio;
    }

    public String getVerification_badge() {
        return verification_badge;
    }

    public String getUser_id() {
        return user_id;
    }
}
