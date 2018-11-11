package com.softwaresaturdays.app.arcade.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.models.Game;
import com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    // All child classes should call this method
    public void recordScore(final double newScore, final String gameTitle) {
        // check and update user's high score for the game
        MyApplication.currUser.checkAndUpdateUserHighScore(gameTitle, newScore);
        DatabaseHelper.uploadUserInfo(MyApplication.currUser);

        // check and update game's high mScore
        // Create a new Score object to upload to the Game's high scores on the database

        // Get the latest high scores from the cloud once (NOT LIVE)
        // Update score if necessary
        DatabaseHelper.getGameHighScores(new DatabaseHelper.onGamesFetchListener() {
            @Override
            public void onGamesFetched(ArrayList<Game> games) {
                for (Game latestGame : games) {
                    try {
                        if (latestGame.getTitle().equals(gameTitle)) {

                            boolean gameNeedsUpdate = false;

                            // Now check if the current score is the highest score
                            if (latestGame.getTop1() < newScore) {
                                // If new score is the highest score, top1 becomes top2 and top2 becomes top3
                                latestGame.setTop3(latestGame.getTop2(), latestGame.getUid2());
                                latestGame.setTop2(latestGame.getTop1(), latestGame.getUid1());
                                latestGame.setTop1(newScore, MyApplication.currUser.getUid());
                                gameNeedsUpdate = true;
                            } else if (latestGame.getTop2() < newScore) {
                                // If new score is the second highest score, top3 becomes top2
                                latestGame.setTop3(latestGame.getTop2(), latestGame.getUid2());
                                latestGame.setTop2(newScore, MyApplication.currUser.getUid());
                                gameNeedsUpdate = true;
                            } else if (latestGame.getTop3() < newScore) {
                                // If new score is the third highest score
                                latestGame.setTop3(newScore, MyApplication.currUser.getUid());
                                gameNeedsUpdate = true;
                            } else {
                                // If the new score is not in the top 3 scores
                                // Do nothing
                            }

                            // if it is a new high score then update the game high scores on the database as well
                            if (gameNeedsUpdate) {
                                DatabaseHelper.updateGameHighScore(latestGame);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        });

    }
}
