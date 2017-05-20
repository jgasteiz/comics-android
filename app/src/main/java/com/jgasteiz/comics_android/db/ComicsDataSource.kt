package com.jgasteiz.comics_android.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

import com.jgasteiz.comics_android.models.Comic
import com.jgasteiz.comics_android.models.Series


class ComicsDataSource(context: Context) {

    private var mDatabase: SQLiteDatabase? = null       // The actual DB!
    private val mComicsHelper: ComicsHelper // Helper class for creating and opening the DB

    init {
        mComicsHelper = ComicsHelper(context)
    }

    /*
     * Open the db. Will create if it doesn't exist
     */
    @Throws(SQLException::class)
    fun open() {
        mDatabase = mComicsHelper.writableDatabase
    }

    /*
     * We always need to close our db connections
     */
    fun close() {
        mDatabase!!.close()
    }

    /*
     * CRUD operations!
     */

    /*
     * INSERT
     */
    fun insertSeries(series: Series) {
        mDatabase!!.beginTransaction()

        try {
            val values = ContentValues()
            values.put(ComicsHelper.COLUMN_EXTERNAL_ID, series.id)
            values.put(ComicsHelper.COLUMN_TITLE, series.title)
            values.put(ComicsHelper.COLUMN_AUTHOR, series.author)
            values.put(ComicsHelper.COLUMN_YEAR, series.year)
            mDatabase!!.insert(ComicsHelper.TABLE_SERIES, // where clause
                    null  // where params
                    , values)
            mDatabase!!.setTransactionSuccessful()
        } finally {
            mDatabase!!.endTransaction()
        }
    }

    fun insertComic(comic: Comic) {
        mDatabase!!.beginTransaction()

        try {
            val values = ContentValues()
            values.put(ComicsHelper.COLUMN_EXTERNAL_ID, comic.id)
            values.put(ComicsHelper.COLUMN_TITLE, comic.title)
            values.put(ComicsHelper.COLUMN_PAGES, comic.serializedPages)
            values.put(ComicsHelper.COLUMN_SERIES, comic.series!!.id)
            mDatabase!!.insert(ComicsHelper.TABLE_COMICS, null, values)
            mDatabase!!.setTransactionSuccessful()
        } finally {
            mDatabase!!.endTransaction()
        }
    }

    /*
     * SELECT ALL series
     */
    fun selectAllSeries(): Cursor {
        return mDatabase!!.query(
                ComicsHelper.TABLE_SERIES, // table
                arrayOf(ComicsHelper.COLUMN_EXTERNAL_ID, ComicsHelper.COLUMN_TITLE, ComicsHelper.COLUMN_AUTHOR, ComicsHelper.COLUMN_YEAR), null, null, null, null, null// orderby
        )// column names
        // where clause
        // where params
        // groupby
        // having
    }

    /*
     * SELECT Series comics
     */
    fun selectSeriesComics(series: Series): Cursor {
        return mDatabase!!.query(
                ComicsHelper.TABLE_COMICS, // table
                arrayOf(ComicsHelper.COLUMN_EXTERNAL_ID, ComicsHelper.COLUMN_TITLE, ComicsHelper.COLUMN_PAGES), // column names
                ComicsHelper.COLUMN_SERIES + " = ?", // where clause
                arrayOf(series.id.toString()), null, null, null// orderby
        )// where params
        // groupby
        // having
    }


    /*
     * DELETE
     */
    fun deleteAllSeries() {
        mDatabase!!.delete(
                ComicsHelper.TABLE_SERIES, null, null
        )// table
    }

    fun deleteAllSeriesComics(series: Series) {
        mDatabase!!.delete(
                ComicsHelper.TABLE_COMICS, // table
                ComicsHelper.COLUMN_SERIES + " = ?", // where clause
                arrayOf(series.id.toString()) // where params
        )
    }
}
