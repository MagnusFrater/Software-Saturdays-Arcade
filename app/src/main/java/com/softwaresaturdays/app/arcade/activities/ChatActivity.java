package com.softwaresaturdays.app.arcade.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.adapters.ChatAdapter;
import com.softwaresaturdays.app.arcade.models.TextMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRvChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mRvChat = findViewById(R.id.rvChat);
        RoundedImageView ivProfile = findViewById(R.id.ivProfile);

        assert MyApplication.currUser != null;

        // Load profile pic using Picasso
        Picasso.get().load(MyApplication.currUser.getPhotoUrl()).into(ivProfile);


        ArrayList<TextMessage> messages = new ArrayList<>();
        messages.add(new TextMessage("first text", MyApplication.currUser));
        messages.add(new TextMessage("second text", MyApplication.currUser));

        ChatAdapter mChatAdapter = new ChatAdapter(this, messages, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onClick(com.softwaresaturdays.app.arcade.models.Message message) {
                // Clicked on message
            }
        });

        mRvChat.setAdapter(mChatAdapter);
        mRvChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}
