package com.jgasteiz.comics_android.SeriesList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.jgasteiz.comics_android.R
import com.jgasteiz.comics_android.models.Series

import java.util.ArrayList

class SeriesListAdapter(context: Context, seriesList: ArrayList<Series>) : ArrayAdapter<Series>(context, 0, seriesList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var cView = convertView
        // Get the data item for this position
        val series = getItem(position)

        // Inflate the view if necessary.
        if (cView == null) {
            cView = LayoutInflater.from(context).inflate(R.layout.series_item, parent, false)
        }

        // Get references to the text views.
        val seriesTitleView = cView!!.findViewById(R.id.series_title) as TextView
        val seriesYearView = cView.findViewById(R.id.series_year) as TextView
        val seriesAuthorView = cView.findViewById(R.id.series_author) as TextView

        // Set the series title, author and year.
        if (series != null) {
            seriesTitleView.text = series.title
            seriesYearView.text = String.format("%s, ", series.year)
            seriesAuthorView.text = series.author
        }

        return cView
    }
}
