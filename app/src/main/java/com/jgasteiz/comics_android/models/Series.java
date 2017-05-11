package com.jgasteiz.comics_android.models;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Series implements Serializable {

    private static final String LOG_TAG = Series.class.getSimpleName();

    private int id;
    private String title;
    private String author;
    private String year;

    public Series() {}

    public Series(JSONObject jsonObject) {
        try {
            this.id = (int) jsonObject.get("pk");
            this.title = (String) jsonObject.get("title");
            this.author = (String) jsonObject.get("author");
            this.year = (String) jsonObject.get("year");
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
