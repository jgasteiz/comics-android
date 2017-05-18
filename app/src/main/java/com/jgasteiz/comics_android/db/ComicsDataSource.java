package com.jgasteiz.comics_android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jgasteiz.comics_android.models.Comic;
import com.jgasteiz.comics_android.models.Series;


public class ComicsDataSource {

    private SQLiteDatabase mDatabase;       // The actual DB!
    private ComicsHelper mComicsHelper; // Helper class for creating and opening the DB

    public ComicsDataSource(Context context) {
        mComicsHelper = new ComicsHelper(context);
    }

    /*
     * Open the db. Will create if it doesn't exist
     */
    public void open() throws SQLException {
        mDatabase = mComicsHelper.getWritableDatabase();
    }

    /*
     * We always need to close our db connections
     */
    public void close() {
        mDatabase.close();
    }

    /*
     * CRUD operations!
     */

    /*
     * INSERT
     */
    public void insertSeries (Series series) {
        mDatabase.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(ComicsHelper.COLUMN_EXTERNAL_ID, series.getId());
            values.put(ComicsHelper.COLUMN_TITLE, series.getTitle());
            values.put(ComicsHelper.COLUMN_AUTHOR, series.getAuthor());
            values.put(ComicsHelper.COLUMN_YEAR, series.getYear());
            mDatabase.insert(ComicsHelper.TABLE_SERIES, null, values);
            mDatabase.setTransactionSuccessful();
        }
        finally {
            mDatabase.endTransaction();
        }
    }

    public void insertComic (Comic comic) {
        mDatabase.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(ComicsHelper.COLUMN_EXTERNAL_ID, comic.getId());
            values.put(ComicsHelper.COLUMN_TITLE, comic.getTitle());
            values.put(ComicsHelper.COLUMN_PAGES, comic.getSerializedPages());
            values.put(ComicsHelper.COLUMN_SERIES, comic.getSeries().getId());
            mDatabase.insert(ComicsHelper.TABLE_COMICS, null, values);
            mDatabase.setTransactionSuccessful();
        }
        finally {
            mDatabase.endTransaction();
        }
    }

    /*
     * SELECT ALL series
     */
    public Cursor selectAllSeries () {
        return mDatabase.query(
                ComicsHelper.TABLE_SERIES, // table
                new String[] { ComicsHelper.COLUMN_EXTERNAL_ID, ComicsHelper.COLUMN_TITLE, ComicsHelper.COLUMN_AUTHOR, ComicsHelper.COLUMN_YEAR }, // column names
                null, // where clause
                null, // where params
                null, // groupby
                null, // having
                null  // orderby
        );
    }

    /*
     * SELECT Series comics
     */
    public Cursor selectSeriesComics (Series series) {
        return mDatabase.query(
                ComicsHelper.TABLE_COMICS, // table
                new String[] { ComicsHelper.COLUMN_EXTERNAL_ID, ComicsHelper.COLUMN_TITLE, ComicsHelper.COLUMN_PAGES }, // column names
                ComicsHelper.COLUMN_SERIES + " = ?", // where clause
                new String[] {String.valueOf(series.getId())}, // where params
                null, // groupby
                null, // having
                null  // orderby
        );
    }


    /*
     * DELETE
     */
    public void deleteAllSeries() {
        mDatabase.delete(
                ComicsHelper.TABLE_SERIES, // table
                null, // where clause
                null  // where params
        );
    }
    public void deleteAllSeriesComics(Series series) {
        mDatabase.delete(
                ComicsHelper.TABLE_COMICS, // table
                ComicsHelper.COLUMN_SERIES + " = ?", // where clause
                new String[] {String.valueOf(series.getId())} // where params
        );
    }
}
