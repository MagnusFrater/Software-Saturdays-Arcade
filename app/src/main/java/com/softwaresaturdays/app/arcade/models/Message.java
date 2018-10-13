package com.softwaresaturdays.app.arcade.models;

public class Message {

    private long timestamp;
    private String userId;
    private String type;

    public static String TYPE_TEXT_MESSAGE = "1";
    public static String TYPE_GIF_MESSAGE = "2";

    public Message() {

    }

    public Message(long timestamp, String userId, String type) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }
}
