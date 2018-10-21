package com.softwaresaturdays.app.arcade.models;

import android.net.Uri;

public class User {
    private String email;
    private String name;
    private String photoUrl;
    private String uid;
    private String fcmToken;

    public User() {

    }

    public User(String email, String displayName, Uri photoUrl, String uid) {
        this.email = email;
        this.name = displayName;
        this.photoUrl = photoUrl.toString();
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String toString() {
        return name + "," + uid + "," + photoUrl + "," + email + "," + fcmToken;
    }

    public User(String hashcode) {
        String[] items = hashcode.split(",");

        try {
            this.name = items[0];
            this.uid = items[1];
            this.photoUrl = items[2];
            this.email = items[3];
            this.fcmToken = items[4];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
