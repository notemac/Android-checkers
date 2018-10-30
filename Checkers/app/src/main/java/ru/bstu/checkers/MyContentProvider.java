package ru.bstu.checkers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import ru.bstu.checkers.roomdb.Game;
import ru.bstu.checkers.roomdb.Item;
import ru.bstu.checkers.roomdb.MyRoomDatabase;
import ru.bstu.checkers.roomdb.Position;
import ru.bstu.checkers.roomdb.Way;

public class MyContentProvider extends ContentProvider {

    /** The authority of this content provider. */
    public static final String AUTHORITY = "ru.bstu.checkers.provider";
    /** The URI for the tables. */
    public static final Uri URI_GAME = Uri.parse("content://" + AUTHORITY + "/" + Game.TABLE_NAME);
    public static final Uri URI_POSITION = Uri.parse("content://" + AUTHORITY + "/" + Position.TABLE_NAME);
    public static final Uri URI_WAY = Uri.parse("content://" + AUTHORITY + "/" + Way.TABLE_NAME);
    public static final Uri URI_ITEM = Uri.parse("content://" + AUTHORITY + "/" + Item.TABLE_NAME);
    // Типы данных MIME
    // набор строк
    private static final String CONTENT_TYPE_DIR_GAME = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + Game.TABLE_NAME;
    private static final String CONTENT_TYPE_DIR_POSITION = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + Position.TABLE_NAME;
    private static final String CONTENT_TYPE_DIR_WAY = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + Way.TABLE_NAME;
    private static final String CONTENT_TYPE_DIR_ITEM = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + Item.TABLE_NAME;
    // одна строка
    private static final String CONTENT_TYPE_ITEM_GAME = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + Game.TABLE_NAME;
    private static final String CONTENT_TYPE_ITEM_POSITION = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + Position.TABLE_NAME;
    private static final String CONTENT_TYPE_ITEM_WAY = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + Way.TABLE_NAME;
    private static final String CONTENT_TYPE_ITEM_ITEM = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + Item.TABLE_NAME;
    // UriMatcher
    // общий Uri
    private static final int URI_DIR_GAME_CODE = 1;
    private static final int URI_DIR_POSITION_CODE = 2;
    private static final int URI_DIR_WAY_CODE = 3;
    private static final int URI_DIR_ITEM_CODE = 4;
    // Uri с указанным ID
    private static final int URI_ITEM_GAME_CODE = 5;
    private static final int URI_ITEM_POSITION_CODE = 6;
    private static final int URI_ITEM_WAY_CODE = 7;
    private static final int URI_ITEM_ITEM_CODE = 8;
    // описание и создание UriMatcher
    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        /*
         * Sets the integer value for multiple rows in table Game to URI_DIR_GAME_CODE.
         * Notice that no wildcard is used in the path
         */
        sUriMatcher.addURI(AUTHORITY, Game.TABLE_NAME, URI_DIR_GAME_CODE);
        sUriMatcher.addURI(AUTHORITY, Position.TABLE_NAME, URI_DIR_POSITION_CODE);
        sUriMatcher.addURI(AUTHORITY, Way.TABLE_NAME, URI_DIR_WAY_CODE);
        sUriMatcher.addURI(AUTHORITY, Item.TABLE_NAME, URI_DIR_ITEM_CODE);

