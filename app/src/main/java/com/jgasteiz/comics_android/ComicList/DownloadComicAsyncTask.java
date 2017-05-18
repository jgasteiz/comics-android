package com.jgasteiz.comics_android.ComicList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.jgasteiz.comics_android.interfaces.OnComicDownloaded;
import com.jgasteiz.comics_android.interfaces.OnPageDownloaded;
import com.jgasteiz.comics_android.models.Comic;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DownloadComicAsyncTask extends AsyncTask<Void, String, Void> {

    private static final String LOG_TAG = DownloadComicAsyncTask.class.getSimpleName();

    private File mFilesDir;
    private Comic mComic;
    private OnPageDownloaded mOnPageDownloaded;
    private OnComicDownloaded mOnComicDownloaded;

    DownloadComicAsyncTask(
            Context context,
            Comic comic,
            OnPageDownloaded onPageDownloaded,
            OnComicDownloaded onComicDownloaded
    ) {
        mFilesDir = context.getFilesDir();
        mComic = comic;
        mOnPageDownloaded = onPageDownloaded;
        mOnComicDownloaded = onComicDownloaded;
    }

    @Override
    protected Void doInBackground(Void... params) {
        downloadComic();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        mOnPageDownloaded.callback(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mOnComicDownloaded.callback();
    }


    /**
     * Download the pages of the given comic in the internal storage.
     */
    private void downloadComic () {
        // Create a directory for the comic
        String comicDirectoryPath = String.format("%s%s%s", mFilesDir, File.separator, mComic.getId());
        File comicDirectory = new File(comicDirectoryPath);
        boolean directoryCreated = comicDirectory.mkdirs();
        if (directoryCreated) {
            Log.d(LOG_TAG, String.format("Comic directory %s created", comicDirectory.getAbsolutePath()));
        }

        // Download all the pages.
        for (int i = 0; i < mComic.getPages().size(); i++) {
            String page = mComic.getPage(i);
            downloadPage(page, comicDirectoryPath);

            // Update the progress.
            String progressMessage = String.format("Downloading at %s percent", i * 100 / mComic.getPages().size());
            publishProgress(progressMessage);
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
}
