package com.softwaresaturdays.app.arcade.models;

public class TurnBasedMultiplayerGame extends Game {

    public enum STATE {
        INIT,
        HOST_TURN,
        JOINEE_TURN,
        GAME_OVER
    }

    public TurnBasedMultiplayerGame() {
        this("");
    }

    public TurnBasedMultiplayerGame(final String title) {
        super(title);
    }
}
