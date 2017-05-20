package com.jgasteiz.comics_android.helpers

import android.os.AsyncTask
import com.jgasteiz.comics_android.interfaces.OnResponseFetched
import okhttp3.OkHttpClient
import okhttp3.Request


class GetStringResponseAsyncTask(private val mOnResponseFetched: OnResponseFetched) : AsyncTask<String, Void, String>() {

    private val client = OkHttpClient()

    override fun doInBackground(vararg params: String): String? {

        val builder = Request.Builder()
        builder.url(params[0])
        val request = builder.build()
        try {
            val response = client.newCall(request).execute()

            return response.body().string()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(response: String) {
        mOnResponseFetched.callback(response)
    }
}
