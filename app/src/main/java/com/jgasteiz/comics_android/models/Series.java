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

    public Series(int id, String title, ArrayList<Integer> comics) {
        this.id = id;
        this.title = title;
    }

    public Series(JSONObject jsonObject) {
        try {
            this.id = (int) jsonObject.get("pk");
            this.title = (String) jsonObject.get("title");
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
}
