package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ItemDao {
    @Insert
    long insert(Item item);

    @Query("DELETE FROM item_table WHERE id = :id")
    void delete(long id);

    @Query("DELETE FROM item_table WHERE id in (:ids)")
    void delete(List<Long> ids);
}
