package com.softwaresaturdays.app.arcade.activities.games;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.models.TurnBasedMultiplayerGame;
import com.softwaresaturdays.app.arcade.utilities.Util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper.KEY_GAMES;
import static com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper.KEY_SESSIONS;
import static com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper.KEY_USERS;

public class TicTacToe extends AppCompatActivity {

    private final String gameTitle = "Tic Tac Toe";
    private final String[] symbols = {"X", "O"};  //  host , joiner
    private final int[][] magicSquare = {
            {8, 1, 6},
            {3, 5, 7},
            {4, 9, 2}
    };

    private GridLayout glBoard;
    private String hostCode;
    private boolean isHost;
    private int[][] board;
    private TurnBasedMultiplayerGame.STATE state;
    private int yourSymbol;
    private boolean yourTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        glBoard = findViewById(R.id.glBoard);
        hostCode = getIntent().getStringExtra("hostCode");
        board = new int[3][3];

        initListeners();
        populateBoardView();
    }

    private void initListeners() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // listen for changes to the game state
        db.collection(KEY_GAMES).document(gameTitle).collection(KEY_SESSIONS).document(hostCode).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(gameTitle, e.getMessage());
                    return;
                }

                // initialize game state if it hasn't happened yet
                if (!documentSnapshot.contains("board")) {
                    getHostStatus();
                    return;
                }

                // update board, state
                setBoard(documentSnapshot.getString("board"));
                setState(documentSnapshot.getString("state"));
            }
        });
    }

    private void getHostStatus() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // retrieves data to tell who is hosting the game
        db.collection(KEY_USERS).document(MyApplication.currUser.getUid()).collection(KEY_SESSIONS).document(gameTitle).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // no old game to delete, create new one
                if (!documentSnapshot.contains("host")) {
                    Log.e(gameTitle, "problem fetching isHost status");
                    return;
                }

                isHost = documentSnapshot.getBoolean("host");
                yourSymbol = (isHost)? 0 : 1;

                // once the host knows they're the host, initialize the board
                if (isHost) {
                    final Map<String, Object> update = new HashMap<>();
                    update.put("board", stringifyBoard());
                    db.collection(KEY_GAMES).document(gameTitle).collection(KEY_SESSIONS).document(hostCode).set(update, SetOptions.merge());
                }
            }
        });
    }

    private void populateBoardView() {
        glBoard.removeAllViews();

        for (int i=0; i<Math.pow(board.length, 2); i++) {
            final TextView tvCell = new TextView(this);
            tvCell.setTag(i);
            tvCell.setBackgroundColor(0xffffffff);

            final int size = (int)Util.convertDpToPixel(110, this);
            final int margin = (int)Util.convertDpToPixel(6, this);
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

            final int y = i / board.length;
            final int x = i % board.length;
            if (board[y][x] == 1) {
                tvCell.setText(symbols[0]);
            } else if (board[y][x] == -1) {
                tvCell.setText(symbols[1]);
            }

            tvCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // make sure it is your turn
                    if (!yourTurn) {
                        return;
                    }

                    // make sure the cell isn't taken
                    final TextView tvCell = (TextView) v;
                    if (!tvCell.getText().equals("")) {
                        return;
                    }

                    // complete successful turn
                    takeTurn(tvCell);
                }
            });

            glBoard.addView(tvCell);
        }
    }

    private void takeTurn(final TextView tvCell) {
        tvCell.setText(symbols[yourSymbol]);

        // update game board
        final int cellTag = (int) tvCell.getTag();
        final int y = cellTag / board.length;
        final int x = cellTag % board.length;
        board[y][x] = (isHost)? 1 : -1;

        // check if game over
        if (!checkGameOver(y, x)) {
            // switch turn
            state = (state == TurnBasedMultiplayerGame.STATE.HOST_TURN)? TurnBasedMultiplayerGame.STATE.JOINER_TURN : TurnBasedMultiplayerGame.STATE.HOST_TURN;

            // update cloud game session
            final Map<String, Object> update = new HashMap<>();
            update.put("board", stringifyBoard());
            update.put("state", state.name());
            FirebaseFirestore.getInstance().collection(KEY_GAMES).document(gameTitle).collection(KEY_SESSIONS).document(hostCode).set(update, SetOptions.merge());
        }
    }

    private boolean checkGameOver(final int y, final int x) {
        int sum;

        // check column
        sum = 0;
        for (int i=0; i<board.length; i++) {
            sum += board[i][x] * magicSquare[i][x];
        }
        if (Math.abs(sum) == 15) {
            gameOver(false);
            return true;
        }

        // check row
        sum = 0;
        for (int i=0; i<board.length; i++) {
            sum += board[y][i] * magicSquare[y][i];
        }
        if (Math.abs(sum) == 15) {
            gameOver(false);
            return true;
        }

        // check diagonal
        if (y == x) {
            sum = 0;
            for (int i=0; i<board.length; i++) {
                sum += board[i][i] * magicSquare[i][i];
            }
            if (Math.abs(sum) == 15) {
                gameOver(false);
                return true;
            }
        }

        // check anti-diagonal
        if (y + x == board.length - 1) {
            sum = 0;
            for (int i=0; i<board.length; i++) {
                sum += board[i][board.length - 1 - i] * magicSquare[i][board.length - 1 - i];
            }
            if (Math.abs(sum) == 15) {
                gameOver(false);
                return true;
            }
        }

        // check tie
        for (int yi=0; yi<board.length; yi++) {
            for (int xi=0; xi<board.length; xi++) {
                if (board[yi][xi] == 0) {
                    return false;
                }
            }
        }
        gameOver(true);
        return true;
    }

    private void gameOver(final boolean tie) {
        if (tie) {
            state = TurnBasedMultiplayerGame.STATE.TIE;
        } else {
            if (isHost) {
                state = TurnBasedMultiplayerGame.STATE.HOST_WIN;
            } else {
                state = TurnBasedMultiplayerGame.STATE.JOINER_WIN;
            }
        }

        final Map<String, Object> update = new HashMap<>();
        update.put("state", state.name());
        FirebaseFirestore.getInstance().collection(KEY_GAMES).document(gameTitle).collection(KEY_SESSIONS).document(hostCode).set(update, SetOptions.merge());
    }

    private String stringifyBoard() {
        String s = "";

        for (int y=0; y<board.length; y++) {
            for (int x=0; x<board.length; x++) {
                s += board[y][x] + ",";
            }
        }

        return s.substring(0, s.length() - 1);
    }

    private void setBoard(final String boardState) {
        final String[] split = boardState.split(",");
        for (int i=0; i<split.length; i++) {
            int y = i / board.length;
            int x = i % board.length;
            board[y][x] = Integer.parseInt(split[i]);
        }

        populateBoardView();
    }

    private void setState(final String newState) {
        state = TurnBasedMultiplayerGame.STATE.valueOf(newState);

        switch (state) {
            case HOST_TURN:
                if (isHost) {
                    yourTurn = true;
                    ((TextView) findViewById(R.id.tvTurn)).setText(R.string.your_turn);
                } else {
                    yourTurn = false;
                    ((TextView) findViewById(R.id.tvTurn)).setText(R.string.opponents_turn);
                }
                break;
            case JOINER_TURN:
                if (isHost) {
                    yourTurn = false;
                    ((TextView) findViewById(R.id.tvTurn)).setText(R.string.opponents_turn);
                } else {
                    yourTurn = true;
                    ((TextView) findViewById(R.id.tvTurn)).setText(R.string.your_turn);
                }
                break;
            case HOST_WIN:
                if (isHost) {
                    ((TextView) findViewById(R.id.tvTurn)).setText(R.string.win);
                } else {
                    ((TextView) findViewById(R.id.tvTurn)).setText(R.string.lose);
                }
                break;
            case JOINER_WIN:
                if (isHost) {
                    ((TextView) findViewById(R.id.tvTurn)).setText(R.string.lose);
                } else {
                    ((TextView) findViewById(R.id.tvTurn)).setText(R.string.win);
                }
            case TIE:
                ((TextView) findViewById(R.id.tvTurn)).setText(R.string.tie);
            default:
                ((TextView) findViewById(R.id.tvTurn)).setText(R.string.error);
        }
    }
}
