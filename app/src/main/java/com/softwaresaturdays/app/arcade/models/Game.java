package com.softwaresaturdays.app.arcade.models;

import android.app.Activity;
import android.content.Intent;

import com.softwaresaturdays.app.arcade.activities.games.HostJoinActivity;

import java.util.HashMap;

public class Game {

    public enum GAME_TYPE {
        local, turn_based_multiplayer
    }

    private String title;
    private Class cls;
    private GAME_TYPE gameType;

    private HashMap<String, String> top1;
    private HashMap<String, String> top2;
    private HashMap<String, String> top3;

    public Game() {
        this("NO TITLE", null, GAME_TYPE.local);
    }

    public Game(String title) {
        this(title, null, GAME_TYPE.local);
    }

    public Game(final String title, final Class cls, final GAME_TYPE gameType) {
        this.title = title;
        this.cls = cls;
        this.gameType = gameType;
    }

    public void startGame(final Activity activity) {
        if (gameType == GAME_TYPE.turn_based_multiplayer) {
            final Intent intent = new Intent(activity.getApplicationContext(), HostJoinActivity.class);
            intent.putExtra("game_title", title);
            intent.putExtra("game_class", cls);
            activity.startActivity(intent);
        } else {
            activity.startActivity(new Intent(activity.getApplicationContext(), cls));
        }
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
