package com.jgasteiz.comics_android.ComicList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.jgasteiz.comics_android.helpers.ComicsController;
import com.jgasteiz.comics_android.R;
import com.jgasteiz.comics_android.Reading.ReadingActivity;
import com.jgasteiz.comics_android.helpers.Constants;
import com.jgasteiz.comics_android.helpers.Utils;
import com.jgasteiz.comics_android.interfaces.OnComicsFetched;
import com.jgasteiz.comics_android.models.Comic;
import com.jgasteiz.comics_android.models.Series;

import java.util.ArrayList;

public class ComicListActivity extends AppCompatActivity {

    private static final String LOG_TAG = ComicListActivity.class.getSimpleName();

    protected Series mSeries;
    private ComicsController mComicsController;
    private Toast mToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_comics);

        // Register download broadcast receiver for updating the UI.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.DOWNLOAD_PROGRESS_ACTION);
        intentFilter.addAction(Constants.COMIC_DOWNLOADED_ACTION);
        registerReceiver(mComicDownloadReceiver, intentFilter);

        // Retrieve the post url from the intent
        Intent intent = getIntent();
        mSeries = (Series) intent.getSerializableExtra("series");

        // Set title on the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mSeries.getTitle());
        }

        mComicsController = new ComicsController(this);
        if (Utils.isNetworkAvailable(this)) {
            mComicsController.fetchSeriesComics(mSeries, new OnComicsFetched() {
            @Override
            public void callback() {
                populateComicList(mComicsController.getSeriesComics(mSeries));
            }
        });
        } else {
            populateComicList(mComicsController.getSeriesComics(mSeries));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mComicDownloadReceiver);
    }

    private void populateComicList (final ArrayList<Comic> comicList) {
        ArrayList<String> comicTitles = new ArrayList<String>();
        for (Comic comic : comicList) {
            comicTitles.add(comic.getTitle());
        }
        ComicListAdapter comicListAdapter = new ComicListAdapter(this, comicList);
        ListView listView = (ListView) findViewById(R.id.comic_list);
        listView.setAdapter(comicListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navigateToReadingView(comicList.get(position));
            }
        });
    }

    private void navigateToReadingView(Comic comic) {
        Intent intent = new Intent(getApplication(), ReadingActivity.class);
        intent.putExtra("comic", comic);
        startActivity(intent);
    }

    private BroadcastReceiver mComicDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case Constants.DOWNLOAD_PROGRESS_ACTION:
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getApplication(), intent.getStringExtra("message"), Toast.LENGTH_SHORT);
                    mToast.show();
                    Log.d(LOG_TAG, intent.getStringExtra("message"));
                    break;
                case Constants.COMIC_DOWNLOADED_ACTION:
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getApplication(), intent.getStringExtra("message"), Toast.LENGTH_SHORT);
                    mToast.show();
                    Log.d(LOG_TAG, intent.getStringExtra("message"));
                    break;
            }
        }
    };
}
