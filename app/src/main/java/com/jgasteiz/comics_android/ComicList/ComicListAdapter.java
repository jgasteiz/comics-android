package com.jgasteiz.comics_android.ComicList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.jgasteiz.comics_android.R;
import com.jgasteiz.comics_android.helpers.ComicsController;
import com.jgasteiz.comics_android.models.Comic;

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

        // Check if a comic is offline or not.
        final ComicsController comicsController = new ComicsController(getContext());
        if (comic.isComicOffline (getContext())) {
            setRemoveButton(convertView, comicsController, comic);
        } else {
            setDownloadButton(convertView, comic);
        }

        return convertView;
    }

    public void setDownloadButton (View convertView, final Comic comic) {
        Button downloadComicButton = (Button) convertView.findViewById(R.id.download_comic);
        downloadComicButton.setText(getContext().getString(R.string.download_comic));

        // Download the comic when the button is clicked.
        downloadComicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert comic != null;

                // Start the comic download service.
                Intent downloadIntent = new Intent(getContext(), ComicDownloadService.class);
                downloadIntent.putExtra("comic", comic);
                getContext().startService(downloadIntent);
            }
        });
    }

    public void setRemoveButton (final View convertView, final ComicsController comicsController, final Comic comic) {
        Button downloadComicButton = (Button) convertView.findViewById(R.id.download_comic);
        downloadComicButton.setText(getContext().getString(R.string.remove_download));

        // Download the comic when the button is clicked.
        downloadComicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comicsController.removeComicDownload(comic);
                setDownloadButton(convertView, comic);
            }
        });
    }
}
