package com.softwaresaturdays.app.arcade.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.adapters.ChatAdapter;
import com.softwaresaturdays.app.arcade.models.Message;
import com.softwaresaturdays.app.arcade.models.TextMessage;
import com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRvChat;
    private ArrayList<Message> mMessages = new ArrayList<>();
    private ChatAdapter mChatAdapter;

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

        mRvChat = findViewById(R.id.rvChat);
        ImageView ivSend = findViewById(R.id.ivSend);
        RoundedImageView ivProfile = findViewById(R.id.ivProfile);
        final EditText etTextMessage = findViewById(R.id.etTextMessage);

        assert MyApplication.currUser != null;

        // Load profile pic using Picasso
        Picasso.get().load(MyApplication.currUser.getPhotoUrl()).into(ivProfile);

        mChatAdapter = new ChatAdapter(this, mMessages, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onClick(Message message) {
                // Clicked on message
            }
        });

        mRvChat.setAdapter(mChatAdapter);
        mRvChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etTextMessage.getText().toString();
                if (text.isEmpty()) {
                    Snackbar.make(mRvChat, "Please enter text", Snackbar.LENGTH_SHORT);
                } else {
                    // Create new text message
                    Message message = new TextMessage(text, MyApplication.currUser);
                    // Upload message to database
                    DatabaseHelper.uploadMessage(message);
                }
                etTextMessage.setText("");
            }
        });
    }
}
