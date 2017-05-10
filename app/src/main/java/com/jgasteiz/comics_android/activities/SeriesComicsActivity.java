package com.jgasteiz.comics_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.jgasteiz.comics_android.ComicsController;
import com.jgasteiz.comics_android.R;
import com.jgasteiz.comics_android.interfaces.OnComicsFetched;
import com.jgasteiz.comics_android.models.Comic;
import com.jgasteiz.comics_android.models.Series;

import java.util.ArrayList;

public class SeriesComicsActivity extends AppCompatActivity {

    protected Series mSeries;
    private ComicsController mComicsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_comics);

        // Retrieve the post url from the intent
        Intent intent = getIntent();
        mSeries = (Series) intent.getSerializableExtra("series");

        // Set title on the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mSeries.getTitle());
        }

        mComicsController = new ComicsController();
        mComicsController.getSeriesComics(mSeries, new OnComicsFetched() {
            @Override
            public void callback(ArrayList<Comic> comicList) {
                populateComicList(comicList);
            }
        });
    }

    private void populateComicList (final ArrayList<Comic> comicList) {
        ArrayList<String> comicTitles = new ArrayList<String>();
        for (Comic comic : comicList) {
            comicTitles.add(comic.getTitle());
        }
        ArrayAdapter<String> seriesListAdapter = new ArrayAdapter<String> (
                this,
                android.R.layout.simple_list_item_1,
                comicTitles
        );
        ListView listView = (ListView) findViewById(R.id.comic_list);
        listView.setAdapter(seriesListAdapter);

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
}
