package com.jgasteiz.comics_android.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.jgasteiz.comics_android.helpers.Secret
import com.jgasteiz.comics_android.helpers.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.io.Serializable
import java.util.ArrayList

class Comic : Serializable {

    private val LOG_TAG = Comic::class.java.simpleName

    var id: Int = 0
    var title: String? = null
    var pages: ArrayList<String>? = null
    var series: Series? = null

    constructor(id: Int, title: String, pages: String, series: Series) {
        this.id = id
        this.title = title
        this.pages = Utils.convertStringToArray(pages)
        this.series = series
    }

    constructor(series: Series, jsonObject: JSONObject) {
        this.series = series

        try {
            this.id = jsonObject.get("pk") as Int
            this.title = jsonObject.get("title") as String
            // Add the pages.
            this.pages = ArrayList<String>()
            val pagesJSONArray = jsonObject.get("pages") as JSONArray
            for (i in 0..pagesJSONArray.length() - 1) {
                this.pages!!.add(pagesJSONArray.getString(i))
            }
        } catch (e: JSONException) {
            Log.e(LOG_TAG, e.message)
        }

    }

    // HELPER FUNCTIONS

    /**
     * Checks if a given comic has been downloaded and return true if it has.
     * @param context Context instance
     * *
     * @return true if the comic is offline, false if not.
     */
    fun isComicOffline(context: Context): Boolean {
        val comicDirectory = getComicDirectory(context)
        return comicDirectory.exists() && comicDirectory.list().size == pages!!.size
    }

    /**
     * Return the comic directory.
     * @param context Context instance
     * *
     * @return File instance for the comic directory
     */
    fun getComicDirectory(context: Context): File {
        val comicDirectoryPath = String.format("%s%s%s", context.filesDir, File.separator, id)
        return File(comicDirectoryPath)
    }

    fun getPage(pageIndex: Int): String {
        if (!Secret.MOCK_ENABLED) {
            return pages!![pageIndex]
        } else {
            return pages!![pageIndex].replace("localhost", Secret.MOCK_SERVER_IP_ADDRESS)
        }
    }

    fun getOfflinePage(context: Context, pageIndex: Int): Bitmap? {
        // Return the offline page
        val fileNameParts = pages!![pageIndex].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val pageFileName = fileNameParts[fileNameParts.size - 1]
        val pageFile = File(getComicDirectory(context), pageFileName)

        if (pageFile.exists()) {
            return BitmapFactory.decodeFile(pageFile.absolutePath)
        }
        return null
    }

    val serializedPages: String
        get() = Utils.convertArrayToString(pages!!)

}
