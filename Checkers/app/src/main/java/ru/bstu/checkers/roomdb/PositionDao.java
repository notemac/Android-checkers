package ru.bstu.checkers.roomdb;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PositionDao {
    @Insert
    long insert(Position position);

    @Query("DELETE FROM position_table WHERE game_name = :gameName")
    void delete(String gameName);
}
