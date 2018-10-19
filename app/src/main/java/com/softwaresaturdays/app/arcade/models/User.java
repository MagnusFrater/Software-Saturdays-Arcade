package com.softwaresaturdays.app.arcade.models;

import android.net.Uri;

public class User {
    private String email;
    private String name;
    private String photoUrl;
    private String uid;

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

    public String toString() {
        return name + "," + uid + "," + photoUrl + "," + email;
    }

    public User(String hashcode) {
        String[] items = hashcode.split(",");

        if (items.length != 4) {
            return;
        }

        this.name = items[0];
        this.uid = items[1];
        this.photoUrl = items[2];
        this.email = items[3];
    }
}
