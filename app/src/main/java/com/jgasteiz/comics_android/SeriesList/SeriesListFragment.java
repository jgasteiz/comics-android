package com.jgasteiz.comics_android.SeriesList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.jgasteiz.comics_android.ComicList.ComicListActivity;
import com.jgasteiz.comics_android.R;
import com.jgasteiz.comics_android.helpers.Utils;
import com.jgasteiz.comics_android.interfaces.OnSeriesFetched;
import com.jgasteiz.comics_android.models.Series;

import java.util.ArrayList;

public class SeriesListFragment extends Fragment {

    private ArrayList<Series> mSeriesList;
    private SeriesListAdapter mAdapter;
    private ListView mListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSeriesList = Utils.getSeries(getContext());
        mSeriesList.add(new Series(1, "The Walking Dead", "Robert Kirkman", "2013"));
        mAdapter = new SeriesListAdapter(getContext(), mSeriesList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.series_list_fragment, container, false);

        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ComicListActivity.class);
                intent.putExtra("series", mSeriesList.get(position));
                startActivity(intent);
            }
        });

        // If there's internet, load the series from the API and reload the
        // comic list.
        if (Utils.isNetworkAvailable(getContext())) {
            Utils.fetchSeries(getContext(), new OnSeriesFetched() {
                @Override
                public void callback() {
                    mSeriesList.clear();
                    mSeriesList.addAll(Utils.getSeries(getContext()));
                    mListView.invalidate();
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        return view;
    }
}
