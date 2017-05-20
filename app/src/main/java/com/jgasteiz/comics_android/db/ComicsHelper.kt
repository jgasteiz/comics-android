package com.jgasteiz.comics_android.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class ComicsHelper(context: Context) : SQLiteOpenHelper(context, ComicsHelper.DB_NAME, null, ComicsHelper.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_SERIES)
        db.execSQL(CREATE_TABLE_COMICS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {

        /*
     * Table and column information
     */
        val TABLE_SERIES = "SERIES"
        val TABLE_COMICS = "COMICS"
        val COLUMN_ID = "_ID"
        val COLUMN_EXTERNAL_ID = "EXTERNAL_ID"
        val COLUMN_TITLE = "TITLE"
        val COLUMN_AUTHOR = "AUTHOR"
        val COLUMN_YEAR = "YEAR"
        val COLUMN_PAGES = "PAGES"
        val COLUMN_SERIES = "SERIES_ID"

        /*
     * Database information
     */
        private val DB_NAME = "comics.db"
        private val DB_VERSION = 1 // Must increment to trigger an upgrade
        private val CREATE_TABLE_SERIES =
                "CREATE TABLE " + TABLE_SERIES + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_EXTERNAL_ID + " INTEGER, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_AUTHOR + " TEXT, " +
                        COLUMN_YEAR + " TEXT);"
        private val CREATE_TABLE_COMICS =
                "CREATE TABLE " + TABLE_COMICS + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_SERIES + " INTEGER, " +
                        COLUMN_EXTERNAL_ID + " INTEGER, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_PAGES + " TEXT);"
    }
}

