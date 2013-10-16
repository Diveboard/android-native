package com.diveboard.mobile;


import java.util.ArrayList;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.ScreenSetup;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class GalleryCarouselActivity extends FragmentActivity {
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	// Number of pages in Dives
	private int mNbPages = 1;
	// Model to display
	private DiveboardModel mModel;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_gallery_carousel);
		ApplicationController AC = (ApplicationController)getApplicationContext();
		mModel = AC.getModel();
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new GalleryCarouselPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(2);
        mNbPages = mModel.getDives().get(getIntent().getIntExtra("index", -1)).getPictures().size();
	}

	/**
	 * Generate the dives pages
	 */
	private class GalleryCarouselPagerAdapter extends FragmentStatePagerAdapter
	{
		private ArrayList<Dive> mDives;
		private ScreenSetup mScreenSetup;
		private Bitmap mRoundedLayer;
		private Bitmap mRoundedLayerSmall;
		
		public GalleryCarouselPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return new GalleryCarouselFragment();
        }

        @Override
        public int getCount() {
            return mNbPages;
        }
	}
}
