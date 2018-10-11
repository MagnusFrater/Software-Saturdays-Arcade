package com.softwaresaturdays.app.arcade.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.models.Game;

import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private GameAdapter.OnItemClickListener listener;
    private Context mContext;
    private ArrayList<Game> games;

    public GameAdapter(Context context, ArrayList<Game> games, GameAdapter.OnItemClickListener listener) {
        this.mContext = context;
        this.games = games;
        this.listener = listener;
    }

    class GameViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGameTitle;
        private CardView cvGame;

        GameViewHolder(View itemView) {
            super(itemView);
            tvGameTitle = itemView.findViewById(R.id.tvGameTitle);
            cvGame = itemView.findViewById(R.id.cvGame);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {

        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);
        // set the view's size, margins, padding and layout parameters
        return new GameAdapter.GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final GameAdapter.GameViewHolder gameViewHolder = (GameAdapter.GameViewHolder) viewHolder;
        final Game game = games.get(position);

        gameViewHolder.tvGameTitle.setText(game.getTitle());

        gameViewHolder.cvGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(game);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return games.size();
    }

    public interface OnItemClickListener {
        void onClick(Game game);
    }

}
