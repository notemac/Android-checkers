package ru.bstu.checkers;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;

import java.util.Arrays;
import java.util.List;

import ru.bstu.checkers.roomdb.Game;
import ru.bstu.checkers.roomdb.GameDao;
import ru.bstu.checkers.roomdb.GameListAdapter;
import ru.bstu.checkers.roomdb.ItemDao;
import ru.bstu.checkers.roomdb.MyRepository;
import ru.bstu.checkers.roomdb.MyRoomDatabase;
import ru.bstu.checkers.roomdb.Position;
import ru.bstu.checkers.roomdb.PositionDao;
import ru.bstu.checkers.roomdb.Way;
import ru.bstu.checkers.roomdb.WayDao;

public class LoadGameActivity extends AppCompatActivity
        implements DeleteLoadSavedGameDialogFragment.NoticeDialogListener, View.OnClickListener
{
    public static final String EXTRA_LOAD_GAME = "ru.bstu.checkers.EXTRA_LOAD_GAME";
    public static final String EXTRA_TURN = "ru.bstu.checkers.EXTRA_TURN";
    public static final String EXTRA_WAYS = "ru.bstu.checkers.EXTRA_WAYS";
    private static final int LOADER_GET_SAVED_GAMES_LIST_FROM_DB = 1;
    public String mSelectedGameName;
    private GameListAdapter mGamesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadgame);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        mGamesAdapter = new GameListAdapter(this);
        recyclerView.setAdapter(mGamesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportLoaderManager().initLoader(LOADER_GET_SAVED_GAMES_LIST_FROM_DB, null, mLoaderCallbacks);
    }

    /*CursorLoader - подкласс класса AsyncTaskLoader, который запрашивает ContentResolver и возвращает Cursor.
    Этот класс реализует протокол Loader стандартным способом для выполнения запросов к курсорам. Он строится
    на AsyncTaskLoader для выполнения запроса к курсору в фоновом потоке, чтобы не блокировать пользовательский
    интерфейс приложения. Использование этого загрузчика — это лучший способ асинхронной загрузки данных из ContentProvider
    вместо выполнения управляемого запроса через платформу или API-интерфейсы операции.*/
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    switch (id) {
                        case LOADER_GET_SAVED_GAMES_LIST_FROM_DB:
                            return new CursorLoader(getApplicationContext(),
                                    MyContentProvider.URI_GAME,
                                    new String[]{Game.COLUMN_NAME},
                                    null, null, null);
                        default:
                            throw new IllegalArgumentException();
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    switch (loader.getId()) {
                        case LOADER_GET_SAVED_GAMES_LIST_FROM_DB:
                            mGamesAdapter.setGames(cursor);
                            break;
                    }
                }
                /** Вызывается, когда курсор вот-вот будет изменен */
                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    switch (loader.getId()) {
                        case LOADER_GET_SAVED_GAMES_LIST_FROM_DB:
                            mGamesAdapter.setGames(null);
                            break;
                    }
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }
    @Override
    public boolean onNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onSavedGameDialogDeleteClick(DialogFragment dialog) {
        MyRepository.delete(getContentResolver(), mSelectedGameName);
    }
    @Override
    public void onSavedGameDialogLoadClick(DialogFragment dialog) {
        MyRepository.load(mSelectedGameName);
    }

    @Override
    public void onClick(View v) {

    }
}
