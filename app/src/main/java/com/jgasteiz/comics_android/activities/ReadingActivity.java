package com.jgasteiz.comics_android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.jgasteiz.comics_android.R;
import com.jgasteiz.comics_android.models.Comic;
import com.squareup.picasso.Picasso;

public class ReadingActivity extends AppCompatActivity {

    private int mCurrentPageIndex = 0;

    private ImageView mPageImageView;
    private Comic mComic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        mPageImageView = (ImageView) findViewById(R.id.active_page);

        mPageImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getAxisValue(MotionEvent.AXIS_X);
                float y = motionEvent.getAxisValue(MotionEvent.AXIS_Y);

                int touchRightPosition = (int) (100 * x / view.getWidth());

                if (touchRightPosition > 85) {
                    loadNextPage();
                } else if (touchRightPosition < 15) {
                    loadPreviousPage();
                }

                return false;
            }
        });

        mComic = (Comic) getIntent().getSerializableExtra("comic");

        loadPageWithIndex(mCurrentPageIndex);
    }

    private void loadNextPage() {
        mCurrentPageIndex++;
        if (mCurrentPageIndex > mComic.getPages().size() - 1) {
            mCurrentPageIndex = mComic.getPages().size() - 1;
        }
        loadPageWithIndex(mCurrentPageIndex);
    }

    private void loadPreviousPage() {
        mCurrentPageIndex--;
        if (mCurrentPageIndex < 0) {
            mCurrentPageIndex = 0;
        }
        loadPageWithIndex(mCurrentPageIndex);
    }

    private void loadPageWithIndex (int pageIndex) {
        Picasso
            .with(this)
            .load(mComic.getPage(pageIndex))
            .into(mPageImageView);
    }
}
