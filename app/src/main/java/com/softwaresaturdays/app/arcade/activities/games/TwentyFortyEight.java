package com.softwaresaturdays.app.arcade.activities.games;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.activities.ChatActivity;
import com.softwaresaturdays.app.arcade.activities.GameActivity;
import com.softwaresaturdays.app.arcade.listeners.OnSwipeListener;
import com.softwaresaturdays.app.arcade.utilities.Util;

import java.util.ArrayList;
import java.util.List;

public class TwentyFortyEight extends GameActivity implements View.OnTouchListener {

    private GridLayout glBoard;  // the game board where the we will generate the tiles
    private GestureDetector gestureDetector;  // helps us attach finger swiping motions to functions

    private int[][] board;  // holds the board/game state
    private int mScore;  // player's current score

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twenty_fourty_eight);

        // tells the Activity that we want to listen for finger touch events
        findViewById(R.id.clLayout).setOnTouchListener(this);

        glBoard = findViewById(R.id.glBoard);

        // connect finger gestures to their relative functions
        gestureDetector = new GestureDetector(this, new OnSwipeListener() {

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
        findViewById(R.id.bReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGame();
            }
        });

        // reset for a new game
        resetGame();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    // make it a new game
    private void resetGame() {
        board = new int[4][4];
        birthTile();
        updateScore(0);
        populateBoardView();
    }

    // dynamically translate the board state to the actual on-screen board
    private void populateBoardView() {
        // clear the board of all entities
        glBoard.removeAllViews();

        // for every row/column,
        for (int y=0; y<board.length; y++) {
            for (int x=0; x<board.length; x++) {
                // create a new tile and set all of its stats
                final TextView tvTile = new TextView(this);
                tvTile.setBackgroundColor(getTileBackgroundColor(board[y][x])); // hex color 0xAARRGGBB

                final int size = (int) Util.convertDpToPixel(80, this);
                final int margin = (int) Util.convertDpToPixel(8, this);
                final ActionBar.LayoutParams params = new ActionBar.LayoutParams(size, size);
                params.setMargins(
                        (x == 0) ? margin : 0,
                        (y == 0) ? margin : 0,
                        margin,
                        margin
                );
                tvTile.setLayoutParams(params);

                tvTile.setTextSize(35);
                tvTile.setGravity(Gravity.CENTER);
                tvTile.setTypeface(null, Typeface.BOLD);
                tvTile.setTextColor(getTileTextColor(board[y][x]));
                tvTile.setText(((board[y][x] > 0) ? String.valueOf(board[y][x]) : ""));  // set the new tile's value to a power of two, or keep it blank

                // push it to the fresh game board
                glBoard.addView(tvTile);
            }
        }
    }

    // gives the correct tile color based on the tile's value
    private int getTileBackgroundColor(final int tileValue) {
        switch (tileValue) {
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

    // returns the correct tile text color based on the tile's value
    private int getTileTextColor(final int tileValue) {
        switch (tileValue) {
            case 2:
            case 4:
                return 0xff776e65;
            default:
                return 0xffffffff;
        }
    }

    private void swipeUp() {
        if (moveUp()) {
            birthTile();
            checkGameOver();
        }
        populateBoardView();
    }

    private void swipeDown() {
        if (moveDown()) {
            birthTile();
            checkGameOver();
        }
        populateBoardView();
    }

    private void swipeLeft() {
        if (moveLeft()) {
            birthTile();
            checkGameOver();
        }
        populateBoardView();
    }

    private void swipeRight() {
        if (moveRight()) {
            birthTile();
            checkGameOver();
        }
        populateBoardView();
    }

    // moves a tile to a different location on the game board
    // return true if move was made, false otherwise
    public boolean moveTo(int fromRow, int fromCol, int toRow, int toCol) {
        // always check if coordinates are within the bounds of the game board
        if (isCoordinateOutOfBounds(fromRow, fromCol) || isCoordinateOutOfBounds(toRow, toCol)) {
            return false;
        }

        // grab the values of the tile to move and the tile to move to
        final int from = board[fromRow][fromCol];
        final int to = board[toRow][toCol];

        // if the current tile doesn't exist, don't move it
        if (from == 0) {
            return false;
        }

        // move the tile only if the goal tile is empty or is the same value as the tile to move
        if (to == 0 || to == from) {
            // move the tile
            board[toRow][toCol] = from + to;
            board[fromRow][fromCol] = 0;

            // upon a tile merge, increase the score
            if (to == from) {
                updateScore(board[toRow][toCol]);
            }

            return true;
        }

        return false;
    }

    // move all tiles upwards as far as they can go
    public boolean moveUp() {
        int cycles = 0;
        boolean madeMove = true;
        while (madeMove) {
            madeMove = false;

            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board.length; y++) {
                    madeMove = moveTo(x, y, x - 1, y) || madeMove;
                }
            }

            cycles++;
        }

        return cycles > 1;
    }

    // move all tiles downwards as far as they can go
    public boolean moveDown() {
        int cycles = 0;
        boolean madeMove = true;
        while (madeMove) {
            madeMove = false;

            for (int x = board.length - 1; x >= 0; x--) {
                for (int y = 0; y < board.length; y++) {
                    madeMove = moveTo(x, y, x + 1, y) || madeMove;
                }
            }

            cycles++;
        }

        return cycles > 1;
    }

    // move all tiles leftwards as far as they can go
    public boolean moveLeft() {
        int cycles = 0;
        boolean madeMove = true;
        while (madeMove) {
            madeMove = false;

            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board.length; y++) {
                    madeMove = moveTo(x, y, x, y - 1) || madeMove;
                }
            }

            cycles++;
        }

        return cycles > 1;
    }

    // move all tiles rightwards as far as they can go
    public boolean moveRight() {
        int cycles = 0;
        boolean madeMove = true;
        while (madeMove) {
            madeMove = false;

            for (int x = 0; x < board.length; x++) {
                for (int y = board.length - 1; y >= 0; y--) {
                    madeMove = moveTo(x, y, x, y + 1) || madeMove;
                }
            }

            cycles++;
        }

        return cycles > 1;
    }

    // returns a list of tiles that are blank
    // (x, y) translated to y * `board length` + x
    // ie. (0, 0) => 0 , (1, 0) => 1 , (0, 1) => 3 , (1, 1) => 4 , ... , (2, 2) => 8
    private List<Integer> getUnusedTiles() {
        final List<Integer> unusedTiles = new ArrayList<>();
        for (int y=0; y<board.length; y++) {
            for (int x=0; x<board.length; x++) {
                if (board[y][x] == 0) {
                    unusedTiles.add(y*board.length + x);
                }
            }
        }

        return unusedTiles;
    }

    // randomly fill a single blank tile with either a 2 or a 4
    private void birthTile() {
        final List<Integer> unusedTiles = getUnusedTiles();

        // make sure there are actually some unused tiles
        if (unusedTiles.size() == 0) return;

        // pick random unused tile to birth
        final int unusedTile = unusedTiles.get(Util.getRandInt(0, unusedTiles.size()-1));
        final int y = unusedTile / board.length;
        final int x = unusedTile % board.length;
        board[y][x] = (Util.getRandInt(0,3) == 0)? 4 : 2;
    }

    // return true if coordinate is within the bounds of the game board, false otherwise
    private boolean isCoordinateOutOfBounds(final int y, final int x) {
        return y < 0 || y > board.length - 1 || x < 0 || x > board.length - 1;
    }

    // add new points to total, however reset it if the new points are equal to 0
    private void updateScore(final int points) {
        if (points == 0) {
            mScore = 0;
        } else {
            mScore += points;
        }

        ((TextView) findViewById(R.id.tvScore)).setText(String.valueOf(mScore));
    }

    // if there are no more blank tiles, and if no tile is surrounded by a similar value, game is over
    private void checkGameOver() {
        if (getUnusedTiles().size() > 0) {
            return;
        }

        for (int y=0; y<board.length; y++) {
            for (int x=0; x<board.length; x++) {
                if (similarNeighbour(y, x)) {
                    return;
                }
            }
        }

        gameOver();
    }

    // returns true if above/below/left/right neighbour has same value
    private boolean similarNeighbour(final int y, final int x) {
        final int[] deltaY = {-1, 1, 0, 0};
        final int[] deltaX = {0, 0, -1, 1};

        for (int i = 0; i < deltaY.length; i++) {
            final int neighbourY = y + deltaY[i];
            final int neighbourX = x + deltaX[i];

            if (isCoordinateOutOfBounds(neighbourY, neighbourX)) {
                continue;
            }

            if (board[neighbourY][neighbourX] == board[y][x]) {
                return true;
            }
        }

        return false;
    }

    // create the popup dialog to allow the player to restart the game or go back to the chat
    private void gameOver() {
        // Record the score - Calling superclass method
        recordScore(mScore, "2048");

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("GAME OVER");

        alert.setCancelable(false);

        alert.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                resetGame();
            }
        });

        alert.setNegativeButton("Back to chat", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                finish();
            }
        });

        alert.show();
    }
}

