package com.jgasteiz.comics_android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.jgasteiz.comics_android.R;
import com.jgasteiz.comics_android.models.Comic;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ReadingActivity extends AppCompatActivity {

    private final static String LOG_TAG = ReadingActivity.class.getSimpleName();

    private int mCurrentPageIndex = 0;

    private ImageView mPageImageView;
    private PhotoViewAttacher mAttacher;
    private Comic mComic;

    private Callback mPicassoCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        // Get the comic from the intent.
        mComic = (Comic) getIntent().getSerializableExtra("comic");

        // Initialize the main image view.
        mPageImageView = (ImageView) findViewById(R.id.active_page);
        // Attach a PhotoView attacher to it.
        mAttacher = new PhotoViewAttacher(mPageImageView);
        // Set a custom single tap listener for navigating through the comic.
        mAttacher.setOnViewTapListener(getOnTapListener());

        // Define the callback for picasso image loading.
        mPicassoCallback = getPicassoCallback();

        // Load the first page.
        loadPageWithIndex(mCurrentPageIndex);
    }

    /**
     * Navigate to the next page of the comic.
     */
    private void loadNextPage() {
        mCurrentPageIndex++;
        if (mCurrentPageIndex > mComic.getPages().size() - 1) {
            mCurrentPageIndex = mComic.getPages().size() - 1;
        }
        loadPageWithIndex(mCurrentPageIndex);
    }

    /**
     * Navigate to the previous page of the comic.
     */
    private void loadPreviousPage() {
        mCurrentPageIndex--;
        if (mCurrentPageIndex < 0) {
            mCurrentPageIndex = 0;
        }
        loadPageWithIndex(mCurrentPageIndex);
    }

    /**
     * Load the comic page with the given `pageIndex` index.
     *
     * @param pageIndex Integer, index of the page to load.
     */
    private void loadPageWithIndex (int pageIndex) {
        Picasso
            .with(this)
            .load(mComic.getPage(pageIndex))
            .into(mPageImageView, mPicassoCallback);
    }

    /**
     * Custom tap listener for loading the next or the previous comic page,
     * depending on where the user taps on the screen:
     * 15% left side of the screen -> previous page.
     * 15% right side of the screen -> next page.
     *
     * @return PhotoViewAttacher.OnViewTapListener, custom tap listener.
     */
    private PhotoViewAttacher.OnViewTapListener getOnTapListener () {
        return new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                int touchRightPosition = (int) (100 * x / view.getWidth());

                if (touchRightPosition > 85) {
                    loadNextPage();
                } else if (touchRightPosition < 15) {
                    loadPreviousPage();
                }
            }
        };
    }

    /**
     * Returns a new Picasso callback instance. To be used when loading an
     * image the imageview. The callback will update the PhotoView attacher
     * after the image has been loaded.
     *
     * @return Picasso Callback.
     */
    private Callback getPicassoCallback () {
        return new Callback() {
            @Override
            public void onSuccess() {
                mAttacher.update();
            }

            @Override
            public void onError() {
                Log.e(LOG_TAG, "An error occurred");
            }
        };
    }
}
