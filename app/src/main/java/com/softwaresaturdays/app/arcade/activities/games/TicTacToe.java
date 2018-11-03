package com.softwaresaturdays.app.arcade.activities.games;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.utilities.Util;

public class TicTacToe extends AppCompatActivity {

    private enum STATE {
        YOU, OPPONENT, GAME_OVER
    }

    private final String[] symbols = {"X", "O"};
    private final int[][] magicSquare = {
            {8, 1, 6},
            {3, 5, 7},
            {4, 9, 2}
    };

    private GridLayout glBoard;

    private int[][] board;
    private STATE state;
    private int yourSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        glBoard = findViewById(R.id.glBoard);

        resetGame();
    }

    private void resetGame() {
        board = new int[3][3];
        populateBoardView();

        state = STATE.YOU;
        yourSymbol = 0;
    }

    private void populateBoardView() {
        glBoard.removeAllViews();

        for (int i=0; i<Math.pow(board.length, 2); i++) {
            final TextView tvCell = new TextView(this);
            tvCell.setTag(i);
            tvCell.setBackgroundColor(0xffffffff);

            final int size = (int)Util.convertDpToPixel(120, this);
            final int margin = (int)Util.convertDpToPixel(8, this);
            final ActionBar.LayoutParams params = new ActionBar.LayoutParams(size, size);
            params.setMargins(
                    0,
                    0,
                    margin,
                    margin
            );
            tvCell.setLayoutParams(params);

            tvCell.setTextSize(35);
            tvCell.setGravity(Gravity.CENTER);
            tvCell.setTypeface(null, Typeface.BOLD);
            tvCell.setTextColor(0xff000000);

            tvCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (state == STATE.GAME_OVER) {
                        return;
                    }

                    final TextView tvCell = (TextView) v;
                    if (!tvCell.getText().equals("")) {
                        return;
                    }

                    takeTurn(tvCell);
                }
            });

            glBoard.addView(tvCell);
        }
    }

    private void takeTurn(final TextView tvCell) {
        tvCell.setText((state == STATE.YOU)? symbols[yourSymbol] : symbols[(yourSymbol + 1) % 2]);

        // update game board
        final int cellTag = (int) tvCell.getTag();
        final int y = cellTag / board.length;
        final int x = cellTag % board.length;
        board[y][x] = (state == STATE.YOU)? 1 : -1;

        // check if game over
        checkGameOver(y, x);

        // switch turn if game not over
        if (state != STATE.GAME_OVER) {
            state = (state == STATE.YOU)? STATE.OPPONENT : STATE.YOU;
            ((TextView) findViewById(R.id.tvTurn)).setText((state == STATE.YOU)? R.string.your_turn : R.string.opponents_turn);
        }
    }

    private void checkGameOver(final int y, final int x) {
        int sum;

        // check column
        sum = 0;
        for (int i=0; i<board.length; i++) {
            sum += board[i][x] * magicSquare[i][x];
        }
        if (Math.abs(sum) == 15) gameOver(false);

        // check row
        sum = 0;
        for (int i=0; i<board.length; i++) {
            sum += board[y][i] * magicSquare[y][i];
        }
        if (Math.abs(sum) == 15) gameOver(false);

        // check diagonal
        if (y == x) {
            sum = 0;
            for (int i=0; i<board.length; i++) {
                sum += board[i][i] * magicSquare[i][i];
            }
            if (Math.abs(sum) == 15) gameOver(false);
        }

        // check anti-diagonal
        if (y + x == board.length - 1) {
            sum = 0;
            for (int i=0; i<board.length; i++) {
                sum += board[i][board.length - 1 - i] * magicSquare[i][board.length - 1 - i];
            }
            if (Math.abs(sum) == 15) gameOver(false);
        }

        // check tie
        for (int yi=0; yi<board.length; yi++) {
            for (int xi=0; xi<board.length; xi++) {
                if (board[yi][xi] == 0) {
                    return;
                }
            }
        }
        gameOver(true);
    }

    private void gameOver(final boolean tie) {
        if (tie) {
            ((TextView) findViewById(R.id.tvTurn)).setText(R.string.tie);
        } else {
            ((TextView) findViewById(R.id.tvTurn)).setText((state == STATE.YOU)? R.string.win : R.string.lose);
        }

        state = STATE.GAME_OVER;
    }
}
