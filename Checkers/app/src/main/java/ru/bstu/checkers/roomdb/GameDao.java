package ru.bstu.checkers.roomdb;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface GameDao {
    @Insert
    long insert(Game game);

    @Query("DELETE FROM game_table WHERE name = :gameName")
    int delete(String gameName);

    @Query("SELECT * from game_table WHERE name = :gameName")
    Game getGame(String gameName);

    @Query("SELECT * from game_table ORDER BY name ASC")
    LiveData<List<Game>> getAllGames();
}
