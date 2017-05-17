package com.jgasteiz.comics_android.ComicList;

import android.content.Context;
import android.os.AsyncTask;
import com.jgasteiz.comics_android.helpers.ComicsController;
import com.jgasteiz.comics_android.interfaces.OnComicDownloaded;
import com.jgasteiz.comics_android.interfaces.OnPageDownloaded;
import com.jgasteiz.comics_android.models.Comic;


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
        ComicsController comicsController = new ComicsController(mContext);
        comicsController.downloadComic(mComic, mOnPageDownloaded);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mOnComicDownloaded.callback();
    }
}
