package com.jgasteiz.comics_android.ComicList

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.jgasteiz.comics_android.R
import com.jgasteiz.comics_android.Reading.ReadingActivity
import com.jgasteiz.comics_android.helpers.Utils
import com.jgasteiz.comics_android.interfaces.OnSeriesFetched
import com.jgasteiz.comics_android.models.Comic
import com.jgasteiz.comics_android.models.Series

import java.util.ArrayList

class ComicListFragment(series: Series) : Fragment() {

    private var mSeries: Series? = series
    private var mComicList: ArrayList<Comic>? = null
    private var mAdapter: ComicListAdapter? = null
    private var mListView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mComicList = Utils.getSeriesComics(context, mSeries!!)
        mAdapter = ComicListAdapter(context, mComicList!!)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.comic_list_fragment, container, false)

        mListView = view.findViewById(R.id.comic_list) as ListView
        mListView!!.adapter = mAdapter

        mListView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(context, ReadingActivity::class.java)
            intent.putExtra("comic", mComicList!!.get(position))
            startActivity(intent)
        }

        // If there's internet, load the series from the API and reload the
        // comic list.
        if (Utils.isNetworkAvailable(context)) {
            Utils.fetchSeries(context, object : OnSeriesFetched {
                override fun callback() {
                    mComicList!!.clear()
                    mComicList!!.addAll(Utils.getSeriesComics(context, mSeries!!))
                    mListView!!.invalidate()
                    mAdapter!!.notifyDataSetChanged()
                }
            })
        }

        return view
    }
}
