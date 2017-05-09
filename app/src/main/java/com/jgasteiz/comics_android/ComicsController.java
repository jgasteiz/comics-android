package com.jgasteiz.comics_android;

import android.os.AsyncTask;
import android.util.Log;
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

    public void getSeries (final OnSeriesFetched onSeriesFetched) {
        final GetStringResponseAsyncTask task = new GetStringResponseAsyncTask(new OnResponseFetched() {
            @Override
            public void callback(String response) {
                Log.d(LOG_TAG, response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<Series> seriesList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        seriesList.add(new Series(jsonObject));
                    }
                    onSeriesFetched.callback(seriesList);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        });
        task.execute(SERIES_URL);
    }

    public void getSeriesComics (Series series, final OnComicsFetched onComicsFetched) {
        final GetStringResponseAsyncTask task = new GetStringResponseAsyncTask(new OnResponseFetched() {
            @Override
            public void callback(String response) {
                Log.d(LOG_TAG, response);
                try {
                    JSONObject jsonSeriesDetail = new JSONObject(response);
                    JSONArray jsonArray = (JSONArray) jsonSeriesDetail.get("comics");
                    ArrayList<Comic> comicList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        comicList.add(new Comic(jsonObject));
                    }
                    onComicsFetched.callback(comicList);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        });
        task.execute(SERIES_URL + series.getId() + "/");
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
