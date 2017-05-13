package com.jgasteiz.comics_android.ComicList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jgasteiz.comics_android.R;
import com.jgasteiz.comics_android.models.Comic;
import com.jgasteiz.comics_android.models.Series;

import java.util.ArrayList;

public class ComicListAdapter extends ArrayAdapter<Comic> {

    public ComicListAdapter(Context context, ArrayList<Comic> comicList) {
        super(context, 0, comicList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Comic comic = getItem(position);

        // Inflate the view if necessary.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comic, parent, false);
        }

        // Get references to the text views.
        TextView comicTitleView = (TextView) convertView.findViewById(R.id.comic_title);

        // Set the comic title, author and year.
        if (comic != null) {
            comicTitleView.setText(comic.getTitle());
        }

        return convertView;
    }
}
