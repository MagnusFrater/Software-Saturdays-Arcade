package com.softwaresaturdays.app.arcade.activities.games;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayout;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.activities.GameActivity;
import com.softwaresaturdays.app.arcade.listeners.OnSwipeListener;
import com.softwaresaturdays.app.arcade.utilities.Util;

import java.util.ArrayList;
import java.util.List;

public class TwentyFourtyEight extends GameActivity implements View.OnTouchListener {

    private GridLayout glBoard;
    private GestureDetector gestureDetector;

    // 2048
    int[][] board;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twenty_fourty_eight);

        // views
        ConstraintLayout clLayout = findViewById(R.id.clLayout);
        glBoard = findViewById(R.id.glBoard);

        gestureDetector = new GestureDetector(this,new OnSwipeListener(){

            @Override
            public boolean onSwipe(Direction direction) {
                if (direction == Direction.up) {
                    swipeUp();
                }

                if (direction == Direction.down) {
                    swipeDown();
                }

                if (direction == Direction.left) {
                    swipeLeft();
                }

                if (direction == Direction.right) {
                    swipeRight();
                }

                return true;
            }


        });

        clLayout.setOnTouchListener(this);

        initializeBoard();
        populateBoardView();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private void initializeBoard() {
        board = new int[4][4];
        birthCell();
    }

    private void populateBoardView() {
        glBoard.removeAllViews();

        for (int y=0; y<4; y++) {
            for (int x=0; x<4; x++) {
                final TextView tvCell = new TextView(this);
                tvCell.setLayoutParams(new ActionBar.LayoutParams(
                        (int)Util.convertDpToPixel(90, this),
                        (int)Util.convertDpToPixel(90, this)
                ));
                tvCell.setTextSize(35);
                tvCell.setGravity(Gravity.CENTER);
                tvCell.setTypeface(null, Typeface.BOLD);
                tvCell.setBackgroundColor(getCellBackgroundColor(board[y][x])); // hex color 0xAARRGGBB
                tvCell.setTextColor(getCellTextColor(board[y][x]));
                tvCell.setText(((board[y][x] > 0)? String.valueOf(board[y][x]) : ""));
                glBoard.addView(tvCell);
            }
        }
    }

    private int getCellBackgroundColor(final int cellValue) {
        switch (cellValue) {
            case 2:
                return 0xffece2d8;
            case 4:
                return 0xffede0c9;
            case 8:
                return 0xfff0b07d;
            case 16:
                return 0xffea8d5a;
            case 32:
                return 0xfff47c63;
            case 64:
                return 0xfff45e43;
            case 128:
                return 0xffeccd77;
            case 256:
                return 0xffeccb6b;
            case 512:
                return 0xffebc75a;
            case 1028:
                return 0xffe28913;
            case 2048:
                return 0xffeec22e;
            case 4096:
                return 0xff5eda92;
            default:
                return 0xffdbcfc3;
        }
    }

    private int getCellTextColor(final int cellValue) {
        switch (cellValue) {
            case 2:
            case 4:
                return 0xff776e65;
            default:
                return 0xffffffff;
        }
    }

    private void swipeUp() {
        birthCell();
        populateBoardView();
    }

    private void swipeDown() {
        birthCell();
        populateBoardView();
    }

    private void swipeLeft() {
        birthCell();
        populateBoardView();
    }

    private void swipeRight() {
        birthCell();
        populateBoardView();
    }

    private void birthCell() {
        // find all unused cells
        final List<Integer> unusedCells = new ArrayList<>();
        for (int y=0; y<4; y++) {
            for (int x=0; x<4; x++) {
                if (board[y][x] == 0) {
                    unusedCells.add(y*4 + x);
                }
            }
        }

        // make sure there are actually some unused cells
        if (unusedCells.size() == 0) return;

        // pick random unused cell to birth
        final int unusedCell = unusedCells.get(Util.getRandInt(0, unusedCells.size()-1));
        final int y = unusedCell / 4;
        final int x = unusedCell % 4;
        board[y][x] = (Util.getRandInt(0,3) == 0)? 4 : 2;
    }
}

