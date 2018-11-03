package com.softwaresaturdays.app.arcade.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    // All child classes should call this method
    public void recordScore(int score, String game) {
        // check and update user's high mScore for the game
        MyApplication.currUser.checkAndUpdateHighScore(game, score);
        DatabaseHelper.uploadUserInfo(MyApplication.currUser);

        // check and update game's high mScore
        //DatabaseHelper.updateGameHighScore(game, score);
    }
}
