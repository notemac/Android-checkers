package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "position_table")
public class Position {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "game_name")
    public String mGameName;

    @ColumnInfo(name = "way1_id")
    public long mWay1Id;
    @ColumnInfo(name = "way2_id")
    public long mWay2Id;
    @ColumnInfo(name = "way3_id")
    public long mWay3Id;
    @ColumnInfo(name = "way4_id")
    public long mWay4Id;
    @ColumnInfo(name = "way5_id")
    public long mWay5Id;
    @ColumnInfo(name = "way6_id")
    public long mWay6Id;
    @ColumnInfo(name = "way7_id")
    public long mWay7Id;
    @ColumnInfo(name = "way8_id")
    public long mWay8Id;
    @ColumnInfo(name = "way9_id")
    public long mWay9Id;
    @ColumnInfo(name = "way10_id")
    public long mWay10Id;
    @ColumnInfo(name = "way11_id")
    public long mWay11Id;
    @ColumnInfo(name = "way12_id")
    public long mWay12Id;
    @ColumnInfo(name = "way13_id")
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
}
