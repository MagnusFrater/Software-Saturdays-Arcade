package com.softwaresaturdays.app.arcade.models;

import com.softwaresaturdays.app.arcade.MyApplication;

public class GifMessage extends Message {
    public String url;
    public String searchText;

    public GifMessage() {

    }

    public GifMessage(String url, String searchText) {
        super(System.currentTimeMillis(), MyApplication.currUser.getUid(), Message.TYPE_GIF_MESSAGE);
        this.url = url;
        this.searchText = searchText;
    }

    public String getUrl() {
        return url;
    }

    public String getSearchText() {
        return searchText;
    }
}
