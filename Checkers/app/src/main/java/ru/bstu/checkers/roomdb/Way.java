package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "way_table")
public class Way {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long mId;

    @ColumnInfo(name = "item1_id")
    public long mItem1Id;
    @ColumnInfo(name = "item2_id")
    public long mItem2Id;
    @ColumnInfo(name = "item3_id")
    public long mItem3Id;
    @ColumnInfo(name = "item4_id")
    public long mItem4Id;
    @ColumnInfo(name = "item5_id")
    public long mItem5Id;
    @ColumnInfo(name = "item6_id")
    public long mItem6Id;
    @ColumnInfo(name = "item7_id")
    public long mItem7Id;
    @ColumnInfo(name = "item8_id")
    public long mItem8Id;

    public Way() {}
}
