package com.softwaresaturdays.app.arcade.models;

import com.softwaresaturdays.app.arcade.MyApplication;

public class GifMessage extends Message {
    public String url;

    public GifMessage() {

    }

    public GifMessage(String url) {
        super(System.currentTimeMillis(), MyApplication.currUser.getUid(), Message.TYPE_GIF_MESSAGE);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
