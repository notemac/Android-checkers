package ru.bstu.checkers.roomdb;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * Annotate the class to be a Room database, declare the entities that belong in the database
 * and set the version number. Listing the entities will create tables in the database.
 */
@Database(entities = {Game.class, Position.class, Way.class, Item.class}, version = 1)
public abstract class MyRoomDatabase extends RoomDatabase {
    //Define the DAOs that work with the database. Provide an abstract "getter" method for each @Dao.
    public abstract GameDao gameDao();
    public abstract PositionDao positionDao();
    public abstract WayDao wayDao();
    public abstract ItemDao itemDao();

    /**
     * Make the MyRoomDatabase a singleton to prevent having multiple instances of the database opened at the same time.
     * */
    private static volatile MyRoomDatabase INSTANCE;
    static MyRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyRoomDatabase.class, "my_database")
                            .build(); //.addCallback(sRoomDatabaseCallback).build()
                }
            }
        }
        return INSTANCE;
    }

    /**
     * There is no data in the database. You will add data in two ways: Add some data when the database is opened,
     * and add an Activity for adding words.
     * To delete all content and repopulate the database whenever the app is started,
     * you create a RoomDatabase.Callback and override onOpen(). Because you cannot do Room
     * database operations on the UI thread, onOpen() creates and executes an AsyncTask to add content to the database.
     */
    /*private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final GameDao mDao;

        PopulateDbAsync(MyRoomDatabase db) {
            mDao = db.gameDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //mDao.deleteAll();
            //Word word = new Word("Hello");
            //mDao.insert(word);
            return null;
        }
    }*/
}
