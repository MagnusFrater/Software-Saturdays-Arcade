package com.softwaresaturdays.app.arcade.models;

public class TurnBasedMultiplayerGame extends Game {

    public enum STATE {
        INIT,
        HOST_TURN,
        JOINER_TURN,
        HOST_WIN,
        JOINER_WIN,
        TIE
    }

    public TurnBasedMultiplayerGame() {
        this("");
    }

    public TurnBasedMultiplayerGame(final String title) {
        super(title);
    }
}
