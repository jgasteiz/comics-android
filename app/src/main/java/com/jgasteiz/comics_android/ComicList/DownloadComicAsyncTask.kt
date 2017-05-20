package com.jgasteiz.comics_android.ComicList

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.jgasteiz.comics_android.interfaces.OnComicDownloaded
import com.jgasteiz.comics_android.interfaces.OnPageDownloaded
import com.jgasteiz.comics_android.models.Comic
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DownloadComicAsyncTask internal constructor(
        context: Context,
        private val mComic: Comic,
        private val mOnPageDownloaded: OnPageDownloaded,
        private val mOnComicDownloaded: OnComicDownloaded
) : AsyncTask<Void, String, Void>() {

    private val LOG_TAG = DownloadComicAsyncTask::class.java.simpleName

    private val mFilesDir: File

    init {
        mFilesDir = context.filesDir
    }

    override fun doInBackground(vararg params: Void): Void? {
        downloadComic()
        return null
    }

    override fun onProgressUpdate(vararg values: String) {
        mOnPageDownloaded.callback(values[0])
    }

    override fun onPostExecute(aVoid: Void) {
        mOnComicDownloaded.callback()
    }


    /**
     * Download the pages of the given comic in the internal storage.
     */
    private fun downloadComic() {
        // Create a directory for the comic
        val comicDirectoryPath = String.format("%s%s%s", mFilesDir, File.separator, mComic.id)
        val comicDirectory = File(comicDirectoryPath)
        val directoryCreated = comicDirectory.mkdirs()
        if (directoryCreated) {
            Log.d(LOG_TAG, String.format("Comic directory %s created", comicDirectory.absolutePath))
        }

        // Download all the pages.
        for (i in 0..mComic.pages!!.size - 1) {
            val page = mComic.getPage(i)
            downloadPage(page, comicDirectoryPath)

            // Update the progress.
            val progressMessage = String.format("Downloading at %s percent", i * 100 / mComic.pages!!.size)
            publishProgress(progressMessage)
        }
    }

    /**
     * Download a single page.

     * Only call from a background thread.

     * @param downloadUrl URL of the page to be downloaded.
     * *
     * @param comicDirectoryPath directory where the page should be downloaded.
     */
    private fun downloadPage(downloadUrl: String, comicDirectoryPath: String) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(downloadUrl).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Failed to download file: " + response)
            }
            val fileNameParts = downloadUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val fileName = fileNameParts[fileNameParts.size - 1]
            val fos = FileOutputStream(String.format("%s%s%s", comicDirectoryPath, File.separator, fileName))
            fos.write(response.body().bytes())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
