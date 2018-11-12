package com.softwaresaturdays.app.arcade.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.models.GifMessage;
import com.softwaresaturdays.app.arcade.models.Message;
import com.softwaresaturdays.app.arcade.models.TextMessage;
import com.softwaresaturdays.app.arcade.models.User;
import com.softwaresaturdays.app.arcade.utilities.Util;

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
        private TextView tvInfo;
        private ImageView ivGif;
        private CardView cvMessage;
        private RoundedImageView ivProfilePic;

        ChatViewHolder(View itemView) {
            super(itemView);
            tvTextMessage = itemView.findViewById(R.id.tvTextMessage);
            cvMessage = itemView.findViewById(R.id.cvMessage);
            ivProfilePic = itemView.findViewById(R.id.ivProfileInMessage);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            ivGif = itemView.findViewById(R.id.ivGif);
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

        // Check message type and update display accordingly
        if (message.getType().equals(Message.TYPE_TEXT_MESSAGE)) {
            TextMessage textMessage = (TextMessage) message;
            chatViewHolder.tvTextMessage.setText(textMessage.getText());
            chatViewHolder.ivGif.setVisibility(View.GONE);
        } else if (message.getType().equals(Message.TYPE_GIF_MESSAGE)) {
            GifMessage gifMessage = (GifMessage) message;
            chatViewHolder.ivGif.setVisibility(View.VISIBLE);
            chatViewHolder.tvTextMessage.setText(gifMessage.searchText);

            // Adding placeholder loading image while the GIF loads
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.progress);
            requestOptions.error(R.drawable.progress);

            Glide.with(chatViewHolder.ivGif)
                    .setDefaultRequestOptions(requestOptions)
                    .load(gifMessage.getUrl())
                    .into(chatViewHolder.ivGif);
        }

        // Set extra info about the message's time and author
        User author = Util.getUserData(message.getUserId(), mContext);
        chatViewHolder.tvInfo.setText(author.getName() + "  |  " + Util.getFormattedTime((double) message.getTimestamp()));
        chatViewHolder.tvInfo.setVisibility(View.GONE);

        chatViewHolder.cvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(message);

                // Toggle visibility of the extra info
                if (chatViewHolder.tvInfo.getVisibility() == View.GONE) {
                    chatViewHolder.tvInfo.setVisibility(View.VISIBLE);
                } else {
                    chatViewHolder.tvInfo.setVisibility(View.GONE);
                }
            }
        });

        // long click interface call
        chatViewHolder.cvMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onLongClick(message, chatViewHolder.cvMessage);
                return true;
            }
        });

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

            // Change searchText color to white
            chatViewHolder.tvTextMessage.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        } else {
            // If the message was created by other users

            // Fetch and show author's profile pic
            chatViewHolder.ivProfilePic.setVisibility(View.VISIBLE);
            chatViewHolder.ivProfilePic.setImageResource(R.drawable.ic_account_circle_black_36dp);

            // programmatically align card view left in the view
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(8, 26, 8, 8);  // left, top, right, bottom
            params.addRule(RelativeLayout.RIGHT_OF, chatViewHolder.ivProfilePic.getId()); // align card right of profile pic
            chatViewHolder.cvMessage.setLayoutParams(params);

            Glide.with(mContext).load(author.getPhotoUrl()).into(chatViewHolder.ivProfilePic);

            // Change color of card view to white
            chatViewHolder.cvMessage.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorGrey));

            // Change searchText color to Black
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

        void onLongClick(Message message, View v);
    }

}
