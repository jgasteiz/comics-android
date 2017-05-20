package com.jgasteiz.comics_android.models

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class Series : Serializable {

    val logTag = "Series"

    var id: Int = 0
    var title: String? = null
    var author: String? = null
    var year: String? = null

    constructor(id: Int, title: String, author: String, year: String) {
        this.id = id
        this.title = title
        this.author = author
        this.year = year
    }

    constructor(jsonObject: JSONObject) {
        try {
            this.id = jsonObject.get("pk") as Int
            this.title = jsonObject.get("title") as String
            this.author = jsonObject.get("author") as String
            this.year = jsonObject.get("year") as String
        } catch (e: JSONException) {
            Log.e(logTag, e.message)
        }

    }
}
