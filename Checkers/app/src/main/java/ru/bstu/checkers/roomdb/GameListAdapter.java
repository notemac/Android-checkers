package ru.bstu.checkers.roomdb;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import ru.bstu.checkers.DeleteLoadSavedGameDialogFragment;
import ru.bstu.checkers.LoadGameActivity;
import ru.bstu.checkers.MyApplication;
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
    //private List<Game> mGames; // Cached copy of games
    private Cursor mCursor = null;

    public GameListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.loadgame_item, parent, false);
        TextView tv =  itemView.findViewById(R.id.tv_loadgame_item);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView)v;
                LoadGameActivity lga = (LoadGameActivity)(MyApplication.getCurrentActivity());
                lga.mSelectedGameName = tv.getText().toString();
                DeleteLoadSavedGameDialogFragment dialog = new DeleteLoadSavedGameDialogFragment();
                dialog.show(MyApplication.getCurrentActivity().getFragmentManager(), MyApplication.getCurrentActivity().getResources().getString(R.string.DLSGDialog));
            }
        });
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        if (mCursor.moveToPosition(position)) {
            holder.gameItemView.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(Game.COLUMN_NAME)));
        } else {
            // Covers the case of data not being ready yet.
        }
    }

    public void setGames(Cursor cursor){
        //mGames = games;
        mCursor = cursor;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mCursor has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        return (null == mCursor) ? 0 : mCursor.getCount();
        /*if (mGames != null) return mGames.size();
        else return 0;*/
    }
}
