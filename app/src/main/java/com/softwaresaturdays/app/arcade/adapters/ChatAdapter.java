package com.softwaresaturdays.app.arcade.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.models.Message;
import com.softwaresaturdays.app.arcade.models.TextMessage;

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

        ChatViewHolder(View itemView) {
            super(itemView);
            tvTextMessage = itemView.findViewById(R.id.tvTextMessage);
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
        ChatViewHolder chatViewHolder = (ChatViewHolder) viewHolder;
        final Message message = messages.get(position);

        if (message.getType().equals(Message.TYPE_TEXT_MESSAGE)) {
            TextMessage textMessage = (TextMessage) message;
            chatViewHolder.tvTextMessage.setText(textMessage.getText());
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
