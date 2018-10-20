package ru.bstu.checkers.roomdb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.bstu.checkers.R;


public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {
    class GameViewHolder extends RecyclerView.ViewHolder {
        private final TextView gameItemView;

        private GameViewHolder(View itemView) {
            super(itemView);
            gameItemView = itemView.findViewById(R.id.tv_loadgame_item);
        }
    }

    private final LayoutInflater mInflater;
    private List<Game> mGames; // Cached copy of games

    public GameListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.loadgame_item, parent, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        if (mGames != null) {
            Game current = mGames.get(position);
            holder.gameItemView.setText(current.mName);
        } else {
            // Covers the case of data not being ready yet.
            holder.gameItemView.setText("No Game");
        }
    }

    public void setGames(List<Game> games){
        mGames = games;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mGames has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mGames != null)
            return mGames.size();
        else return 0;
    }
}
