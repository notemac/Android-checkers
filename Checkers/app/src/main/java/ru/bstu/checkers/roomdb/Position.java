package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = Position.TABLE_NAME)
public class Position implements Serializable {
    public static final String TABLE_NAME = "position_table";
    public static final String ENTRY_KEY = "position_entry";
    public static final String COLUMN_GAME_NAME = "game_name";
    public static final String COLUMN_WAY1_ID_NAME = "way1_id";
    public static final String COLUMN_WAY2_ID_NAME = "way2_id";
    public static final String COLUMN_WAY3_ID_NAME = "way3_id";
    public static final String COLUMN_WAY4_ID_NAME = "way4_id";
    public static final String COLUMN_WAY5_ID_NAME = "way5_id";
    public static final String COLUMN_WAY6_ID_NAME = "way6_id";
    public static final String COLUMN_WAY7_ID_NAME = "way7_id";
    public static final String COLUMN_WAY8_ID_NAME = "way8_id";
    public static final String COLUMN_WAY9_ID_NAME = "way9_id";
    public static final String COLUMN_WAY10_ID_NAME = "way10_id";
    public static final String COLUMN_WAY11_ID_NAME = "way11_id";
    public static final String COLUMN_WAY12_ID_NAME = "way12_id";
    public static final String COLUMN_WAY13_ID_NAME = "way13_id";
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = COLUMN_GAME_NAME)
    public String mGameName;

    @ColumnInfo(name = COLUMN_WAY1_ID_NAME)
    public long mWay1Id;
    @ColumnInfo(name = COLUMN_WAY2_ID_NAME)
    public long mWay2Id;
    @ColumnInfo(name = COLUMN_WAY3_ID_NAME)
    public long mWay3Id;
    @ColumnInfo(name = COLUMN_WAY4_ID_NAME)
    public long mWay4Id;
    @ColumnInfo(name = COLUMN_WAY5_ID_NAME)
    public long mWay5Id;
    @ColumnInfo(name = COLUMN_WAY6_ID_NAME)
    public long mWay6Id;
    @ColumnInfo(name = COLUMN_WAY7_ID_NAME)
    public long mWay7Id;
    @ColumnInfo(name = COLUMN_WAY8_ID_NAME)
    public long mWay8Id;
    @ColumnInfo(name = COLUMN_WAY9_ID_NAME)
    public long mWay9Id;
    @ColumnInfo(name = COLUMN_WAY10_ID_NAME)
    public long mWay10Id;
    @ColumnInfo(name = COLUMN_WAY11_ID_NAME)
    public long mWay11Id;
    @ColumnInfo(name = COLUMN_WAY12_ID_NAME)
    public long mWay12Id;
    @ColumnInfo(name = COLUMN_WAY13_ID_NAME)
    public long mWay13Id;

    public Position() {}
    public Position(@NonNull String gameName, ArrayList<Long> waysId) {
        mGameName = gameName;
        mWay1Id = waysId.get(0);
        mWay2Id = waysId.get(1);
        mWay3Id = waysId.get(2);
        mWay4Id = waysId.get(3);
        mWay5Id = waysId.get(4);
        mWay6Id = waysId.get(5);
        mWay7Id = waysId.get(6);
        mWay8Id = waysId.get(7);
        mWay9Id = waysId.get(8);
        mWay10Id = waysId.get(9);
        mWay11Id = waysId.get(10);
        mWay12Id = waysId.get(11);
        mWay13Id = waysId.get(12);
    }

    public List<Long> GetWaysId()
    {
        return Arrays.asList(mWay1Id, mWay2Id, mWay3Id, mWay4Id, mWay5Id, mWay6Id,
                mWay7Id, mWay8Id, mWay9Id, mWay10Id, mWay11Id, mWay12Id, mWay13Id);
    }

    public static List<Long> GetWaysId(Cursor cursor) {
        return Arrays.asList(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY1_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY2_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY3_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY4_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY5_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY6_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY7_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY8_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY9_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY10_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY11_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY12_ID_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAY13_ID_NAME)));
    }
}
