package com.jgasteiz.comics_android.ComicList

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jgasteiz.comics_android.R
import com.jgasteiz.comics_android.models.Series

class ComicListActivity : AppCompatActivity() {

    private var mSeries: Series? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comic_list_activity)

        // Retrieve the selected series from the intent.
        val intent = intent
        mSeries = intent.getSerializableExtra("series") as Series

        // Set title on the toolbar
        supportActionBar?.title = mSeries?.title

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.comic_list_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return
            }

            // Create a new Fragment to be placed in the activity layout
            val firstFragment = ComicListFragment(mSeries!!)

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.arguments = intent.extras

            // Add the fragment to the 'fragment_container' FrameLayout
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.comic_list_fragment_container, firstFragment)
                    .commit()
        }
    }
//    }
}
