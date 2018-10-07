package com.softwaresaturdays.app.arcade.models;

import android.net.Uri;

public class User {
    private String email;
    private String name;
    private String photoUrl;
    private String uid;

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
}
