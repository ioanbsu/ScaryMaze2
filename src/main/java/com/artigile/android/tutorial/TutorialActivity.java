package com.artigile.android.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.artigile.android.R;
import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * @author ivanbahdanau
 */
public class TutorialActivity extends FragmentActivity {
    private ArrayList<Integer> imagesId = Lists.newArrayList();
    private ArrayList<Integer> textsIds = Lists.newArrayList();

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;


    public TutorialActivity() {
        imagesId.add(R.drawable.game_preview);
        imagesId.add(R.drawable.touch_to_show_menu);
        textsIds.add(R.string.tutorial_page_1);
        textsIds.add(R.string.tutorial_page_2);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 2 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TutorialFragment tutorialFragment = TutorialFragment.newInstance();
            tutorialFragment.setImageId(imagesId.get(position));
            tutorialFragment.setTextId(textsIds.get(position));
            tutorialFragment.setDisplayNextPageImg(position == 0);
            return tutorialFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}