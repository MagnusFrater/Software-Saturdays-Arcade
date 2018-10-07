package com.softwaresaturdays.app.arcade.models;

public class TextMessage extends Message {
    private String text;

    public TextMessage() {
        super();
    }

    public TextMessage(String text, String userId) {
        super(System.currentTimeMillis(), userId, Message.TYPE_TEXT_MESSAGE);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
