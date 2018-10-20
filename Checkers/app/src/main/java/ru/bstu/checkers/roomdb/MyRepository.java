package ru.bstu.checkers.roomdb;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * A Repository is a class that abstracts access to multiple data sources.
 * The Repository is not part of the Architecture Components libraries,
 * but is a suggested best practice for code separation and architecture.
 * A Repository class handles data operations. It provides a clean API to the rest of the app for app data.
 * A Repository manages query threads and allows you to use multiple backends.
 * In the most common example, the Repository implements the logic for deciding whether
 * to fetch data from a network or use results cached in a local database.
 */
public class MyRepository {
    //Add member variables for the DAO and the list of games.
    private GameDao mGameDao;
    private LiveData<List<Game>> mAllGames;
    private PositionDao mPositionDao;
    private WayDao mWayDao;
    private ItemDao mItemDao;

    MyRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        mGameDao = db.gameDao();
        mPositionDao = db.positionDao();
        mWayDao = db.wayDao();
        mItemDao = db.itemDao();
        mAllGames = mGameDao.getAllGames();
    }

    //Add a wrapper for getAllWords(). Room executes all queries on a separate thread.
    //Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Game>> getAllGames() {
        return mAllGames;
    }

    //Add a wrapper for the insert() method. You must call this on a non-UI thread or your app will crash.
    //Room ensures that you don't do any long-running operations on the main thread, blocking the UI.
    public void insert (Game game) {
        new insertAsyncTask(mGameDao).execute(game);
    }

    private static class insertAsyncTask extends AsyncTask<Game, Void, Void> {

        private GameDao mAsyncTaskDao;

        insertAsyncTask(GameDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Game... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}