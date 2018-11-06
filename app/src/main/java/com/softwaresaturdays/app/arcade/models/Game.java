package com.softwaresaturdays.app.arcade.models;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;

public class Game {

    protected String title;
    protected Class cls;

    private HashMap<String, String> top1;
    private HashMap<String, String> top2;
    private HashMap<String, String> top3;

    public Game() {
        this("NO TITLE", null);
    }

    public Game(final String title) {
        this(title, null);
    }

    public Game(final String title, final Class cls) {
        this.title = title;
        this.cls = cls;
    }

    public void startGame(final Activity activity) {
        activity.startActivity(new Intent(activity.getApplicationContext(), cls));
    }

    public String getTitle() {
        return title;
    }

    public Class getCls() {
        return cls;
    }

    public HashMap<String, String> getTop1() {
        return top1;
    }

    public HashMap<String, String> getTop2() {
        return top2;
    }

    public HashMap<String, String> getTop3() {
        return top3;
    }
}
