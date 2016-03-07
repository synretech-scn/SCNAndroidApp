package com.tribaltech.android.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.tribaltech.android.entities.Game;

public class DbController implements DbConstants {

    private DbHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;


    public DbController(Context ctx) {
        context = ctx;
    }

    public void open() {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertNewGame(Game game) {
        Long retId = null;
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME, game.userName);
        cv.put(CHECKOUT_ID, game.checkoutId);
        cv.put(CENTER_TYPE, game.centerType);
        cv.put(GAME_ID, game.gameId);
        cv.put(LANE_NUMBER, game.laneNumber);
        cv.put(CENTER_NAME, game.centerName);
        cv.put(SCREEN_NAME, game.screenName);
        cv.put(VENUE_ID, game.venueId);
        cv.put(PATTERN_NAME, game.patternName);
        cv.put(PATTERN_LENGTH, game.patternLength);
        cv.put(COMP_TYPE, game.compType);
        try {
            retId = database.insert(TABLE_NAME, null, cv);
        } catch (SQLiteConstraintException e) {
            retId = -2L;
        }
        return retId;
    }

    public void updateGame(String userName, String liveGameId) {
        ContentValues values = new ContentValues();
        values.put(LIVE_GAME_ID, liveGameId);
        database.update(TABLE_NAME, values, USER_NAME + "=?",
                new String[]{userName});
    }

    public Game getGameData(String userName) {
        Cursor cursor = null;
        Game game = null;
        try {
            cursor = database.rawQuery("SELECT * from " + TABLE_NAME
                    + " WHERE " + USER_NAME + "=?", new String[]{userName
                    + ""});

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                game = new Game(cursor.getString(cursor
                        .getColumnIndex(USER_NAME)), cursor.getString(cursor
                        .getColumnIndex(CHECKOUT_ID)), cursor.getString(cursor
                        .getColumnIndex(CENTER_TYPE)), cursor.getString(cursor
                        .getColumnIndex(GAME_ID)), cursor.getString(cursor
                        .getColumnIndex(LANE_NUMBER)), cursor.getString(cursor
                        .getColumnIndex(CENTER_NAME)), cursor.getString(cursor
                        .getColumnIndex(LIVE_GAME_ID)), cursor.getString(cursor
                        .getColumnIndex(SCREEN_NAME)), cursor.getInt(cursor
                        .getColumnIndex(VENUE_ID)), cursor.getString(cursor
                        .getColumnIndex(PATTERN_NAME)), cursor.getString(cursor
                        .getColumnIndex(PATTERN_LENGTH)),
                        cursor.getString(cursor.getColumnIndex(COMP_TYPE)));
            }
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return game;
    }

    public int deleteGame(String userName) {
        return database.delete(TABLE_NAME, USER_NAME + "=?", new String[]{userName});
    }

}
