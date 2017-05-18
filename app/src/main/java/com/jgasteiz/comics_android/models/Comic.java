package com.jgasteiz.comics_android.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.jgasteiz.comics_android.helpers.Secret;
import com.jgasteiz.comics_android.helpers.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Comic implements Serializable {

    private static final String LOG_TAG = Comic.class.getSimpleName();

    private int id;
    private String title;
    private ArrayList<String> pages;
    private Series series;

    public Comic(int id, String title, String pages, Series series) {
        this.id = id;
        this.title = title;
        this.pages = Utils.convertStringToArray(pages);
        this.series = series;
    }

    public Comic(Series series, JSONObject jsonObject) {
        this.series = series;

        try {
            this.id = (int) jsonObject.get("pk");
            this.title = (String) jsonObject.get("title");
            // Add the pages.
            this.pages = new ArrayList<>();
            JSONArray pagesJSONArray = (JSONArray) jsonObject.get("pages");
            if (pagesJSONArray != null) {
                for (int i = 0; i < pagesJSONArray.length(); i++) {
                    this.pages.add(pagesJSONArray.getString(i));
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getPages() {
        return pages;
    }

    public void setPages(ArrayList<String> pages) {
        this.pages = pages;
    }

    // HELPER FUNCTIONS

    /**
     * Checks if a given comic has been downloaded and return true if it has.
     * @param context Context instance
     * @return true if the comic is offline, false if not.
     */
    public boolean isComicOffline (Context context) {
        File comicDirectory = getComicDirectory(context);
        return comicDirectory.exists() && comicDirectory.list().length == getPages().size();
    }

    /**
     * Return the comic directory.
     * @param context Context instance
     * @return File instance for the comic directory
     */
    public File getComicDirectory (Context context) {
        String comicDirectoryPath = String.format("%s%s%s", context.getFilesDir(), File.separator, getId());
        return new File(comicDirectoryPath);
    }

    public String getPage(int pageIndex) {
        if (!Secret.MOCK_ENABLED) {
            return pages.get(pageIndex);
        } else {
            return pages.get(pageIndex).replace("localhost", Secret.MOCK_SERVER_IP_ADDRESS);
        }
    }

    public Bitmap getOfflinePage (Context context, int pageIndex) {
        // Return the offline page
        String[] fileNameParts = pages.get(pageIndex).split("/");
        String pageFileName = fileNameParts[fileNameParts.length - 1];
        File pageFile = new File(getComicDirectory(context), pageFileName);

        if(pageFile.exists()){
            return BitmapFactory.decodeFile(pageFile.getAbsolutePath());
        }
        return null;
    }

    public String getSerializedPages() {
        return Utils.convertArrayToString(getPages());
    }
}
