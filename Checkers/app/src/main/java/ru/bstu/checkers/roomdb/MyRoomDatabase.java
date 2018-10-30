package ru.bstu.checkers.roomdb;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.bstu.checkers.GameEngine;
import ru.bstu.checkers.MyApplication;
import ru.bstu.checkers.Utility;
import ru.bstu.checkers.Item.ITEM_TYPE;

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
    public static MyRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyRoomDatabase.class, "my_database")
                            .addCallback(sRoomDatabaseCallback)
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
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                }
                /** Инициализируем БД при ее создании позицией из файла INIT_DB_STATE.json */
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    try {
                        ArrayList<ru.bstu.checkers.Item>[] ways = GameEngine.GetDefaultPosition();
                        for(int i = 0; i < ways.length; ++i) {
                            for(int j = 0; j < ways[i].size(); ++j) {
                                // Убираем все шашки с доски
                                ways[i].get(j).type = ITEM_TYPE.square;
                            }
                        }
                        JSONObject json = Utility.ParseJSONData("INIT_DB_STATE.json");
                        String gameName  = json.getString("game_name");
                        int turn = ITEM_TYPE.valueOf(json.getString("turn")).ordinal();
                        JSONArray items = json.getJSONArray("items");
                        for(int i = 0; i < items.length(); ++i) {
                            JSONObject item = items.getJSONObject(i);
                            int way1_idx = item.getInt("way1");
                            int idx_in_way1 = item.getInt("idx_in_way1");
                            ways[way1_idx].get(idx_in_way1).type = ITEM_TYPE.valueOf(item.getString("type"));
                            ways[way1_idx].get(idx_in_way1).isKing = item.getBoolean("king");
                        }
                        MyRepository.insert(MyApplication.getCurrentActivity().getContentResolver(),
                                new Game(gameName, turn), Utility.CreateObjectForDatabaseInsert(ways));
                    }
                    catch (JSONException ex) { };
                }
            };
}
