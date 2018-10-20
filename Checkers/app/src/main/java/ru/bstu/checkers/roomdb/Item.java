package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "item_table")
public class Item {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long mId;

    @ColumnInfo(name = "view_id")
    public int mViewId;
    @ColumnInfo(name = "type")
    public int mType;
    @ColumnInfo(name = "king")
    public boolean mKing;
    @ColumnInfo(name = "way1_idx")
    public int mWay1Idx;
    @ColumnInfo(name = "way2_idx")
    public int mWay2Idx;

    public Item() {}
}
