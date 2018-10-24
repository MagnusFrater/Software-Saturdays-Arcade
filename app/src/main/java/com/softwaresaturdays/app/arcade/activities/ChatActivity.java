package com.softwaresaturdays.app.arcade.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;
import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.adapters.ChatAdapter;
import com.softwaresaturdays.app.arcade.adapters.GameAdapter;
import com.softwaresaturdays.app.arcade.models.Game;
import com.softwaresaturdays.app.arcade.models.GifMessage;
import com.softwaresaturdays.app.arcade.models.Message;
import com.softwaresaturdays.app.arcade.models.TextMessage;
import com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper;
import com.softwaresaturdays.app.arcade.networkHelpers.NetworkHelper;
import com.softwaresaturdays.app.arcade.utilities.Util;

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
    private boolean mIsGifButton;
    private RoundedImageView mIvProfile;
    private Game mSelectedGame;
    private int mLimit = 12;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        DatabaseHelper.fetchMessages(mLimit, new DatabaseHelper.OnDatabaseFetchListener() {
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
                    mRvChat.scrollToPosition(mMessages.size() - 1);
                }
            }
        });

        subscribeToNotifications();
    }

    private void subscribeToNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("arcade");
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(name, context, attrs);
    }

    void refreshChatList() {
        mLimit += 12;
        DatabaseHelper.fetchMessages(mLimit, new DatabaseHelper.OnDatabaseFetchListener() {
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
        mIvProfile = findViewById(R.id.ivProfile);
        final EditText etTextMessage = findViewById(R.id.etTextMessage);
        CardView cvPlayButton = findViewById(R.id.cvPlayButton);


        mSwipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                mSwipeContainer.setRefreshing(false);
                refreshChatList();
            }
        });

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        assert MyApplication.currUser != null;

        mIvProfile.setImageResource(R.drawable.gif);

        // Load profile pic using Glide
        Glide.with(this).load(MyApplication.currUser.getPhotoUrl()).into(mIvProfile);

        // Game info only visible when clicked on a game
        mRlGameInfo.setVisibility(View.GONE);


        cvPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedGame != null) {
                    Snackbar.make(mRvChat, mSelectedGame.getTitle() + " is Under construction", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etTextMessage.getText().toString();
                if (text.isEmpty()) {
                    Snackbar.make(mRvChat, "Please enter searchText", Snackbar.LENGTH_SHORT).show();
                } else {
                    // Create new searchText message
                    Message message = new TextMessage(text);
                    // Upload message to database
                    DatabaseHelper.uploadMessage(message);

                    // Send notifications to all devices
                    NetworkHelper.sendNotifications(MyApplication.currUser.getName() + " says " + text,
                            MyApplication.currUser.getUid(), ChatActivity.this);
                }
                etTextMessage.setText("");
            }
        });

        etTextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRvChat.scrollToPosition(mMessages.size() - 1);
            }
        });

        etTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2) {
                    Glide.with(ChatActivity.this).load(R.drawable.gif).into(mIvProfile);
                    mIsGifButton = true;
                } else {
                    mIsGifButton = false;
                    // Load profile pic using Glide
                    Glide.with(ChatActivity.this).load(MyApplication.currUser.getPhotoUrl()).into(mIvProfile);
                }
            }
        });

        mIvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsGifButton) {
                    final String gifSearchText = etTextMessage.getText().toString();
                    NetworkHelper.fetchGIF(gifSearchText, ChatActivity.this, new NetworkHelper.OnFetchSuccessListener() {
                        @Override
                        public void onFetchedGifUrl(String gifUrl) {
                            // Got the url
                            Log.d("CHAT ACTIVITY", "URL for GIF: " + gifUrl);

                            if (gifUrl != null && !gifUrl.isEmpty()) {
                                // Create a new GIF message
                                Message message = new GifMessage(gifUrl, gifSearchText);
                                // Upload GIF message to database
                                DatabaseHelper.uploadMessage(message);

                                // Send notifications to all devices
                                NetworkHelper.sendNotifications(MyApplication.currUser.getName() + " sent a GIF for " + gifSearchText,
                                        MyApplication.currUser.getUid(), ChatActivity.this);
                            }

                            mRvChat.scrollToPosition(mMessages.size() - 1);
                        }
                    });
                    etTextMessage.setText("");
                    Util.hideKeyboard(ChatActivity.this);
                }
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
                mSelectedGame = game;

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

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRvChat.setAdapter(mChatAdapter);
        mRvChat.setLayoutManager(mLayoutManager);
        mRvChat.scrollToPosition(mMessages.size() - 1);
    }
}
