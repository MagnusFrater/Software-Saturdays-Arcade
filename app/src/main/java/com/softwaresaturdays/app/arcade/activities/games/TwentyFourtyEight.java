package com.softwaresaturdays.app.arcade.activities.games;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.activities.GameActivity;
import com.softwaresaturdays.app.arcade.listeners.OnSwipeListener;

public class TwentyFourtyEight extends GameActivity implements View.OnTouchListener {

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twenty_fourty_eight);

        gestureDetector = new GestureDetector(this,new OnSwipeListener(){

            @Override
            public boolean onSwipe(Direction direction) {
                if (direction == Direction.up){
                    Snackbar.make(findViewById(R.id.layout), "onSwipe: up", Snackbar.LENGTH_SHORT).show();
                }

                if (direction == Direction.down){
                    Snackbar.make(findViewById(R.id.layout), "onSwipe: down", Snackbar.LENGTH_SHORT).show();
                }

                if (direction == Direction.left){
                    Snackbar.make(findViewById(R.id.layout), "onSwipe: left", Snackbar.LENGTH_SHORT).show();
                }

                if (direction == Direction.right){
                    Snackbar.make(findViewById(R.id.layout), "onSwipe: right", Snackbar.LENGTH_SHORT).show();
                }

                return true;
            }


        });

        findViewById(R.id.layout).setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }
}

