package com.jgasteiz.comics_android.Reading

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.jgasteiz.comics_android.R
import com.jgasteiz.comics_android.models.Comic
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import uk.co.senab.photoview.PhotoViewAttacher

class ReadingActivity : Activity() {

    private val LOG_TAG = ReadingActivity::class.java.simpleName

    private var mCurrentPageIndex = 0

    private var mPageImageView: ImageView? = null
    private var mAttacher: PhotoViewAttacher? = null
    private var mProgressBar: ProgressBar? = null
    private var mComic: Comic? = null

    private var mPicassoCallback: Callback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reading_activity)

        // Get the comic from the intent.
        mComic = intent.getSerializableExtra("comic") as Comic

        // Initialize the progress bar.
        mProgressBar = findViewById(R.id.progress_bar) as ProgressBar
        mProgressBar!!.visibility = View.GONE
        // Initialize the main image view.
        mPageImageView = findViewById(R.id.active_page) as ImageView
        // Attach a PhotoView attacher to it.
        mAttacher = PhotoViewAttacher(mPageImageView!!)
        // Set a custom single tap listener for navigating through the comic.
        mAttacher!!.onViewTapListener = onTapListener

        // Define the callback for picasso image loading.
        mPicassoCallback = picassoCallback

        // Load the first page.
        loadPageWithIndex(mCurrentPageIndex)

        // Start immersive mode for better reading.
        setImmersiveMode()
    }

    /**
     * Navigate to the next page of the comic.
     */
    private fun loadNextPage() {
        mCurrentPageIndex++
        if (mCurrentPageIndex > mComic!!.pages!!.size - 1) {
            mCurrentPageIndex = mComic!!.pages!!.size - 1
        }
        loadPageWithIndex(mCurrentPageIndex)
    }

    /**
     * Navigate to the previous page of the comic.
     */
    private fun loadPreviousPage() {
        mCurrentPageIndex--
        if (mCurrentPageIndex < 0) {
            mCurrentPageIndex = 0
        }
        loadPageWithIndex(mCurrentPageIndex)
    }

    /**
     * Load the comic page with the given `pageIndex` index.

     * @param pageIndex Integer, index of the page to load.
     */
    private fun loadPageWithIndex(pageIndex: Int) {
        mPageImageView!!.visibility = View.GONE
        mProgressBar!!.visibility = View.VISIBLE
        if (mComic!!.isComicOffline(this)) {
            val pageBitmap = mComic!!.getOfflinePage(this, pageIndex)
            mPageImageView!!.setImageBitmap(pageBitmap)
            mProgressBar!!.visibility = View.GONE
            mPageImageView!!.visibility = View.VISIBLE
        } else {
            Picasso
                    .with(this)
                    .load(mComic!!.getPage(pageIndex))
                    .into(mPageImageView!!, mPicassoCallback)
        }
    }

    /**
     * Custom tap listener for loading the next or the previous comic page,
     * depending on where the user taps on the screen:
     * 15% left side of the screen -> previous page.
     * 15% right side of the screen -> next page.

     * @return PhotoViewAttacher.OnViewTapListener, custom tap listener.
     */
    private val onTapListener: PhotoViewAttacher.OnViewTapListener
        get() = PhotoViewAttacher.OnViewTapListener { view, x, y ->
            val touchRightPosition = (100 * x / view.width).toInt()

            if (touchRightPosition > 85) {
                loadNextPage()
            } else if (touchRightPosition < 15) {
                loadPreviousPage()
            } else {
                setImmersiveMode()
            }
        }

    /**
     * Returns a new Picasso callback instance. To be used when loading an
     * image the imageview. The callback will update the PhotoView attacher
     * after the image has been loaded.

     * @return Picasso Callback.
     */
    private val picassoCallback: Callback
        get() = object : Callback {
            override fun onSuccess() {
                mProgressBar!!.visibility = View.GONE
                mPageImageView!!.visibility = View.VISIBLE
                mAttacher!!.update()
            }

            override fun onError() {
                Log.e(LOG_TAG, "An error occurred")
            }
        }

    private fun setImmersiveMode() {
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or // hide nav bar
                View.SYSTEM_UI_FLAG_FULLSCREEN or // hide status bar
                View.SYSTEM_UI_FLAG_IMMERSIVE
    }
}
