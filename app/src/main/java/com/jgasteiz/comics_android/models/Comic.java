package com.jgasteiz.comics_android.models;

import android.util.Log;
import com.jgasteiz.comics_android.helpers.Secret;
import com.jgasteiz.comics_android.helpers.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public String getPage(int pageIndex) {
        if (!Secret.MOCK_ENABLED) {
            return pages.get(pageIndex);
        } else {
            return pages.get(pageIndex).replace("localhost", Secret.MOCK_SERVER_IP_ADDRESS);
        }
    }

    public String getSerializedPages() {
        return Utils.convertArrayToString(getPages());
    }
}
