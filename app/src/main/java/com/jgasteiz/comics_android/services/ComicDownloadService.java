package com.jgasteiz.comics_android.services;

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
import com.jgasteiz.comics_android.interfaces.OnComicDownloaded;
import com.jgasteiz.comics_android.interfaces.OnPageDownloaded;
import com.jgasteiz.comics_android.models.Comic;

import java.io.File;


public class ComicDownloadService extends Service {

    private static final String LOG_TAG = ComicDownloadService.class.getSimpleName();

    private ComicsController mComicsController;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Comic download service started", Toast.LENGTH_SHORT).show();

        mComicsController = new ComicsController(this);

        Comic comic = (Comic) intent.getSerializableExtra("comic");

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

    private void downloadComic (Comic comic) {
        final Toast[] toast = new Toast[1];
        mComicsController.downloadComic(comic,
                new OnPageDownloaded() {
                @Override
                public void callback(final String message) {
                    Log.d(LOG_TAG, message);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (toast[0] != null) {
                                toast[0].cancel();
                            }
                            toast[0] = Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT);
                            toast[0].show();
                        }
                    });
                }
            }, new OnComicDownloaded() {
                @Override
                public void callback() {
                    final String message = "Comic downloaded!";
                    Log.d(LOG_TAG, message);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (toast[0] != null) {
                                toast[0].cancel();
                            }
                            toast[0] = Toast.makeText(getApplication(), message, Toast.LENGTH_LONG);
                            toast[0].show();
                        }
                    });
                }
            });
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
