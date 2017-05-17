package com.jgasteiz.comics_android.helpers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.jgasteiz.comics_android.ComicList.DownloadComicAsyncTask;
import com.jgasteiz.comics_android.db.ComicsDataSource;
import com.jgasteiz.comics_android.db.ComicsHelper;
import com.jgasteiz.comics_android.interfaces.*;
import com.jgasteiz.comics_android.models.Comic;
import com.jgasteiz.comics_android.models.Series;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        cursor.moveToFirst();

        boolean hasNext = true;
        while (hasNext) {
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
            hasNext = cursor.moveToNext();
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
        cursor.moveToFirst();

        boolean hasNext = true;
        while (hasNext) {
            int idColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_EXTERNAL_ID);
            int titleColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_TITLE);
            int pagesColumnIndex = cursor.getColumnIndex(ComicsHelper.COLUMN_PAGES);
            comicList.add(new Comic(
                    cursor.getInt(idColumnIndex),
                    cursor.getString(titleColumnIndex),
                    cursor.getString(pagesColumnIndex),
                    series
            ));
            hasNext = cursor.moveToNext();
        }
        cursor.close();
        mComicsDataSource.close();

        return comicList;
    }

    /**
     * Download the pages of the given comic in the internal storage.
     *
     * Only call from a background thread.
     *
     * @param comic Comic instance
     * @param onPageDownloaded callback
     */
    public void downloadComic (Comic comic, OnPageDownloaded onPageDownloaded) {
        // Create a directory for the comic
        String comicDirectoryPath = String.format("%s%s%s", mContext.getFilesDir(), File.separator, comic.getId());
        File comicDirectory = new File(comicDirectoryPath);
        comicDirectory.mkdirs();
        Log.d(LOG_TAG, String.format("Comic directory %s created", comicDirectory.getAbsolutePath()));

        // Download all the pages.
        for (int i = 0; i < comic.getPages().size(); i++) {
            String page = comic.getPage(i);
            downloadPage(page, comicDirectoryPath);
            onPageDownloaded.callback(String.format("Downloading at %s percent", i * 100 / comic.getPages().size()));
        }
    }

    /**
     * Download a single page.
     *
     * Only call from a background thread.
     *
     * @param downloadUrl URL of the page to be downloaded.
     * @param comicDirectoryPath directory where the page should be downloaded.
     */
    private void downloadPage (final String downloadUrl, final String comicDirectoryPath) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(downloadUrl).build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download file: " + response);
            }
            String[] fileNameParts = downloadUrl.split("/");
            String fileName = fileNameParts[fileNameParts.length - 1];
            FileOutputStream fos = new FileOutputStream(String.format("%s%s%s", comicDirectoryPath, File.separator, fileName));
            fos.write(response.body().bytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the downloaded comic pages.
     * @param comic
     */
    public void removeComicDownload (Comic comic) {
        File comicDirectory = comic.getComicDirectory(mContext);
        for (File file : comicDirectory.listFiles()) {
            file.delete();
        }
    }
}
