package com.jgasteiz.comics_android.SeriesList;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.jgasteiz.comics_android.helpers.ComicsController;
import com.jgasteiz.comics_android.R;
import com.jgasteiz.comics_android.ComicList.ComicListActivity;
import com.jgasteiz.comics_android.helpers.Utils;
import com.jgasteiz.comics_android.interfaces.OnSeriesFetched;
import com.jgasteiz.comics_android.models.Series;

import java.util.ArrayList;

public class SeriesListActivity extends AppCompatActivity {

    private ComicsController mComicsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);

        mComicsController = new ComicsController(this);
        populateSeriesList(mComicsController.getSeries());

        // If there's internet, load the series from the API and reload the
        // comic list.
        if (Utils.isNetworkAvailable(this)) {
            mComicsController.fetchSeries(new OnSeriesFetched() {
                @Override
                public void callback() {
                    populateSeriesList(mComicsController.getSeries());
                }
            });
        }
    }

    private void populateSeriesList (final ArrayList<Series> seriesList) {
        SeriesListAdapter seriesListAdapter = new SeriesListAdapter(this, seriesList);
        ListView listView = (ListView) findViewById(R.id.series_list);
        listView.setAdapter(seriesListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navigateToSeriesView(seriesList.get(position));
            }
        });
    }

    private void navigateToSeriesView(Series series) {
        Intent intent = new Intent(getApplication(), ComicListActivity.class);
        intent.putExtra("series", series);
        startActivity(intent);
    }
}
