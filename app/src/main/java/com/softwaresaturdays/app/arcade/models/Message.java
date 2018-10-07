package com.softwaresaturdays.app.arcade.models;

public class Message {

    private long timestamp;
    private User author;
    private String type;

    public static String TYPE_TEXT_MESSAGE = "1";


    public Message(long timestamp, User author, String type) {
        this.timestamp = timestamp;
        this.author = author;
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public User getAuthor() {
        return author;
    }

    public String getType() {
        return type;
    }
}
