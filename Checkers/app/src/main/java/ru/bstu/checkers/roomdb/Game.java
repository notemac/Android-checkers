package ru.bstu.checkers.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = Game.TABLE_NAME)
public class Game implements Serializable {
    public static final String TABLE_NAME = "game_table";
    public static final String ENTRY_KEY = "game_entry";
    /** The name of the name column. */
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TURN_NAME = "turn";
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = COLUMN_NAME)
    public String mName;

    @ColumnInfo(name = COLUMN_TURN_NAME)
    public int mTurn;

    public Game(String name, int turn) { this.mName = name; this.mTurn = turn; }
}
