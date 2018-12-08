package com.softwaresaturdays.app.arcade.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;
import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.activities.games.TicTacToe;
import com.softwaresaturdays.app.arcade.activities.games.TwentyFortyEight;
import com.softwaresaturdays.app.arcade.adapters.ChatAdapter;
import com.softwaresaturdays.app.arcade.adapters.GameAdapter;
import com.softwaresaturdays.app.arcade.models.Game;
import com.softwaresaturdays.app.arcade.models.GifMessage;
import com.softwaresaturdays.app.arcade.models.Message;
import com.softwaresaturdays.app.arcade.models.TextMessage;
import com.softwaresaturdays.app.arcade.models.User;
import com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper;
import com.softwaresaturdays.app.arcade.networkHelpers.NetworkHelper;
import com.softwaresaturdays.app.arcade.utilities.Util;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private RecyclerView mRvChat;
    private RecyclerView mRvGames;
    private ArrayList<Message> mMessages = new ArrayList<>();
    private ArrayList<Game> mAllGames = new ArrayList<>();
    private ChatAdapter mChatAdapter;
    private GameAdapter mGameAdapter;
    private RelativeLayout mRlGameInfo;
    private TextView mTvUserHighScore;
    private boolean mIsGifButton;
    private RoundedImageView mIvProfile;
    private Game mSelectedGame;
    private int mLimit = 12;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeContainer;
    private EditText mEtTextMessage;
    private Message mSelectedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        DatabaseHelper.fetchMessages(mLimit, new DatabaseHelper.OnDatabaseFetchListener() {
            @Override
            public void onMessagesFetched(ArrayList<Message> messages) {
                refreshChatList(messages);
                scrollToEnd();
            }
        });

        subscribeToNotifications();
    }

    // Refresh the recycler view with updated messages
    private void refreshChatList(ArrayList<Message> messages) {
        // Update messages REAL-TIME and update list view
        mMessages = messages;
        mChatAdapter = new ChatAdapter(ChatActivity.this, mMessages, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onClick(Message message) {
                // Clicked on message
            }

            @Override
            public void onLongClick(Message message, View v) {
                // Long click on message enables a popup to let users delete the message
                if (message.getUserId().equals(MyApplication.currUser.getUid())) {
                    PopupMenu popupMenu = new PopupMenu(ChatActivity.this, v);
                    popupMenu.setOnMenuItemClickListener(ChatActivity.this);
                    popupMenu.inflate(R.menu.popup_menu_message);
                    popupMenu.show();
                    mSelectedMessage = message;
                }
            }
        });
        if (mRvChat != null) {
            mRvChat.setAdapter(mChatAdapter);
        }
    }

    private void scrollToEnd() {
        mLayoutManager.scrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    // Subscribe to the topic "arcade", all notifications are sent users subscribed to the topic "arcade"
    private void subscribeToNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("arcade").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.d("TOPICS:", "UNSUCCESSFUL SUBSCRIPTION");
                } else {
                    Log.d("TOPICS:", "SUCCESSFUL SUBSCRIPTION");
                }
            }
        });
    }

    private void unsubscribeToNotifications() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("arcade");
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Used to make sure the view contents move up when soft keyboard comes up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(name, context, attrs);
    }

    // Fetch the next 12 messages and then refresh the recycler view
    void refreshChatList() {
        mLimit += 12;
        DatabaseHelper.fetchMessages(mLimit, new DatabaseHelper.OnDatabaseFetchListener() {
            @Override
            public void onMessagesFetched(ArrayList<Message> messages) {
                refreshChatList(messages);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Link Java code variables to UI elements
        mRvChat = findViewById(R.id.rvChat);
        mRvGames = findViewById(R.id.rvGames);
        mRlGameInfo = findViewById(R.id.rlGameInfo);
        mTvUserHighScore = findViewById(R.id.tvUserHighScore);
        ImageView ivSend = findViewById(R.id.ivSend);
        mIvProfile = findViewById(R.id.ivProfile);
        mEtTextMessage = findViewById(R.id.etTextMessage);
        CardView cvPlayButton = findViewById(R.id.cvPlayButton);
        mSwipeContainer = findViewById(R.id.swipeContainer);

        // Needed to set global state - used to check that notifications are not displayed when app is foreground
        MyApplication.isForeground = true;

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                mSwipeContainer.setRefreshing(false);
                refreshChatList();
            }
        });

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Assert throws error if condition is not true. Good for debugging
        assert MyApplication.currUser != null;

        // Load profile pic using Glide
        Glide.with(this).load(MyApplication.currUser.getPhotoUrl()).into(mIvProfile);

        // Game info only visible when clicked on a game
        mRlGameInfo.setVisibility(View.GONE);

        // Set clicklisteners to the activity so all onClick callbacks are together
        cvPlayButton.setOnClickListener(this);
        ivSend.setOnClickListener(this);
        mEtTextMessage.setOnClickListener(this);
        mIvProfile.setOnClickListener(this);

        mEtTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // If a text entered is more than 2 letters, show GIF icon to allow gif adding
                if (s.length() > 2) {
                    // Show GIF icon
                    Glide.with(ChatActivity.this).load(R.drawable.gif).into(mIvProfile);
                    mIsGifButton = true;
                } else {
                    mIsGifButton = false;
                    // Load profile pic using Glide
                    Glide.with(ChatActivity.this).load(MyApplication.currUser.getPhotoUrl()).into(mIvProfile);
                }
            }
        });


        setupChatList();

        setupGamesList();

        automaticRefreshGamesScores();
    }

    // Fetches all game info and update game list
    private void automaticRefreshGamesScores() {
        DatabaseHelper.getLiveGameHighScores(new DatabaseHelper.onGamesFetchListener() {
            @Override
            public void onGamesFetched(ArrayList<Game> games) {
                // Add all latest game data to mAllGames
                mAllGames = new ArrayList<>();
                mAllGames.addAll(games);

                // Add extra games under construction
                mAllGames.add(new Game("Pac Man"));
                mAllGames.add(new Game("Hang Man"));
                mAllGames.add(new Game("Flappy Bird"));

                // Reset game adapter to update list view
                mGameAdapter = new GameAdapter(ChatActivity.this, mAllGames, new GameAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Game game) {
                        if (mSelectedGame == null) {
                            // if previously no game selected
                            mSelectedGame = game;
                            showGameInfo(game);
                        } else if (mRlGameInfo.getVisibility() == View.GONE || !mSelectedGame.getTitle().equals(game.getTitle())) {
                            // if user selected a game with a new title
                            showGameInfo(game);
                            mSelectedGame = game;
                        } else {
                            // user tapped on the same game again, hide the game info
                            hidGameInfo();
                            mSelectedGame = game;
                        }
                    }
                });

                if (mRvGames != null) {
                    mRvGames.setAdapter(mGameAdapter);
                }
            }
        });
    }

    // Sets up the intial list
    private void setupGamesList() {
        mAllGames.add(new Game("2048"));
        mAllGames.add(new Game("TicTacToe"));
        mAllGames.add(new Game("Pac Man"));
        mAllGames.add(new Game("Hang Man"));
        mAllGames.add(new Game("Flappy Bird"));

        mGameAdapter = new GameAdapter(this, mAllGames, new GameAdapter.OnItemClickListener() {
            @Override
            public void onClick(Game game) {
                if (mSelectedGame == null) {
                    // if previously no game selected
                    mSelectedGame = game;
                    showGameInfo(game);
                } else if (mRlGameInfo.getVisibility() == View.GONE || !mSelectedGame.getTitle().equals(game.getTitle())) {
                    // if user selected a game with a new title
                    showGameInfo(game);
                    mSelectedGame = game;
                } else {
                    // user tapped on the same game again, hide the game info
                    hidGameInfo();
                    mSelectedGame = game;
                }
            }
        });

        mRvGames.setAdapter(mGameAdapter);
        mRvGames.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void hidGameInfo() {
        mRlGameInfo.setVisibility(View.GONE);
    }

    private void showGameInfo(Game game) {
        mRlGameInfo.setVisibility(View.VISIBLE);
        if (MyApplication.currUser.getHighScores() != null && MyApplication.currUser.getHighScores().get(game.getTitle()) != null) {
            mTvUserHighScore.setText("Your high score: " + MyApplication.currUser.getHighScores().get(game.getTitle()));
        } else {
            mTvUserHighScore.setText("Your high score: 0.0");
        }

        TextView tvTop1 = findViewById(R.id.tvTop1);
        TextView tvTop2 = findViewById(R.id.tvTop2);
        TextView tvTop3 = findViewById(R.id.tvTop3);

        tvTop1.setText(game.getTop1() + "");
        tvTop2.setText(game.getTop2() + "");
        tvTop3.setText(game.getTop3() + "");

        RoundedImageView ivProfile1 = findViewById(R.id.ivProfile1);
        RoundedImageView ivProfile2 = findViewById(R.id.ivProfile2);
        RoundedImageView ivProfile3 = findViewById(R.id.ivProfile3);

        User user1 = Util.getUserData(game.getUid1(), this);
        User user2 = Util.getUserData(game.getUid2(), this);
        User user3 = Util.getUserData(game.getUid3(), this);

        // Adding placeholder loading image while the GIF loads
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_account_circle_black_36dp);

        // Load profile pic using Glide
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(user1.getPhotoUrl()).into(ivProfile1);
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(user2.getPhotoUrl()).into(ivProfile2);
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(user3.getPhotoUrl()).into(ivProfile3);
    }

    private void setupChatList() {
        mChatAdapter = new ChatAdapter(this, mMessages, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onClick(Message message) {
                // Clicked on message
            }

            @Override
            public void onLongClick(Message message, View v) {
                // Long click on message
                if (message.getUserId().equals(MyApplication.currUser.getUid())) {
                    PopupMenu popupMenu = new PopupMenu(ChatActivity.this, v);
                    popupMenu.setOnMenuItemClickListener(ChatActivity.this);
                    popupMenu.inflate(R.menu.popup_menu_message);
                    popupMenu.show();
                    mSelectedMessage = message;
                }
            }
        });

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRvChat.setAdapter(mChatAdapter);
        mRvChat.setLayoutManager(mLayoutManager);
        scrollToEnd();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.cvPlayButton:
                // When game play button is pressed, start the appropriate Game activity
                if (mSelectedGame != null) {
                    switch (mSelectedGame.getTitle()) {
                        case "2048":
                            startActivity(new Intent(getApplicationContext(), TwentyFortyEight.class));
                            break;
                        case "TicTacToe":
                            startActivity(new Intent(getApplicationContext(), TicTacToe.class));
                            break;
                        default:
                            Snackbar.make(mRvChat, mSelectedGame.getTitle() + " is Under construction", Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.ivSend:
                // When send message button is pressed, check for search text
                String text = mEtTextMessage.getText().toString();
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
                mEtTextMessage.setText("");
                break;
            case R.id.etTextMessage:
                // When user clicks on edittext, the list is scrolled to end so user can see the last entered message
                new CountDownTimer(200, 200) {
                    public void onFinish() {
                        // When timer is finished
                        scrollToEnd();
                    }

                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
                break;
            case R.id.ivProfile:
                // When user clicks on profile pic button, it either gives user the option menu or let's the user send a gif
                if (mIsGifButton) {
                    final String gifSearchText = mEtTextMessage.getText().toString();
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
                        }
                    });
                    mEtTextMessage.setText("");
                    Util.hideKeyboard(ChatActivity.this);
                } else {
                    PopupMenu popupMenu = new PopupMenu(ChatActivity.this, mIvProfile);
                    popupMenu.setOnMenuItemClickListener(ChatActivity.this);
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.show();
                }
                break;
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.notificationsOFF:
                unsubscribeToNotifications();
                Snackbar.make(mIvProfile, "Notifications turned OFF", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.notificationsON:
                subscribeToNotifications();
                Snackbar.make(mIvProfile, "Notifications turned ON", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                if (mSelectedMessage != null) {
                    DatabaseHelper.deleteMessage(mSelectedMessage);
                }
                break;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                break;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.isForeground = false;
    }
}
