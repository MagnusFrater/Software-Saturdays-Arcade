package com.softwaresaturdays.app.arcade.models;

import android.net.Uri;

import java.util.HashMap;

public class User {
    private String email;
    private String name;
    private String photoUrl;
    private String uid;
    private String fcmToken;
    private HashMap<String, Double> highScores;

    public User() {

    }

    public User(String email, String displayName, Uri photoUrl, String uid) {
        this.email = email;
        this.name = displayName;
        this.photoUrl = photoUrl.toString();
        this.uid = uid;
        this.highScores = new HashMap<>();
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

    public void checkAndUpdateUserHighScore(String game, double score) {
        highScores.put(game, score);
    }

    public HashMap<String, Double> getHighScores() {
        return this.highScores;
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
