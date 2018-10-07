package com.softwaresaturdays.app.arcade.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.models.Message;
import com.softwaresaturdays.app.arcade.models.TextMessage;
import com.softwaresaturdays.app.arcade.models.User;
import com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private ArrayList<Message> messages;

    public ChatAdapter(Context context, ArrayList<Message> messages, OnItemClickListener listener) {
        this.mContext = context;
        this.messages = messages;
        this.onItemClickListener = listener;
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTextMessage;
        private CardView cvMessage;
        private RoundedImageView ivProfilePic;

        ChatViewHolder(View itemView) {
            super(itemView);
            tvTextMessage = itemView.findViewById(R.id.tvTextMessage);
            cvMessage = itemView.findViewById(R.id.cvMessage);
            ivProfilePic = itemView.findViewById(R.id.ivProfileInMessage);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {

        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        // set the view's size, margins, padding and layout parameters
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final ChatViewHolder chatViewHolder = (ChatViewHolder) viewHolder;
        final Message message = messages.get(position);

        if (message.getType().equals(Message.TYPE_TEXT_MESSAGE)) {
            TextMessage textMessage = (TextMessage) message;
            chatViewHolder.tvTextMessage.setText(textMessage.getText());
        }

        // If the message is created by the user
        if (message.getUserId() != null && message.getUserId().equals(MyApplication.currUser.getUid())) {

            // programmatically align it right in the view
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            chatViewHolder.cvMessage.setLayoutParams(params);

            // Make profile pic invisible
            chatViewHolder.ivProfilePic.setVisibility(View.GONE);

            // Change color of card view to primary color
            chatViewHolder.cvMessage.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));

            // Change text color to white
            chatViewHolder.tvTextMessage.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        } else {

            // programmatically align it left in the view
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(140, 8, 8, 8);  // left, top, right, bottom
            chatViewHolder.cvMessage.setLayoutParams(params);

            // Fetch and show author's profile pic
            chatViewHolder.ivProfilePic.setVisibility(View.VISIBLE);

            // TODO Change this so it's not calling for profile every time
            DatabaseHelper.getUserInfo(message.getUserId(), new DatabaseHelper.OnUserInfoFetchListener() {
                @Override
                public void onUserInfoFetched(User user) {
                    Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.).into(chatViewHolder.ivProfilePic);
                }
            });

            // Change color of card view to white
            chatViewHolder.cvMessage.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorGrey));

            // Change text color to Black
            chatViewHolder.tvTextMessage.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface OnItemClickListener {
        void onClick(Message message);
    }

}
