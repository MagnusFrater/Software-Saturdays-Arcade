package com.softwaresaturdays.app.arcade.models;

public class Message {
    private long timestamp;
    private User author;

    public Message(long timestamp, User author) {
        this.timestamp = timestamp;
        this.author = author;
    }
}
