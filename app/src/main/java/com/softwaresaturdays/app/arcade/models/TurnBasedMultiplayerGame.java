package com.softwaresaturdays.app.arcade.models;

import android.content.Context;
import android.content.Intent;

import com.softwaresaturdays.app.arcade.activities.games.HostJoinActivity;

public class TurnBasedMultiplayerGame extends Game {

    public enum STATE {
        INIT,
        HOST_TURN,
        JOINER_TURN,
        GAME_OVER
    }

    public TurnBasedMultiplayerGame() {
        this("");
    }

    public TurnBasedMultiplayerGame(final String title) {
        super(title);
    }
}