        /*
         * Sets the code for a single row. In this case, the "#" wildcard is
         * used. "content://com.example.app.provider/table3/3" matches, but
         * "content://com.example.app.provider/table3 doesn't.
         */
        sUriMatcher.addURI(AUTHORITY, Game.TABLE_NAME + "/*", URI_ITEM_GAME_CODE); //доступ по gameName
        sUriMatcher.addURI(AUTHORITY, Position.TABLE_NAME + "/*", URI_ITEM_POSITION_CODE); //доступ по gameName
        sUriMatcher.addURI(AUTHORITY, Way.TABLE_NAME + "/#", URI_ITEM_WAY_CODE); //доступ по id
        sUriMatcher.addURI(AUTHORITY, Item.TABLE_NAME + "/#", URI_ITEM_ITEM_CODE); //доступ по id
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        MyRoomDatabase db;
        // проверяем Uri
        switch (sUriMatcher.match(uri)) {
            case URI_DIR_GAME_CODE:
                db = MyRoomDatabase.getDatabase(getContext());
                cursor = db.gameDao().getAllGames();
                /** register to watch a content URI for changes */
                cursor.setNotificationUri(getContext().getContentResolver(), URI_GAME);
                break;
            case URI_ITEM_GAME_CODE:
                db = MyRoomDatabase.getDatabase(getContext());
                cursor = db.gameDao().getGame(uri.getLastPathSegment());
                break;
            case URI_ITEM_POSITION_CODE:
                db = MyRoomDatabase.getDatabase(getContext());
                cursor = db.positionDao().getPosition(uri.getLastPathSegment());
                break;
            case URI_ITEM_WAY_CODE:
                db = MyRoomDatabase.getDatabase(getContext());
                cursor = db.wayDao().getWay(Long.parseLong(uri.getLastPathSegment()));
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        MyRoomDatabase db;
        Uri resultUri = URI_GAME; // default init for pressing warning
        long rowID = 0;
        // проверяем Uri
        switch (sUriMatcher.match(uri)) {
            case URI_DIR_GAME_CODE:
                db = MyRoomDatabase.getDatabase(getContext());
                rowID = db.gameDao().insert((Game)(Utility.FromByteArray(values.getAsByteArray(Game.ENTRY_KEY))));
                resultUri = ContentUris.withAppendedId(URI_GAME, rowID);
                break;
            case URI_DIR_POSITION_CODE:
                db = MyRoomDatabase.getDatabase(getContext());
                rowID = db.positionDao().insert((Position)(Utility.FromByteArray(values.getAsByteArray(Position.ENTRY_KEY))));
                resultUri = ContentUris.withAppendedId(URI_GAME, rowID);
                break;
            case URI_DIR_WAY_CODE:
                db = MyRoomDatabase.getDatabase(getContext());
                rowID = db.wayDao().insert((Way)(Utility.FromByteArray(values.getAsByteArray(Way.ENTRY_KEY))));
                resultUri = ContentUris.withAppendedId(URI_WAY, rowID);
                break;
            case URI_DIR_ITEM_CODE:
                db = MyRoomDatabase.getDatabase(getContext());
                rowID = db.itemDao().insert((Item)(Utility.FromByteArray(values.getAsByteArray(Item.ENTRY_KEY))));
                resultUri = ContentUris.withAppendedId(URI_ITEM, rowID);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        // getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int update( @NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri))
        {
            case URI_ITEM_GAME_CODE:
                //Update game name in game_table and position_table
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        MyRoomDatabase db;
        switch (sUriMatcher.match(uri)) {
            case URI_ITEM_GAME_CODE: // Uri с gameName
                db = MyRoomDatabase.getDatabase(getContext());
                count = db.gameDao().delete(uri.getLastPathSegment());
                // Уведомляем курсор, что данные изменились по адресу URI_GAME
                getContext().getContentResolver().notifyChange(URI_GAME, null);
                break;
            case URI_ITEM_POSITION_CODE: // Uri с gameName
                db = MyRoomDatabase.getDatabase(getContext());
                count = db.positionDao().delete(uri.getLastPathSegment());
                break;
            case URI_ITEM_ITEM_CODE: // Uri с ID
                db = MyRoomDatabase.getDatabase(getContext());
                count = db.itemDao().delete(Long.parseLong(uri.getLastPathSegment()));
                break;
            case URI_ITEM_WAY_CODE: // Uri с ID
                db = MyRoomDatabase.getDatabase(getContext());
                count = db.wayDao().delete(Long.parseLong(uri.getLastPathSegment()));
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return count;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case URI_DIR_GAME_CODE:
                return CONTENT_TYPE_DIR_GAME;
            case URI_DIR_POSITION_CODE:
                return CONTENT_TYPE_DIR_POSITION;
            case URI_DIR_WAY_CODE:
                return CONTENT_TYPE_DIR_WAY;
            case URI_DIR_ITEM_CODE:
                return CONTENT_TYPE_DIR_ITEM;
            case URI_ITEM_GAME_CODE:
                return CONTENT_TYPE_ITEM_GAME;
            case URI_ITEM_POSITION_CODE:
                return CONTENT_TYPE_ITEM_POSITION;
            case URI_ITEM_WAY_CODE:
                return CONTENT_TYPE_ITEM_WAY;
            case URI_ITEM_ITEM_CODE:
                return CONTENT_TYPE_ITEM_ITEM;
        }
        return null;
    }
}
