package com.jgasteiz.comics_android.helpers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.jgasteiz.comics_android.db.ComicsDataSource;
import com.jgasteiz.comics_android.db.ComicsHelper;
import com.jgasteiz.comics_android.interfaces.*;
import com.jgasteiz.comics_android.models.Comic;
import com.jgasteiz.comics_android.models.Series;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ComicsController {

    private static final String LOG_TAG = ComicsController.class.getSimpleName();
    private static final String SERIES_URL = "http://comics.jgasteiz.com/api/series/";
    private static final String MOCK_SERIES_URL = String.format("http://%s:8000/api/series/", Secret.MOCK_SERVER_IP_ADDRESS);

    private Context mContext;
    private ComicsDataSource mComicsDataSource;

    public ComicsController(Context context) {
        mContext = context;
        mComicsDataSource = new ComicsDataSource(mContext);
    }

    /**
     * Fetch all the series from the API and store them in the DB.
     * @param onSeriesFetched callback
     */
    public void fetchSeries (final OnSeriesFetched onSeriesFetched) {
        final GetStringResponseAsyncTask task = new GetStringResponseAsyncTask(new OnResponseFetched() {
            @Override
            public void callback(String response) {
                Log.d(LOG_TAG, response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    mComicsDataSource.open();
                    mComicsDataSource.deleteAllSeries();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        mComicsDataSource.insertSeries(new Series(jsonObject));
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                mComicsDataSource.close();
                onSeriesFetched.callback();
            }
        });
        if (!Secret.MOCK_ENABLED) {
            task.execute(String.format("%s?token=%s", SERIES_URL, Secret.API_TOKEN));
        } else {
            task.execute(String.format("%s?token=%s", MOCK_SERIES_URL, Secret.MOCK_API_TOKEN));
        }
    }

    /**
     * Fetch a given series' comics from the API and store them in the DB.
     * @param series Series instance
     * @param onComicsFetched callback
     */
    public void fetchSeriesComics (final Series series, final OnComicsFetched onComicsFetched) {
        final GetStringResponseAsyncTask task = new GetStringResponseAsyncTask(new OnResponseFetched() {
            @Override
            public void callback(String response) {
                Log.d(LOG_TAG, response);
                try {
                    JSONObject jsonSeriesDetail = new JSONObject(response);
                    JSONArray jsonArray = (JSONArray) jsonSeriesDetail.get("comics");
                    mComicsDataSource.open();
                    mComicsDataSource.deleteAllSeriesComics(series);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        mComicsDataSource.insertComic(new Comic(series, jsonObject));
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                mComicsDataSource.close();
                onComicsFetched.callback();
            }
        });
        if (!Secret.MOCK_ENABLED) {
            task.execute(String.format("%s%s?token=%s", SERIES_URL, series.getId(), Secret.API_TOKEN));
        } else {
            task.execute(String.format("%s%s?token=%s", MOCK_SERIES_URL, series.getId(), Secret.MOCK_API_TOKEN));
        }
    }

    /**
     * Get all the series from the DB.
     * @return array list of Series instances
     */
    public ArrayList<Series> getSeries () {
        ArrayList<Series> seriesList = new ArrayList<>();

        mComicsDataSource.open();
        Cursor cursor = mComicsDataSource.selectAllSeries();

        while (cursor.moveToNext()) {
            int idColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_EXTERNAL_ID);
            int titleColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_AUTHOR);
            int yearColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_YEAR);
            seriesList.add(new Series(
                    cursor.getInt(idColumnIndex),
                    cursor.getString(titleColumnIndex),
                    cursor.getString(authorColumnIndex),
                    cursor.getString(yearColumnIndex)
            ));
        }
        cursor.close();
        mComicsDataSource.close();

        return seriesList;
    }

    /**
     * Get a given series' comics from the DB.
     * @param series Series instance
     * @return array list of Comic instances
     */
    public ArrayList<Comic> getSeriesComics (Series series) {
        ArrayList<Comic> comicList = new ArrayList<>();

        mComicsDataSource.open();
        Cursor cursor = mComicsDataSource.selectSeriesComics(series);

        while (cursor.moveToNext()) {
            int idColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_EXTERNAL_ID);
            int titleColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_TITLE);
            int pagesColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_PAGES);
            comicList.add(new Comic(
                    cursor.getInt(idColumnIndex),
                    cursor.getString(titleColumnIndex),
                    cursor.getString(pagesColumnIndex),
                    series
            ));
        }
        cursor.close();
        mComicsDataSource.close();

        return comicList;
    }

    /**
     * Delete the downloaded comic pages.
     * @param comic Comic instance
     */
    public void removeComicDownload (Comic comic) {
        File comicDirectory = comic.getComicDirectory(mContext);
        for (File file : comicDirectory.listFiles()) {
            boolean result = file.delete();
            if (result) {
                Log.d(LOG_TAG, "Comic page deleted");
            }
        }
    }
}
