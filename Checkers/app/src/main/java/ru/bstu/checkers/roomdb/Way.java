package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
}
