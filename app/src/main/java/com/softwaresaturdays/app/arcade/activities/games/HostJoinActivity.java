package com.softwaresaturdays.app.arcade.activities.games;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.models.TurnBasedMultiplayerGame;
import com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper;
import com.softwaresaturdays.app.arcade.utilities.Util;

import javax.annotation.Nullable;

import static com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper.KEY_GAMES;
import static com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper.KEY_SESSIONS;
import static com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper.KEY_USERS;

public class HostJoinActivity extends AppCompatActivity {

    private final int maxCodeLength = 5;
    private final String hostCode = Util.generateRandomCode(maxCodeLength);

    private String gameTitle;
    private Class gameClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_join);

        gameTitle = getIntent().getStringExtra("gameTitle");
        gameClass = (Class) getIntent().getSerializableExtra("gameClass");

        // set game title && host code UI
        ((TextView) findViewById(R.id.tvGameNameLabel)).setText(gameTitle);
        ((TextView) findViewById(R.id.tvHostCode)).setText(hostCode);

        final EditText etJoinCode = findViewById(R.id.etJoinCode);
        etJoinCode.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(maxCodeLength) });
        etJoinCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // N/A
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                joinGame(String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // N/A
            }
        });

        // initialize turn based game
        DatabaseHelper.initTurnBasedGame(gameTitle, hostCode);

        initListeners();
    }

    private void initListeners() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // listen for changes in pre-set-up host game
        db.collection(KEY_GAMES).document(gameTitle).collection(KEY_SESSIONS).document(hostCode).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("hostGame", "Listen failed: " + e);
                    return;
                }

                if (documentSnapshot == null || !documentSnapshot.exists()) {
                    Log.e("hostGame", "problem with snapshot");
                    return;
                }

                if (!documentSnapshot.contains("state")) {
                    Log.e("hostGame", "no game state??");
                    return;
                }

                if (!documentSnapshot.get("state").equals(TurnBasedMultiplayerGame.STATE.INIT.name())) {
                    startActivity(new Intent(getApplicationContext(), gameClass));
                }
            }
        });

        // listen for changes in game session saved to profile
        db.collection(KEY_USERS).document(MyApplication.currUser.getUid()).collection(KEY_SESSIONS).document(gameTitle).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("profile", "Listen failed: " + e);
                    return;
                }

                if (documentSnapshot == null || !documentSnapshot.exists()) {
                    Log.e("profile", "problem with snapshot");
                    return;
                }

                if (!documentSnapshot.contains("code")) {
                    Log.e("profile", "no game code??");
                    return;
                }

                if (!documentSnapshot.contains("code")) {
                    return;
                }

                final String savedGameCode = documentSnapshot.getString("code");

                db.collection(KEY_GAMES).document(gameTitle).collection(KEY_SESSIONS).document(savedGameCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot == null || !documentSnapshot.exists()) {
                            return;
                        }

                        if (!documentSnapshot.contains("state")) {
                            return;
                        }

                        final String gameState = documentSnapshot.getString("state");
                        if (!gameState.equals(TurnBasedMultiplayerGame.STATE.INIT.name())) {
                            Log.e("profile", documentSnapshot.get("code") + " " + hostCode);
                            startActivity(new Intent(getApplicationContext(), gameClass));
                        }
                    }
                });
            }
        });
    }

    private void joinGame(final String gameCode) {
        // only attempt joining game if code is of correct length
        if (gameCode.length() != 5) {
            return;
        }

        // can't join your game
        if (gameCode.equals(hostCode)) {
            Snackbar.make(findViewById(R.id.clLayout), "You can't play against yourself!", Snackbar.LENGTH_LONG).show();
            return;
        }

        DatabaseHelper.joinTurnBasedGame(gameTitle, gameCode);
    }
}
