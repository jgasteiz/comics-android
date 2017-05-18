package com.jgasteiz.comics_android.SeriesList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jgasteiz.comics_android.R;
import com.jgasteiz.comics_android.models.Series;

import java.util.ArrayList;

public class SeriesListAdapter extends ArrayAdapter<Series> {

    public SeriesListAdapter(Context context, ArrayList<Series> seriesList) {
        super(context, 0, seriesList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        final Series series = getItem(position);

        // Inflate the view if necessary.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.series, parent, false);
        }

        // Get references to the text views.
        TextView seriesTitleView = (TextView) convertView.findViewById(R.id.series_title);
        TextView seriesYearView = (TextView) convertView.findViewById(R.id.series_year);
        TextView seriesAuthorView = (TextView) convertView.findViewById(R.id.series_author);

        // Set the series title, author and year.
        if (series != null) {
            seriesTitleView.setText(series.getTitle());
            seriesYearView.setText(String.format("%s, ", series.getYear()));
            seriesAuthorView.setText(series.getAuthor());
        }

        return convertView;
    }
}
