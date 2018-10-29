package com.softwaresaturdays.app.arcade.activities.games;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.activities.GameActivity;
import com.softwaresaturdays.app.arcade.listeners.OnSwipeListener;

public class TwentyFourtyEight extends GameActivity implements View.OnTouchListener {

    private ConstraintLayout clLayout;
    private GridLayout glBoard;

    private GestureDetector gestureDetector;

    int[][] board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twenty_fourty_eight);

        clLayout = findViewById(R.id.clLayout);
        glBoard = findViewById(R.id.glBoard);

        gestureDetector = new GestureDetector(this,new OnSwipeListener(){

            @Override
            public boolean onSwipe(Direction direction) {
                if (direction == Direction.up){
                    Snackbar.make(clLayout, "onSwipe: up", Snackbar.LENGTH_SHORT).show();
                }

                if (direction == Direction.down){
                    Snackbar.make(clLayout, "onSwipe: down", Snackbar.LENGTH_SHORT).show();
                }

                if (direction == Direction.left){
                    Snackbar.make(clLayout, "onSwipe: left", Snackbar.LENGTH_SHORT).show();
                }

                if (direction == Direction.right){
                    Snackbar.make(clLayout, "onSwipe: right", Snackbar.LENGTH_SHORT).show();
                }

                return true;
            }


        });

        clLayout.setOnTouchListener(this);

        board = new int[4][4];
        populateBoard();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private void populateBoard() {
        glBoard.removeAllViews();

        for (int y=0; y<4; y++) {
            for (int x=0; x<4; x++) {
                final TextView tvPiece = new TextView(this);
                tvPiece.setLayoutParams(new ActionBar.LayoutParams((int)convertDpToPixel(90, this), (int)convertDpToPixel(90, this)));
                tvPiece.setText(board[y][x] + "");
                tvPiece.setGravity(Gravity.CENTER);
                tvPiece.setBackgroundColor(0xffece2d8); // hex color 0xAARRGGBB
                glBoard.addView(tvPiece);
            }
        }

        Snackbar.make(clLayout, glBoard.getChildCount() + "", Snackbar.LENGTH_SHORT).show();
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}

