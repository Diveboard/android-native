package com.diveboard.mobile;


import java.util.ArrayList;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.ScreenSetup;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
		// Set the action bar
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			getActionBar().hide();
		}
		else
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		setContentView(R.layout.activity_gallery_carousel);
		ApplicationController AC = (ApplicationController)getApplicationContext();
		mModel = AC.getModel();
		mPager = (ViewPager) findViewById(R.id.pager);
		mNbPages = mModel.getDives().get(getIntent().getIntExtra("index", -1)).getPictures().size();
		mPagerAdapter = new GalleryCarouselPagerAdapter(getSupportFragmentManager(), mModel.getDives());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(AC.getCarouselIndex());
        mPager.setOffscreenPageLimit(2);
        mPager.setCurrentItem(AC.getCarouselIndex());
        mPager.setPageTransformer(true, new DepthPageTransformer());
        mPager.setOnPageChangeListener(new OnPageChangeListener()
        {
			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (arg0 == 0)
				{
					ApplicationController AC = ((ApplicationController)getApplicationContext());
					AC.setCarouselIndex(mPager.getCurrentItem());
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {	
			}

			@Override
			public void onPageSelected(int arg0) {
			}
        	
        });
	}
	
	@Override
	public void onBackPressed() {
		ApplicationController AC = ((ApplicationController)getApplicationContext());
		AC.setCarouselIndex(0);
		finish();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		ApplicationController AC = ((ApplicationController)getApplicationContext());
		if (mPager != null)
			AC.setCarouselIndex(mPager.getCurrentItem());
		else
			AC.setCarouselIndex(0);
	}

	/**
	 * Generate the dives pages
	 */
	private class GalleryCarouselPagerAdapter extends FragmentStatePagerAdapter
	{
		private ArrayList<Dive> mDives;
		
		public GalleryCarouselPagerAdapter(FragmentManager fragmentManager, ArrayList<Dive> dives) {
            super(fragmentManager);
            mDives = dives;
        }

        @Override
        public Fragment getItem(int position) {
            return new GalleryCarouselFragment(mModel.getDives().get(getIntent().getIntExtra("index", -1)), position);
        }

        @Override
        public int getCount() {
            return mNbPages;
        }
	}
}
