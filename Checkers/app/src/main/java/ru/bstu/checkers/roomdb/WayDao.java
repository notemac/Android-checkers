package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface WayDao {
    @Insert
    long insert(Way way);

    @Query("DELETE FROM way_table WHERE id = :id")
    int delete(long id);

    @Query("DELETE FROM way_table WHERE id in (:ids)")
    int delete(List<Long> ids);

    @Query("SELECT * FROM way_table WHERE id in (:ids)")
    List<Way> getWays(List<Long> ids);

    @Query("SELECT * FROM way_table WHERE id = :id")
    Cursor getWay(long id);
}
