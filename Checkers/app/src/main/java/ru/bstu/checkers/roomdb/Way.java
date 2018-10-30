package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.database.Cursor;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = Way.TABLE_NAME)
public class Way implements Serializable {
    public static final String TABLE_NAME = "way_table";
    public static final String ENTRY_KEY = "way_entry";
    public static final String COLUMN_ID_NAME = "id";
    public static final String COLUMN_ITEM1_ID_NAME = "item1_id";
    public static final String COLUMN_ITEM2_ID_NAME = "item2_id";
    public static final String COLUMN_ITEM3_ID_NAME = "item3_id";
    public static final String COLUMN_ITEM4_ID_NAME = "item4_id";
    public static final String COLUMN_ITEM5_ID_NAME = "item5_id";
    public static final String COLUMN_ITEM6_ID_NAME = "item6_id";
    public static final String COLUMN_ITEM7_ID_NAME = "item7_id";
    public static final String COLUMN_ITEM8_ID_NAME = "item8_id";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID_NAME)
    public long mId;

    @ColumnInfo(name = COLUMN_ITEM1_ID_NAME)
    public long mItem1Id;
    @ColumnInfo(name = COLUMN_ITEM2_ID_NAME)
    public long mItem2Id;
    @ColumnInfo(name = COLUMN_ITEM3_ID_NAME)
    public long mItem3Id;
    @ColumnInfo(name = COLUMN_ITEM4_ID_NAME)
    public long mItem4Id;
    @ColumnInfo(name = COLUMN_ITEM5_ID_NAME)
    public long mItem5Id;
    @ColumnInfo(name = COLUMN_ITEM6_ID_NAME)
    public long mItem6Id;
    @ColumnInfo(name = COLUMN_ITEM7_ID_NAME)
    public long mItem7Id;
    @ColumnInfo(name = COLUMN_ITEM8_ID_NAME)
    public long mItem8Id;

    public Way() {}
    public Way(ArrayList<Long> itemsId) {
        mItem1Id = itemsId.get(0);
        mItem2Id = itemsId.get(1);
        mItem3Id = itemsId.get(2);
        mItem4Id = itemsId.get(3);
        mItem5Id = itemsId.get(4);
        mItem6Id = itemsId.get(5);
        mItem7Id = itemsId.get(6);
        mItem8Id = itemsId.get(7);
    }
    public List<Long> GetItemsId() {
        return Arrays.asList(mItem1Id, mItem2Id, mItem3Id, mItem4Id, mItem5Id, mItem6Id, mItem7Id, mItem8Id);
    }

    public static List<Long> GetItemsId(Cursor cursor) {
        return Arrays.asList(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM1_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM2_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM3_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM4_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM5_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM6_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM7_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM8_ID_NAME)));
    }
}
