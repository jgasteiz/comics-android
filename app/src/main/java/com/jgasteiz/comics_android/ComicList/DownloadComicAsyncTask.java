package com.jgasteiz.comics_android.ComicList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.jgasteiz.comics_android.interfaces.OnComicDownloaded;
import com.jgasteiz.comics_android.interfaces.OnPageDownloaded;
import com.jgasteiz.comics_android.models.Comic;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DownloadComicAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = DownloadComicAsyncTask.class.getSimpleName();

    Context mContext;
    Comic mComic;
    OnPageDownloaded mOnPageDownloaded;
    OnComicDownloaded mOnComicDownloaded;

    public DownloadComicAsyncTask(
            Context context,
            Comic comic,
            OnPageDownloaded onPageDownloaded,
            OnComicDownloaded onComicDownloaded
    ) {
        mContext = context;
        mComic = comic;
        mOnPageDownloaded = onPageDownloaded;
        mOnComicDownloaded = onComicDownloaded;
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Create a directory for the comic
        String comicDirectoryPath = String.format("%s%s%s", mContext.getFilesDir(), File.separator, mComic.getId());
        File comicDirectory = new File(comicDirectoryPath);
        comicDirectory.mkdirs();
        Log.d(LOG_TAG, String.format("Comic directory %s created", comicDirectory.getAbsolutePath()));

        // Download all the pages.
        for (int i = 0; i < mComic.getPages().size(); i++) {
            String page = mComic.getPage(i);
            downloadImage(page, comicDirectoryPath);
            mOnPageDownloaded.callback(String.format("Downloading at %s percent", i * 100 / mComic.getPages().size()));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mOnComicDownloaded.callback();
    }

    private void downloadImage(final String downloadUrl, final String comicDirectoryPath) {
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
}
