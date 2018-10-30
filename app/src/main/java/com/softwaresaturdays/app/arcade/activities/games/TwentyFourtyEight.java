package com.softwaresaturdays.app.arcade.activities.games;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
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

    private int[][] board;
    private final int boardSize = 4;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twenty_fourty_eight);

        findViewById(R.id.clLayout).setOnTouchListener(this);

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

        initializeBoard();
        populateBoardView();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private void initializeBoard() {
        board = new int[boardSize][boardSize];
        birthCell();
    }

    private void populateBoardView() {
        glBoard.removeAllViews();

        for (int y=0; y<boardSize; y++) {
            for (int x=0; x<boardSize; x++) {
                final TextView tvCell = new TextView(this);
                tvCell.setBackgroundColor(getCellBackgroundColor(board[y][x])); // hex color 0xAARRGGBB

                final int size = (int)Util.convertDpToPixel(80, this);
                final int margin = (int)Util.convertDpToPixel(8, this);
                final ActionBar.LayoutParams params = new ActionBar.LayoutParams(size, size);
                params.setMargins(
                        (x == 0)? margin : 0,
                        (y == 0)? margin : 0,
                        margin,
                        margin
                );
                tvCell.setLayoutParams(params);

                tvCell.setTextSize(35);
                tvCell.setGravity(Gravity.CENTER);
                tvCell.setTypeface(null, Typeface.BOLD);
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
        if (moveUp()){
            birthCell();
        }
        populateBoardView();
    }

    private void swipeDown() {
        if (moveDown()) {
            birthCell();
        }
        populateBoardView();
    }

    private void swipeLeft() {
        if (moveLeft()) {
            birthCell();
        }
        populateBoardView();
    }

    private void swipeRight() {
        if (moveRight()) {
            birthCell();
        }
        populateBoardView();
    }

    public boolean moveTo(int fromRow, int fromCol, int toRow, int toCol) {
        if (isCoordinateOutOfBounds(fromRow, fromCol) || isCoordinateOutOfBounds(toRow, toCol)) {
            return false;
        }

        final int from = board[fromRow][fromCol];
        final int to = board[toRow][toCol];

        if (from == 0) {
            return false;
        }

        if (to == 0 || to == from) {
            board[toRow][toCol] = from + to;
            board[fromRow][fromCol] = 0;
            return true;
        }

        return false;
    }

    public boolean moveUp(){
        int cycles = 0;
        boolean madeMove = true;
        while (madeMove) {
            madeMove = false;

            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    madeMove = moveTo(x, y, x - 1, y) || madeMove;
                }
            }

            cycles++;
        }

        return cycles > 1;
    }

    public boolean moveDown() {
        int cycles = 0;
        boolean madeMove = true;
        while (madeMove) {
            madeMove = false;

            for (int x = boardSize - 1; x >= 0; x--) {
                for (int y = 0; y < boardSize; y++) {
                    madeMove = moveTo(x, y, x + 1, y) || madeMove;
                }
            }

            cycles++;
        }

        return cycles > 1;
    }

    public boolean moveRight() {
        int cycles = 0;
        boolean madeMove = true;
        while (madeMove) {
            madeMove = false;

            for (int x = 0; x < boardSize; x++) {
                for (int y = boardSize - 1; y >= 0; y--) {
                    madeMove = moveTo(x, y, x, y + 1) || madeMove;
                }
            }

            cycles++;
        }

        return cycles > 1;
    }

    public boolean moveLeft() {
        int cycles = 0;
        boolean madeMove = true;
        while (madeMove) {
            madeMove = false;

            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    madeMove = moveTo(x, y, x, y - 1) || madeMove;
                }
            }

            cycles++;
        }

        return cycles > 1;
    }

    private List<Integer> getUnusedCells() {
        final List<Integer> unusedCells = new ArrayList<>();
        for (int y=0; y<boardSize; y++) {
            for (int x=0; x<boardSize; x++) {
                if (board[y][x] == 0) {
                    unusedCells.add(y*boardSize + x);
                }
            }
        }

        return unusedCells;
    }

    private void birthCell() {
        final List<Integer> unusedCells = getUnusedCells();

        // make sure there are actually some unused cells
        if (unusedCells.size() == 0) return;

        // pick random unused cell to birth
        final int unusedCell = unusedCells.get(Util.getRandInt(0, unusedCells.size()-1));
        final int y = unusedCell / boardSize;
        final int x = unusedCell % boardSize;
        board[y][x] = (Util.getRandInt(0,3) == 0)? 4 : 2;
    }

    private boolean isCoordinateOutOfBounds(final int y, final int x) {
        return y < 0 || y > boardSize - 1 || x < 0 || x > boardSize - 1;
    }
}

