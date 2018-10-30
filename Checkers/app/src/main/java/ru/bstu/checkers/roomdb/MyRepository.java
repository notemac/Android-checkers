package ru.bstu.checkers.roomdb;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ru.bstu.checkers.GameActivity;
import ru.bstu.checkers.LoadGameActivity;
import ru.bstu.checkers.MyApplication;
import ru.bstu.checkers.MyContentProvider;
import ru.bstu.checkers.Utility;


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
    MyRepository(Application application) { }

    //Add a wrapper for the insert() method. You must call this on a non-UI thread or your app will crash.
    //Room ensures that you don't do any long-running operations on the main thread, blocking the UI.
    public static void insert(ContentResolver resolver, Game game, ArrayList<ru.bstu.checkers.roomdb.Item>[] ways) {
        new insertAsyncTask(resolver, game, ways).execute();
    }
    private static class insertAsyncTask extends AsyncTask<Void, Void, Void> {
        private ContentResolver mResolver;
        private Game mGame;
        ArrayList<ru.bstu.checkers.roomdb.Item>[] mWays;
        insertAsyncTask(ContentResolver resolver, Game game, ArrayList<ru.bstu.checkers.roomdb.Item>[] ways)
        { mResolver = resolver; mGame = game; mWays = ways; }
        @Override
        protected Void doInBackground(final Void... params) {
            Cursor cursor = mResolver.query(Uri.parse(MyContentProvider.URI_GAME + "/" + mGame.mName),
                    null, null, null,null);
            // Удаляем записи, если игра с таким названием уже имеется
            if (cursor.getCount() > 0)
                delete(mGame.mName, mResolver);
            cursor.close();

            // Добавляем запись в таблицу game_table
            ContentValues cv = new ContentValues();
            cv.put(Game.ENTRY_KEY, Utility.ToByteArray(mGame));
            // Добавляем запись в таблицу game_table
            mResolver.insert(MyContentProvider.URI_GAME, cv);
            cv.clear();
            final int WAY_SIZE = 8;
            ArrayList<Long> waysId = new ArrayList<>(mWays.length);
            for(int i = 0; i < mWays.length; ++i)
            {
                ArrayList<Long> itemsId = new ArrayList<>(WAY_SIZE);
                // Добавляем записи в таблицу item_table (т.е. добавляем шашки/клетки текущей диагонали)
                for(int j = 0; j < mWays[i].size(); ++j) {
                    cv.put(ru.bstu.checkers.roomdb.Item.ENTRY_KEY, Utility.ToByteArray(mWays[i].get(j)));
                    long rowID = Long.parseLong((mResolver.insert(MyContentProvider.URI_ITEM, cv)).getLastPathSegment());
                    cv.clear();
                    itemsId.add(rowID);
                }
                while(itemsId.size() < WAY_SIZE) itemsId.add(0L);
                // Добавляем запись в таблицу way_table (т.е. текущую диагональ)
                cv.put(Way.ENTRY_KEY, Utility.ToByteArray(new Way(itemsId)));
                long rowID = Long.parseLong((mResolver.insert(MyContentProvider.URI_WAY, cv)).getLastPathSegment());
                cv.clear();
                waysId.add(rowID);
            }
            // Добавляем запись в таблицу position_table
            cv.put(Position.ENTRY_KEY, Utility.ToByteArray(new Position(mGame.mName, waysId)));
            mResolver.insert(MyContentProvider.URI_POSITION, cv);
            cv.clear();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Уведомляем курсор, что данные изменились по адресу URI_GAME
            mResolver.notifyChange(MyContentProvider.URI_GAME, null);
        }
    }

    private static void delete(String gameName, ContentResolver resolver) {
        // Удаляем запись из таблицы game_table
        int count = resolver.delete(Uri.parse(MyContentProvider.URI_GAME + "/" + gameName), null, null);
        // Получаем объект Position
        Cursor cursor = resolver.query(Uri.parse(MyContentProvider.URI_POSITION + "/" + gameName),
                null, null, null,null);
        cursor.moveToNext();
        // Получаем все waysId из Position
        List<Long> waysId = Position.GetWaysId(cursor);
        cursor.close();
        // Удаляем запись из таблицы position_table
        count = resolver.delete(Uri.parse(MyContentProvider.URI_POSITION + "/" + gameName), null, null);
        for(int i = 0; i < waysId.size(); ++i) {
            // Получаем объект Way
            cursor = resolver.query(Uri.parse(MyContentProvider.URI_WAY + "/" + waysId.get(i).toString()),
                    null, null, null,null);
            cursor.moveToNext();
            // Получаем все itemsId из Way
            List<Long> itemsId = Way.GetItemsId(cursor);
            cursor.close();
            // Удаляем все items для текущего Way
            for(int j = 0; j < itemsId.size(); ++j) {
                // Удаляем запись из таблицы item_table
                count = resolver.delete(Uri.parse(MyContentProvider.URI_ITEM + "/" + itemsId.get(j).toString()),
                        null, null);
            }
            // Удаляем запись из таблицы way_table
            count = resolver.delete(Uri.parse(MyContentProvider.URI_WAY + "/" + waysId.get(i).toString()),
                    null, null);
        }
    }

    public static void delete(ContentResolver resolver, String gameName) {
        new deleteAsyncTask(resolver, gameName).execute();
    }
    private static class deleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private ContentResolver mResolver;
        private String mGameName;
        deleteAsyncTask(ContentResolver resolver, String gameName) { mResolver = resolver; mGameName = gameName; }
        @Override
        protected Void doInBackground(final Void... params) {
            delete(mGameName, mResolver);
            return null;
        }
    }

    public static void load(String gameName) {
        new loadAsyncTask(gameName).execute();
    }
    private static class loadAsyncTask extends AsyncTask<Void, Void, Void> {
        private String mGameName;
        private GameDao mAsyncGameDao;
        private PositionDao mAsyncPositionDao;
        private WayDao mAsyncWayDao;
        private ItemDao mAsyncItemDao;
        private ArrayList<ru.bstu.checkers.Item>[] mWays;
        private Game mGame;
        loadAsyncTask(String gameName) {
            mGameName = gameName;
            mAsyncGameDao = MyRoomDatabase.getDatabase(MyApplication.getInstance()).gameDao();
            mAsyncPositionDao = MyRoomDatabase.getDatabase(MyApplication.getInstance()).positionDao();
            mAsyncWayDao = MyRoomDatabase.getDatabase(MyApplication.getInstance()).wayDao();
            mAsyncItemDao = MyRoomDatabase.getDatabase(MyApplication.getInstance()).itemDao();
            mWays = new ArrayList[ru.bstu.checkers.Item.WAYS_COUNT];
            for (int i = 0; i < mWays.length; ++i) // Максимум 8 клеток/шашек на одной диагонали
                mWays[i] = new ArrayList<ru.bstu.checkers.Item>(8);
        }
        @Override
        protected Void doInBackground(final Void... params) {
            // Получаем позицию (содержит id диагоналей в БД для выбранной игры)
            Position position = mAsyncPositionDao.getPosition_v2(mGameName);
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
                    // Один и тот же item на разных диагоналях должен быть одним и тем же объектом
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
            mGame = mAsyncGameDao.getGame_v2(mGameName);
            return null;
        }
        // После того как загрузили игру с БД, запускаем GameActivity
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