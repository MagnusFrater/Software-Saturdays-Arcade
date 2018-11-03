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
    final int boardSize = 3;

    int turn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        glBoard = findViewById(R.id.glBoard);

        populateBoardView();
    }

    private void populateBoardView() {
        glBoard.removeAllViews();

        for (int y=0; y<boardSize; y++) {
            for (int x=0; x<boardSize; x++) {
                final TextView tvCell = new TextView(this);
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
                        final TextView tv = (TextView) v;

                        if (!tv.getText().equals("")) {
                            return;
                        }

                        if (turn % 2 == 0) {
                            tv.setText("X");
                        } else {
                            tv.setText("O");
                        }

                        turn++;
                    }
                });

                glBoard.addView(tvCell);
            }
        }
    }
}
