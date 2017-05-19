package com.jgasteiz.comics_android.ComicList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.jgasteiz.comics_android.R;
import com.jgasteiz.comics_android.helpers.Utils;
import com.jgasteiz.comics_android.interfaces.OnComicDownloaded;
import com.jgasteiz.comics_android.interfaces.OnPageDownloaded;
import com.jgasteiz.comics_android.models.Comic;

import java.util.ArrayList;

public class ComicListAdapter extends ArrayAdapter<Comic> {

    private static final String LOG_TAG = ComicListAdapter.class.getSimpleName();

    ComicListAdapter(Context context, ArrayList<Comic> comicList) {
        super(context, 0, comicList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
        assert comic != null;
        if (comic.isComicOffline(getContext())) {
            setRemoveButton(convertView, comic);
        } else {
            setDownloadButton(convertView, comic);
        }

        return convertView;
    }

    /**
     * Change the action button to `download` a comic.
     * @param convertView View instance
     * @param comic Comic instance
     */
    private void setDownloadButton (final View convertView, final Comic comic) {
        Button downloadComicButton = (Button) convertView.findViewById(R.id.action_button);
        downloadComicButton.setText(getContext().getString(R.string.download_comic));

        // Download the comic when the button is clicked.
        downloadComicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert comic != null;

                final Button downloadComicButton = (Button) convertView.findViewById(R.id.action_button);
                final TextView progressTextView = (TextView) convertView.findViewById(R.id.progress_text);

                downloadComicButton.setVisibility(View.GONE);
                progressTextView.setVisibility(View.VISIBLE);
                progressTextView.setText(R.string.downloading_comic);

                DownloadComicAsyncTask task = new DownloadComicAsyncTask(
                    getContext(),
                    comic,
                    new OnPageDownloaded() {
                        @Override
                        public void callback(final String message) {
                            ((ComicListActivity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressTextView.setText(message);
                                    Log.d(LOG_TAG, message);
                                }
                            });
                        }
                    }, new OnComicDownloaded() {
                        @Override
                        public void callback() {
                            ((ComicListActivity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Comic downloaded", Toast.LENGTH_SHORT).show();
                                    Log.d(LOG_TAG, "Comic downloaded");

                                    setRemoveButton(convertView, comic);

                                    progressTextView.setVisibility(View.GONE);
                                    downloadComicButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                    task.execute();
                }
            });
    }

    /**
     * Change the action button to `remove` a downloaded comic.
     * @param convertView View instance
     * @param comic Comic instance
     */
    private void setRemoveButton (final View convertView, final Comic comic) {
        Button downloadComicButton = (Button) convertView.findViewById(R.id.action_button);
        downloadComicButton.setText(getContext().getString(R.string.remove_download));

        // Remove the downloaded comic when the button is clicked.
        downloadComicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.removeComicDownload(getContext(), comic);
                setDownloadButton(convertView, comic);
            }
        });
    }
}
