package com.softwaresaturdays.app.arcade.models;

import com.softwaresaturdays.app.arcade.MyApplication;

public class TextMessage extends Message {
    private String text;

    public TextMessage() {
        super();
    }

    public TextMessage(String text) {
        super(System.currentTimeMillis(), MyApplication.currUser.getUid(), Message.TYPE_TEXT_MESSAGE);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
