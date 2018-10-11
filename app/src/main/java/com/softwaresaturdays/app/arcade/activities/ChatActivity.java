package com.softwaresaturdays.app.arcade.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.adapters.ChatAdapter;
import com.softwaresaturdays.app.arcade.adapters.GameAdapter;
import com.softwaresaturdays.app.arcade.models.Game;
import com.softwaresaturdays.app.arcade.models.Message;
import com.softwaresaturdays.app.arcade.models.TextMessage;
import com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRvChat;
    private RecyclerView mRvGames;
    private ArrayList<Message> mMessages = new ArrayList<>();
    private ArrayList<Game> mGames = new ArrayList<>();
    private ChatAdapter mChatAdapter;
    private GameAdapter mGameAdapter;
    private RelativeLayout mRlGameInfo;
    private TextView mTvLeaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        DatabaseHelper.fetchMessages(new DatabaseHelper.OnDatabaseFetchListener() {
            @Override
            public void onMessagesFetched(ArrayList<Message> messages) {
                // Update messages REAL-TIME and update list view
                mMessages = messages;
                mChatAdapter = new ChatAdapter(ChatActivity.this, mMessages, new ChatAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Message message) {
                        // Clicked on message
                    }
                });
                if (mRvChat != null) {
                    mRvChat.setAdapter(mChatAdapter);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mRvChat = findViewById(R.id.rvChat);
        mRvGames = findViewById(R.id.rvGames);
        mRlGameInfo = findViewById(R.id.rlGameInfo);
        mTvLeaderboard = findViewById(R.id.tvLeaderboard);
        ImageView ivSend = findViewById(R.id.ivSend);
        RoundedImageView ivProfile = findViewById(R.id.ivProfile);
        final EditText etTextMessage = findViewById(R.id.etTextMessage);

        assert MyApplication.currUser != null;

        // Load profile pic using Picasso
        Picasso.get().load(MyApplication.currUser.getPhotoUrl()).into(ivProfile);

        // Game info only visible when clicked on a game
        mRlGameInfo.setVisibility(View.GONE);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etTextMessage.getText().toString();
                if (text.isEmpty()) {
                    Snackbar.make(mRvChat, "Please enter text", Snackbar.LENGTH_SHORT).show();
                } else {
                    // Create new text message
                    Message message = new TextMessage(text, MyApplication.currUser.getUid());
                    // Upload message to database
                    DatabaseHelper.uploadMessage(message);
                }
                etTextMessage.setText("");
            }
        });


        setupChatList();

        setupGamesList();
    }

    private void setupGamesList() {
        ArrayList<Game> sampleGames = new ArrayList<>();
        sampleGames.add(new Game("2048"));
        sampleGames.add(new Game("Tic Tac Toe"));
        sampleGames.add(new Game("Pac Man"));
        sampleGames.add(new Game("Hang Man"));
        sampleGames.add(new Game("Flappy Bird"));

        mGameAdapter = new GameAdapter(this, sampleGames, new GameAdapter.OnItemClickListener() {
            @Override
            public void onClick(Game game) {
                // Clicked on Game
                Snackbar.make(mRvChat, "Under construction", Snackbar.LENGTH_SHORT).show();

                if (mRlGameInfo.getVisibility() == View.GONE) {
                    fetchAndShowGameInfo(game);
                } else {
                    hidGameInfo();
                }
            }
        });

        mRvGames.setAdapter(mGameAdapter);
        mRvGames.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void hidGameInfo() {
        mRlGameInfo.setVisibility(View.GONE);
    }

    private void fetchAndShowGameInfo(Game game) {
        mRlGameInfo.setVisibility(View.VISIBLE);
        mTvLeaderboard.setText(game.getTitle() + " Leaderboard: UNAVAILABLE");
    }

    private void setupChatList() {
        mChatAdapter = new ChatAdapter(this, mMessages, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onClick(Message message) {
                // Clicked on message
            }
        });

        mRvChat.setAdapter(mChatAdapter);
        mRvChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}
