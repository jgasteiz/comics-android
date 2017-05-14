package com.jgasteiz.comics_android.helpers;

import android.os.AsyncTask;
import com.jgasteiz.comics_android.interfaces.OnResponseFetched;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GetStringResponseAsyncTask extends AsyncTask<String, Void, String> {
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
