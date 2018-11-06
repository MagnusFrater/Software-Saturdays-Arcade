package com.softwaresaturdays.app.arcade.models;

import android.app.Activity;
import android.content.Intent;

import com.softwaresaturdays.app.arcade.activities.games.HostJoinActivity;

public class TurnBasedMultiplayerGame extends Game {

    public enum STATE {
        INIT,
        HOST_TURN,
        JOINEE_TURN,
        GAME_OVER
    }

    public TurnBasedMultiplayerGame() {
        super();
    }

    public TurnBasedMultiplayerGame(final String title) {
        super(title);
    }

    public TurnBasedMultiplayerGame(final String title, final Class cls) {
        super(title, cls);
    }

    @Override
    public void startGame(final Activity activity) {
        final Intent intent = new Intent(activity.getApplicationContext(), HostJoinActivity.class);
        intent.putExtra("game_title", title);
        intent.putExtra("game_class", cls);
        activity.startActivity(intent);
    }
}
