package com.softwaresaturdays.app.arcade.models;

public class TextMessage extends Message {
    private String text;

    public TextMessage(String text, User author) {
        super(System.currentTimeMillis(), author);
        this.text = text;
    }
}
