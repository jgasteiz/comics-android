package com.jgasteiz.comics_android.helpers


import android.content.Context
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.jgasteiz.comics_android.db.ComicsDataSource
import com.jgasteiz.comics_android.db.ComicsHelper
import com.jgasteiz.comics_android.interfaces.OnComicsFetched
import com.jgasteiz.comics_android.interfaces.OnResponseFetched
import com.jgasteiz.comics_android.interfaces.OnSeriesFetched
import com.jgasteiz.comics_android.models.Comic
import com.jgasteiz.comics_android.models.Series
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.util.ArrayList
import java.util.Collections

object Utils {

    private val LOG_TAG = Utils::class.java.simpleName

    private val STR_SEPARATOR = "__,__"

    private val SERIES_URL = "http://comics.jgasteiz.com/api/series/"
    private val MOCK_SERIES_URL = String.format("http://%s:8000/api/series/", Secret.MOCK_SERVER_IP_ADDRESS)

    fun convertArrayToString(array: ArrayList<String>): String {
        val str = StringBuilder()
        for (i in array.indices) {
            str.append(array[i])
            // Do not append comma at the end of last element
            if (i < array.size - 1) {
                str.append(STR_SEPARATOR)
            }
        }
        return str.toString()
    }

    fun convertStringToArray(str: String): ArrayList<String> {
        val pages = str.split(STR_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val pageList = ArrayList<String>()
        Collections.addAll(pageList, *pages)
        return pageList
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /**
     * Fetch all the series from the API and store them in the DB.
     * @param onSeriesFetched callback
     */
    fun fetchSeries(context: Context, onSeriesFetched: OnSeriesFetched) {
        val comicsDataSource = ComicsDataSource(context)

        val task = GetStringResponseAsyncTask(object : OnResponseFetched {
            override fun callback(response: String) {
                Log.d(LOG_TAG, response)
                try {
                    val jsonArray = JSONArray(response)
                    comicsDataSource.open()
                    comicsDataSource.deleteAllSeries()
                    for (i in 0..jsonArray.length() - 1) {
                        val jsonObject = jsonArray.get(i) as JSONObject
                        comicsDataSource.insertSeries(Series(jsonObject))
                    }
                } catch (e: JSONException) {
                    Log.e(LOG_TAG, e.message)
                }

                comicsDataSource.close()
                onSeriesFetched.callback()
            }
        })
        if (!Secret.MOCK_ENABLED) {
            task.execute(String.format("%s?token=%s", SERIES_URL, Secret.API_TOKEN))
        } else {
            task.execute(String.format("%s?token=%s", MOCK_SERIES_URL, Secret.MOCK_API_TOKEN))
        }
    }

    /**
     * Fetch a given series' comics from the API and store them in the DB.
     * @param series Series instance
     * *
     * @param onComicsFetched callback
     */
    fun fetchSeriesComics(context: Context, series: Series, onComicsFetched: OnComicsFetched) {
        val comicsDataSource = ComicsDataSource(context)

        val task = GetStringResponseAsyncTask(object : OnResponseFetched {
            override fun callback(response: String) {
                Log.d(LOG_TAG, response)
                try {
                    val jsonSeriesDetail = JSONObject(response)
                    val jsonArray = jsonSeriesDetail.get("comics") as JSONArray
                    comicsDataSource.open()
                    comicsDataSource.deleteAllSeriesComics(series)
                    for (i in 0..jsonArray.length() - 1) {
                        val jsonObject = jsonArray.get(i) as JSONObject
                        comicsDataSource.insertComic(Comic(series, jsonObject))
                    }
                } catch (e: JSONException) {
                    Log.e(LOG_TAG, e.message)
                }

                comicsDataSource.close()
                onComicsFetched.callback()
            }
        })
        if (!Secret.MOCK_ENABLED) {
            task.execute(String.format("%s%s?token=%s", SERIES_URL, series.id, Secret.API_TOKEN))
        } else {
            task.execute(String.format("%s%s?token=%s", MOCK_SERIES_URL, series.id, Secret.MOCK_API_TOKEN))
        }
    }

    /**
     * Get all the series from the DB.
     * @return array list of Series instances
     */
    fun getSeries(context: Context): ArrayList<Series> {
        val comicsDataSource = ComicsDataSource(context)
        val seriesList = ArrayList<Series>()

        comicsDataSource.open()
        val cursor = comicsDataSource.selectAllSeries()

        while (cursor.moveToNext()) {
            val idColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_EXTERNAL_ID)
            val titleColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_TITLE)
            val authorColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_AUTHOR)
            val yearColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_YEAR)
            seriesList.add(Series(
                    cursor.getInt(idColumnIndex),
                    cursor.getString(titleColumnIndex),
                    cursor.getString(authorColumnIndex),
                    cursor.getString(yearColumnIndex)
            ))
        }
        cursor.close()
        comicsDataSource.close()

        return seriesList
    }

    /**
     * Get a given series' comics from the DB.
     * @param series Series instance
     * *
     * @return array list of Comic instances
     */
    fun getSeriesComics(context: Context, series: Series): ArrayList<Comic> {
        val comicsDataSource = ComicsDataSource(context)
        val comicList = ArrayList<Comic>()

        comicsDataSource.open()
        val cursor = comicsDataSource.selectSeriesComics(series)

        while (cursor.moveToNext()) {
            val idColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_EXTERNAL_ID)
            val titleColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_TITLE)
            val pagesColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_PAGES)
            comicList.add(Comic(
                    cursor.getInt(idColumnIndex),
                    cursor.getString(titleColumnIndex),
                    cursor.getString(pagesColumnIndex),
                    series
            ))
        }
        cursor.close()
        comicsDataSource.close()

        return comicList
    }

    /**
     * Delete the downloaded comic pages.
     * @param comic Comic instance
     */
    fun removeComicDownload(context: Context, comic: Comic) {
        val comicDirectory = comic.getComicDirectory(context)
        for (file in comicDirectory.listFiles()) {
            val result = file.delete()
            if (result) {
                Log.d(LOG_TAG, "Comic page deleted")
            }
        }
    }
}
