package com.jgasteiz.comics_android.helpers;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import com.jgasteiz.comics_android.db.ComicsDataSource;
import com.jgasteiz.comics_android.db.ComicsHelper;
import com.jgasteiz.comics_android.interfaces.OnComicsFetched;
import com.jgasteiz.comics_android.interfaces.OnResponseFetched;
import com.jgasteiz.comics_android.interfaces.OnSeriesFetched;
import com.jgasteiz.comics_android.models.Comic;
import com.jgasteiz.comics_android.models.Series;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private class GetStringResponseAsyncTask extends AsyncTask<String, Void, String> {

        private OnResponseFetched mOnResponseFetched;

        private OkHttpClient client = new OkHttpClient();

        public GetStringResponseAsyncTask(OnResponseFetched onResponseFetched) {
            mOnResponseFetched = onResponseFetched;
        }

        @Override
        protected String doInBackground(String... params) {

            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();

                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            mOnResponseFetched.callback(response);
        }
    }
}
