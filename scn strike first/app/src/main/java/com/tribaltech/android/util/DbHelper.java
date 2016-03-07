package com.tribaltech.android.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper implements DbConstants {

    public DbHelper(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + USER_NAME
                + " TEXT PRIMARY KEY, " + CHECKOUT_ID + " TEXT NOT NULL, "
                + CENTER_TYPE + " TEXT NOT NULL, " + GAME_ID
                + " TEXT NOT NULL, " + LANE_NUMBER + " TEXT NOT NULL, "
                + CENTER_NAME + " TEXT NOT NULL, " + LIVE_GAME_ID
                + " TEXT DEFAULT '', " + SCREEN_NAME + " TEXT, " + VENUE_ID
                + " INT, " + PATTERN_NAME + " TEXT, " + PATTERN_LENGTH
                + " TEXT, " + COMP_TYPE + " TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
