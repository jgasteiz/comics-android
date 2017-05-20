package com.jgasteiz.comics_android.SeriesList

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.jgasteiz.comics_android.ComicList.ComicListActivity
import com.jgasteiz.comics_android.R
import com.jgasteiz.comics_android.helpers.Utils
import com.jgasteiz.comics_android.interfaces.OnSeriesFetched
import com.jgasteiz.comics_android.models.Series

import java.util.ArrayList

class SeriesListFragment : Fragment() {

    private var mSeriesList: ArrayList<Series>? = null
    private var mAdapter: SeriesListAdapter? = null
    private var mListView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSeriesList = Utils.getSeries(context)
        mAdapter = SeriesListAdapter(context, mSeriesList!!)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.series_list_fragment, container, false)

        mListView = view.findViewById(R.id.series_list) as ListView
        mListView!!.adapter = mAdapter

        mListView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(context, ComicListActivity::class.java)
            intent.putExtra("series", mSeriesList!![position])
            startActivity(intent)
        }

        // If there's internet, load the series from the API and reload the
        // comic list.
        if (Utils.isNetworkAvailable(context)) {
            Utils.fetchSeries(context, object : OnSeriesFetched {
                override fun callback() {
                    mSeriesList!!.clear()
                    mSeriesList!!.addAll(Utils.getSeries(context))
                    mListView!!.invalidate()
                    mAdapter!!.notifyDataSetChanged()
                }
            })
        }

        return view
    }
}
