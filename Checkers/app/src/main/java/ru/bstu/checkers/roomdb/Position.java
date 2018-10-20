package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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
}
