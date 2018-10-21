package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "game_table")
public class Game {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    public String mName;

    @ColumnInfo(name = "turn")
    public int mTurn;

    public Game(String name, int turn) { this.mName = name; this.mTurn = turn; }
}
