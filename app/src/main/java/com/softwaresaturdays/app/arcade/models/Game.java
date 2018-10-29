package com.softwaresaturdays.app.arcade.models;

public class Game {
    private String title;
    private Class cls;

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
}
