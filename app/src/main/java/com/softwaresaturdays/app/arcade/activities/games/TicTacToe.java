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

    private GridLayout glBoard;

    final private int[][] magicSquare = {
            {8, 1, 6},
            {3, 5, 7},
            {4, 9, 2}
    };

    private int[][] board;
    private int turn = 0;

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
        tvCell.setText((turn % 2 == 0)? "X" : "O");

        ((TextView) findViewById(R.id.tvTurn)).setText((turn % 2 == 0)? R.string.your_turn : R.string.opponents_turn);

        turn = ++turn % 2;
    }
}
