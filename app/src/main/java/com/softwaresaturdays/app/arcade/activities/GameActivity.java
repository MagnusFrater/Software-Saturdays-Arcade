package com.softwaresaturdays.app.arcade.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.softwaresaturdays.app.arcade.R;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    public void recordScore(int score, String game) {
        // check and update user's high score for the game

        // check and update game's high score
    }
}
