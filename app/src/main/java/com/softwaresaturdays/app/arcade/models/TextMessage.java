package com.softwaresaturdays.app.arcade.models;

public class TextMessage extends Message {
    private String text;

    public TextMessage(String text, User author) {
        super(System.currentTimeMillis(), author, Message.TYPE_TEXT_MESSAGE);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
