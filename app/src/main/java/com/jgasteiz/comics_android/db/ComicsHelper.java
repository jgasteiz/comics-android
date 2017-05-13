package com.jgasteiz.comics_android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ComicsHelper extends SQLiteOpenHelper {

    /*
     * Table and column information
     */
    public static final String TABLE_SERIES = "SERIES";
    public static final String TABLE_COMICS = "COMICS";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_EXTERNAL_ID = "EXTERNAL_ID";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_AUTHOR = "AUTHOR";
    public static final String COLUMN_YEAR = "YEAR";
    public static final String COLUMN_PAGES = "PAGES";
    public static final String COLUMN_SERIES = "SERIES_ID";

    /*
     * Database information
     */
    private static final String DB_NAME = "comics.db";
    private static final int DB_VERSION = 1; // Must increment to trigger an upgrade
    private static final String CREATE_TABLE_SERIES =
            "CREATE TABLE " + TABLE_SERIES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EXTERNAL_ID + " INTEGER, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_AUTHOR + " TEXT, " +
                    COLUMN_YEAR + " TEXT);";
    private static final String CREATE_TABLE_COMICS =
            "CREATE TABLE " + TABLE_COMICS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SERIES + " INTEGER, " +
                    COLUMN_EXTERNAL_ID + " INTEGER, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_PAGES + " TEXT);";

    public ComicsHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SERIES);
        db.execSQL(CREATE_TABLE_COMICS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}

