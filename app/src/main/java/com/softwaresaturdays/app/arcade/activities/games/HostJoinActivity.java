package com.softwaresaturdays.app.arcade.activities.games;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.utilities.Util;

public class HostJoinActivity extends AppCompatActivity {

    private final int maxCodeLength = 5;
    private final String hostCode = Util.generateRandomCode(maxCodeLength);

    private String game_title;
    private Class game_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_join);

        game_title = getIntent().getStringExtra("game_title");
        game_class = (Class) getIntent().getSerializableExtra("game_class");

        // set game title && host code UI
        ((TextView) findViewById(R.id.tvGameNameLabel)).setText(game_title);
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

        startActivity(new Intent(getApplicationContext(), game_class));
    }
}
