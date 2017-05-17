package com.jgasteiz.comics_android.ComicList;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.jgasteiz.comics_android.SeriesList.SeriesListActivity;
import com.jgasteiz.comics_android.helpers.ComicsController;
import com.jgasteiz.comics_android.helpers.Constants;
import com.jgasteiz.comics_android.interfaces.OnComicDownloaded;
import com.jgasteiz.comics_android.interfaces.OnPageDownloaded;
import com.jgasteiz.comics_android.models.Comic;


public class ComicDownloadService extends Service {

    private static final String LOG_TAG = ComicDownloadService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Comic download service started", Toast.LENGTH_SHORT).show();

        Comic comic = (Comic) intent.getSerializableExtra("comic");

        // Show a notification while the comic is downloading.
        Intent notificationIntent = new Intent(this, SeriesListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this)
            .setContentTitle(String.format("Downloading %s", comic.getTitle()))
            .setContentIntent(pendingIntent)
            .build();
        startForeground(comic.getId(), notification);

        // Download the thing!
        downloadComic(comic);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Download the pages of the given comic in the internal storage.
     * @param comic Comic instance
     */
    private void downloadComic (Comic comic) {
        DownloadComicAsyncTask task = new DownloadComicAsyncTask(
                this,
                comic,
                new OnPageDownloaded() {
                    @Override
                    public void callback(final String message) {
                        Intent intent = new Intent(Constants.DOWNLOAD_PROGRESS_ACTION);
                        intent.putExtra("message", message);
                        sendBroadcast(intent);
                    }
                }, new OnComicDownloaded() {
                    @Override
                    public void callback() {
                        Intent intent = new Intent(Constants.COMIC_DOWNLOADED_ACTION);
                        intent.putExtra("message", "Comic downloaded!");
                        sendBroadcast(intent);
                        // Kill the service when the download is finished.
                        stopSelf();
                    }
                });
        task.execute();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service is finished", Toast.LENGTH_SHORT).show();
    }

}
