package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = Item.TABLE_NAME)
public class Item implements Serializable {
    public static final String TABLE_NAME = "item_table";
    public static final String COLUMN_ID_NAME = "id";
    public static final String ENTRY_KEY = "item_entry";
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID_NAME)
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
