package com.jgasteiz.comics_android.helpers;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.jgasteiz.comics_android.db.ComicsDataSource;
import com.jgasteiz.comics_android.db.ComicsHelper;
import com.jgasteiz.comics_android.interfaces.OnComicsFetched;
import com.jgasteiz.comics_android.interfaces.OnResponseFetched;
import com.jgasteiz.comics_android.interfaces.OnSeriesFetched;
import com.jgasteiz.comics_android.models.Comic;
import com.jgasteiz.comics_android.models.Series;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    private static String STR_SEPARATOR = "__,__";

    private static final String SERIES_URL = "http://comics.jgasteiz.com/api/series/";
    private static final String MOCK_SERIES_URL = String.format("http://%s:8000/api/series/", Secret.MOCK_SERVER_IP_ADDRESS);

    public static String convertArrayToString(ArrayList<String> array){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            str.append(array.get(i));
            // Do not append comma at the end of last element
            if(i < array.size() - 1){
                str.append(STR_SEPARATOR);
            }
        }
        return str.toString();
    }
    public static ArrayList<String> convertStringToArray(String str){
        String[] pages = str.split(STR_SEPARATOR);
        ArrayList<String> pageList = new ArrayList<>();
        Collections.addAll(pageList, pages);
        return pageList;
    }

    public static boolean isNetworkAvailable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Fetch all the series from the API and store them in the DB.
     * @param onSeriesFetched callback
     */
    public static void fetchSeries (Context context, final OnSeriesFetched onSeriesFetched) {
        final ComicsDataSource comicsDataSource = new ComicsDataSource(context);

        GetStringResponseAsyncTask task = new GetStringResponseAsyncTask(new OnResponseFetched() {
            @Override
            public void callback(String response) {
                Log.d(LOG_TAG, response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    comicsDataSource.open();
                    comicsDataSource.deleteAllSeries();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        comicsDataSource.insertSeries(new Series(jsonObject));
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                comicsDataSource.close();
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
    public static void fetchSeriesComics (Context context, final Series series, final OnComicsFetched onComicsFetched) {
        final ComicsDataSource comicsDataSource = new ComicsDataSource(context);

        GetStringResponseAsyncTask task = new GetStringResponseAsyncTask(new OnResponseFetched() {
            @Override
            public void callback(String response) {
                Log.d(LOG_TAG, response);
                try {
                    JSONObject jsonSeriesDetail = new JSONObject(response);
                    JSONArray jsonArray = (JSONArray) jsonSeriesDetail.get("comics");
                    comicsDataSource.open();
                    comicsDataSource.deleteAllSeriesComics(series);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        comicsDataSource.insertComic(new Comic(series, jsonObject));
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                comicsDataSource.close();
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
    public static ArrayList<Series> getSeries (Context context) {
        ComicsDataSource comicsDataSource = new ComicsDataSource(context);
        ArrayList<Series> seriesList = new ArrayList<>();

        comicsDataSource.open();
        Cursor cursor = comicsDataSource.selectAllSeries();

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
        comicsDataSource.close();

        return seriesList;
    }

    /**
     * Get a given series' comics from the DB.
     * @param series Series instance
     * @return array list of Comic instances
     */
    public static ArrayList<Comic> getSeriesComics (Context context, Series series) {
        ComicsDataSource comicsDataSource = new ComicsDataSource(context);
        ArrayList<Comic> comicList = new ArrayList<>();

        comicsDataSource.open();
        Cursor cursor = comicsDataSource.selectSeriesComics(series);

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
        comicsDataSource.close();

        return comicList;
    }

    /**
     * Delete the downloaded comic pages.
     * @param comic Comic instance
     */
    public static void removeComicDownload (Context context, Comic comic) {
        File comicDirectory = comic.getComicDirectory(context);
        for (File file : comicDirectory.listFiles()) {
            boolean result = file.delete();
            if (result) {
                Log.d(LOG_TAG, "Comic page deleted");
            }
        }
    }
}
