package com.jgasteiz.comics_android.models;

import android.util.Log;
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

    public Comic(int id, String title, ArrayList<Integer> comics) {
        this.id = id;
        this.title = title;
    }

    public Comic(JSONObject jsonObject) {
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

    public static String getLogTag() {
        return LOG_TAG;
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
        return pages.get(pageIndex);
    }
}
