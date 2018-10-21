package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

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
    public Item(int viewId, int type, boolean king, ArrayList<Integer> idx) {
        this.mViewId = viewId;
        this.mType = type;
        this.mKing = king;
        this.mWay1Idx = idx.get(0);
        this.mWay2Idx = (2 == idx.size()) ? idx.get(1) : -1;
    }
}
