package ru.bstu.checkers.roomdb;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ru.bstu.checkers.GameActivity;
import ru.bstu.checkers.LoadGameActivity;
import ru.bstu.checkers.MyApplication;


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


    private static void delete(String gameName, GameDao mAsyncGameDao,
                               PositionDao mAsyncPositionDao, WayDao mAsyncWayDao, ItemDao mAsyncItemDao) {
        int res;
        // Удаляем запись в game_table
        res = mAsyncGameDao.delete(gameName);
        Position position = mAsyncPositionDao.getPosition(gameName);
        // Удаляем запись в position_table
        res = mAsyncPositionDao.delete(gameName);
        List<Way> ways = mAsyncWayDao.getWays(position.GetWaysId());
        // Удаляем записи в way_table
        res = mAsyncWayDao.delete(position.GetWaysId());
        // Удаляем записи в item_table
        for(int i = 0; i < ways.size(); ++i)
            res = mAsyncItemDao.delete(ways.get(i).GetItemsId());
    }

    //Add a wrapper for the insert() method. You must call this on a non-UI thread or your app will crash.
    //Room ensures that you don't do any long-running operations on the main thread, blocking the UI.
    public void insert (Game game, ArrayList<Item>[] ways) {
        new insertAsyncTask(game, mGameDao, mPositionDao, mWayDao, mItemDao, ways).execute();
    }
    public static class insertAsyncTask extends AsyncTask<Void, Void, Void> {
        private Game mGame;
        private GameDao mAsyncGameDao;
        private PositionDao mAsyncPositionDao;
        private WayDao mAsyncWayDao;
        private ItemDao mAsyncItemDao;
        private ArrayList<Item>[] ways;
        insertAsyncTask(Game game, GameDao gDao, PositionDao pDao, WayDao wDao, ItemDao iDao, ArrayList<Item>[] ways)
        {
            mGame = game;
            mAsyncGameDao = gDao;
            mAsyncPositionDao = pDao;
            mAsyncWayDao = wDao;
            mAsyncItemDao = iDao;
            this.ways = ways;
        }
        @Override
        protected Void doInBackground(final Void... params) {
            Game game = mAsyncGameDao.getGame(mGame.mName);
            // Удаляем записи, если игра с таким названием уже имеется
            if (null != game)
                delete(mGame.mName, mAsyncGameDao, mAsyncPositionDao, mAsyncWayDao, mAsyncItemDao);
            // Добавляем запись в таблицу game_table
            mAsyncGameDao.insert(mGame);
            final int WAY_SIZE = 8;
            ArrayList<Long> waysId = new ArrayList<>(ways.length);
            for(int i = 0; i < ways.length; ++i)
            {
                ArrayList<Long> itemsId = new ArrayList<>(WAY_SIZE);
                // Добавляем в таблицу item_table шашки/клетки текущей диагонали
                for(int j = 0; j < ways[i].size(); ++j) {
                    long rowid = mAsyncItemDao.insert(ways[i].get(j));
                    itemsId.add(rowid);
                }
                while(itemsId.size() < WAY_SIZE) itemsId.add(0L);
                // Добавляем в таблицу way_table текущую диагональ
                long rowid = mAsyncWayDao.insert(new Way(itemsId));
                waysId.add(rowid);
            }
            // Добавляем запись в таблицу position_table
            mAsyncPositionDao.insert(new Position(mGame.mName, waysId));
            return null;
        }
    }

    public void delete(String gameName) {
        new deleteAsyncTask(gameName, mGameDao, mPositionDao, mWayDao, mItemDao).execute();
    }
    private static class deleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private String mGameName;
        private GameDao mAsyncGameDao;
        private PositionDao mAsyncPositionDao;
        private WayDao mAsyncWayDao;
        private ItemDao mAsyncItemDao;
        deleteAsyncTask(String gameName, GameDao gDao, PositionDao pDao, WayDao wDao, ItemDao iDao) {
            mGameName = gameName;
            mAsyncGameDao = gDao;
            mAsyncPositionDao = pDao;
            mAsyncWayDao = wDao;
            mAsyncItemDao = iDao;
        }
        @Override
        protected Void doInBackground(final Void... params) {
            delete(mGameName, mAsyncGameDao, mAsyncPositionDao, mAsyncWayDao, mAsyncItemDao);
            return null;
        }
    }

    public void load(String gameName) {
        new loadAsyncTask(gameName, mGameDao, mPositionDao, mWayDao, mItemDao).execute();
    }
    private static class loadAsyncTask extends AsyncTask<Void, Void, Void> {
        private String mGameName;
        private GameDao mAsyncGameDao;
        private PositionDao mAsyncPositionDao;
        private WayDao mAsyncWayDao;
        private ItemDao mAsyncItemDao;
        private ArrayList<ru.bstu.checkers.Item>[] mWays;
        private Game mGame;
        loadAsyncTask(String gameName, GameDao gDao, PositionDao pDao, WayDao wDao, ItemDao iDao) {
            mGameName = gameName;
            mAsyncGameDao = gDao;
            mAsyncPositionDao = pDao;
            mAsyncWayDao = wDao;
            mAsyncItemDao = iDao;
            mWays = new ArrayList[ru.bstu.checkers.Item.WAYS_COUNT];
            for (int i = 0; i < mWays.length; ++i) // Максимум 8 клеток/шашек на одной диагонали
                mWays[i] = new ArrayList<ru.bstu.checkers.Item>(8);
        }
        @Override
        protected Void doInBackground(final Void... params) {
            // Получаем позицию (содержит id диагоналей в БД для выбранной игры)
            Position position = mAsyncPositionDao.getPosition(mGameName);
            // Получаем диагонали ways, содержащие id item'ов в БД
            List<Way> _ways = mAsyncWayDao.getWays(position.GetWaysId());
            for (int i = 0; i < _ways.size(); ++i)
            {
                // Получаем items для текущей диагонали
                List<Item> _items = mAsyncItemDao.getItems(_ways.get(i).GetItemsId());
                for (int j = 0; j < _items.size(); ++j)
                {
                    Item _item = _items.get(j);
                    ru.bstu.checkers.Item item = new ru.bstu.checkers.Item(_item.mViewId,
                            _item.mType, _item.mKing, _item.mWay1Idx, _item.mWay2Idx);
                    /* Один и тот же item на разных диагоналях должен быть одним и тем же объектом */
                    LOOP:
                    {
                        for (int q = 0; q < mWays.length; ++q)
                        {
                            for (int s = 0; s < mWays[q].size(); ++s)
                            {
                                ru.bstu.checkers.Item added = mWays[q].get(s);
                                if (item.id == added.id) {
                                    item = added;
                                    break LOOP;
                                }
                            }
                        }
                    }
                    mWays[i].add(item);
                }
            }
            mGame = mAsyncGameDao.getGame(mGameName);
            return null;
        }
        /** После того как загрузили игру с БД, запускаем GameActivity*/
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Activity activity = MyApplication.getCurrentActivity();
            Intent intent = new Intent(activity, GameActivity.class);
            intent.putExtra(LoadGameActivity.EXTRA_LOAD_GAME, true)
                .putExtra(LoadGameActivity.EXTRA_TURN, mGame.mTurn)
               .putExtra(LoadGameActivity.EXTRA_WAYS, mWays);
            activity.startActivity(intent);
            activity.finish();
        }
    }
}