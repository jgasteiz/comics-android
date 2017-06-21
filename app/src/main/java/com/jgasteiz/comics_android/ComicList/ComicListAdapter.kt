package com.jgasteiz.comics_android.ComicList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.jgasteiz.comics_android.R
import com.jgasteiz.comics_android.helpers.Utils
import com.jgasteiz.comics_android.interfaces.OnComicDownloaded
import com.jgasteiz.comics_android.interfaces.OnPageDownloaded
import com.jgasteiz.comics_android.models.Comic

import java.util.ArrayList

class ComicListAdapter internal constructor(context: Context, comicList: ArrayList<Comic>?) : ArrayAdapter<Comic>(context, 0, comicList) {

    private val LOG_TAG = ComicListAdapter::class.java.simpleName

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // Get the data item for this position
        val comic = getItem(position)

        // Inflate the view if necessary.
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comic_item, parent, false)
        }

        // Get references to the text views.
        val comicTitleView = convertView!!.findViewById(R.id.comic_title) as TextView

        // Set the comic title, author and year.
        if (comic != null) {
            comicTitleView.text = comic.title
        }

        // Check if a comic is offline or not.
        if (comic.isComicOffline(context)) {
            setRemoveButton(convertView, comic)
        } else {
            setDownloadButton(convertView, comic)
        }

        return convertView
    }

    /**
     * Change the action button to `download` a comic.
     * @param convertView View instance
     * *
     * @param comic Comic instance
     */
    private fun setDownloadButton(convertView: View, comic: Comic) {
        val downloadComicButton = convertView.findViewById(R.id.action_button) as Button
        downloadComicButton.text = context.getString(R.string.download_comic)

        // Download the comic when the button is clicked.
        downloadComicButton.setOnClickListener {
            assert(comic != null)

            val downloadComicButton = convertView.findViewById(R.id.action_button) as Button
            val progressTextView = convertView.findViewById(R.id.progress_text) as TextView

            downloadComicButton.visibility = View.GONE
            progressTextView.visibility = View.VISIBLE
            progressTextView.setText(R.string.downloading_comic)

            val task = DownloadComicAsyncTask(
                    context,
                    comic,
                    object : OnPageDownloaded {
                        override fun callback(message: String) {
                            (context as ComicListActivity).runOnUiThread {
                                progressTextView.text = message
                                Log.d(LOG_TAG, message)
                            }
                        }
                    }, object : OnComicDownloaded {
                        override fun callback() {
                            (context as ComicListActivity).runOnUiThread {
                                Toast.makeText(context, "Comic downloaded", Toast.LENGTH_SHORT).show()
                                Log.d(LOG_TAG, "Comic downloaded")

                                setRemoveButton(convertView, comic)

                                progressTextView.visibility = View.GONE
                                downloadComicButton.visibility = View.VISIBLE
                            }
                        }
                    }
            )
            task.execute()
        }
    }

    /**
     * Change the action button to `remove` a downloaded comic.
     * @param convertView View instance
     * *
     * @param comic Comic instance
     */
    private fun setRemoveButton(convertView: View, comic: Comic) {
        val downloadComicButton = convertView.findViewById(R.id.action_button) as Button
        downloadComicButton.text = context.getString(R.string.remove_download)

        // Remove the downloaded comic when the button is clicked.
        downloadComicButton.setOnClickListener {
            Utils.removeComicDownload(context, comic)
            setDownloadButton(convertView, comic)
        }
    }
}
