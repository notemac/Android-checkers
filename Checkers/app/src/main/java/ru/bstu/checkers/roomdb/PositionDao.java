package ru.bstu.checkers.roomdb;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

@Dao
public interface PositionDao {
    @Insert
    long insert(Position position);

    @Query("DELETE FROM position_table WHERE game_name = :gameName")
    int delete(String gameName);

    @Query("SELECT * from position_table WHERE game_name = :gameName")
    Position getPosition_v2(String gameName);

    @Query("SELECT * from position_table WHERE game_name = :gameName")
    Cursor getPosition(String gameName);
}
