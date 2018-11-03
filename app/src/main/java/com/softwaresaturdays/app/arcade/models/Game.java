package com.softwaresaturdays.app.arcade.models;

import java.util.HashMap;

public class Game {
    private String title;
    private Class cls;
    private HashMap<String, String> top1;
    private HashMap<String, String> top2;
    private HashMap<String, String> top3;

    public Game() {
        this("NO TITLE", null);
    }

    public Game(String title) {
        this(title, null);
    }

    public Game(String title, Class cls) {
        this.title = title;
        this.cls = cls;
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
