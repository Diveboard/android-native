package com.diveboard.mobile;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.FilePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.ScreenSetup;
import com.google.analytics.tracking.android.EasyTracker;

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
	private GalleryViewPager mPager;
	private FilePagerAdapter mPagerAdapter;
	// Number of pages in Dives
	private int mNbPages = 1;
	// Model to display
	private DiveboardModel mModel;
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// Set the action bar
		setContentView(R.layout.activity_gallery_carousel);
		ApplicationController AC = (ApplicationController)getApplicationContext();
		if (AC.handleLowMemory() == true)
			return ;
		mModel = AC.getModel();
		List<Picture> items = mModel.getDives().get(getIntent().getIntExtra("index", 0)).getPictures();
		
//		for (int i = 0; i < mModel.getDives().get(getIntent().getIntExtra("index", 0)).getPictures().size(); i++)
//		{
//			String filepath = mModel.getDives().get(getIntent().getIntExtra("index", 0)).getPictures().get(i).getLocalPath(getApplicationContext(), Picture.Size.LARGE);
//			System.out.println(filepath);
//			items.add(filepath);
//		}
//		String[] urls = {
//                "http://cs407831.userapi.com/v407831207/18f6/jBaVZFDhXRA.jpg",
//                "http://cs407831.userapi.com/v4078f31207/18fe/4Tz8av5Hlvo.jpg",
//                "http://cs407831.userapi.com/v407831207/1906/oxoP6URjFtA.jpg",
//                "http://cs407831.userapi.com/v407831207/190e/2Sz9A774hUc.jpg",
//                "http://cs407831.userapi.com/v407831207/1916/Ua52RjnKqjk.jpg",
//                "http://cs407831.userapi.com/v407831207/191e/QEQE83Ok0lQ.jpg"
//        };
        //Collections.addAll(items, urls);		
		mPager = (GalleryViewPager) findViewById(R.id.pager);
		//mNbPages = mModel.getDives().get(getIntent().getIntExtra("index", 0)).getPictures().size();
		mPagerAdapter = new FilePagerAdapter(getApplicationContext(), items);
		//mPagerAdapter = new GalleryCarouselPagerAdapter(getSupportFragmentManager(), mModel.getDives());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(AC.getCarouselIndex());
        mPager.setOffscreenPageLimit(2);
        mPager.setPageTransformer(true, new DepthPageTransformer());
//        mPager.setOnPageChangeListener(new OnPageChangeListener()
//        {
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//				if (arg0 == 0)
//				{
//					ApplicationController AC = ((ApplicationController)getApplicationContext());
//					AC.setCarouselIndex(mPager.getCurrentItem());
//				}
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {	
//			}
//
//			@Override
//			public void onPageSelected(int arg0) {
//			}
//        	
//        });
	}
	
	@Override
	public void onBackPressed() {
		ApplicationController AC = ((ApplicationController)getApplicationContext());
		//AC.setCarouselIndex(0);
		finish();
	}
	
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		ApplicationController AC = ((ApplicationController)getApplicationContext());
//		if (mPager != null)
//			AC.setCarouselIndex(mPager.getCurrentItem());
//		else
//			AC.setCarouselIndex(0);
//	}

	/**
	 * Generate the dives pages
	 */
//	private class GalleryCarouselPagerAdapter extends FragmentStatePagerAdapter
//	{
//		private ArrayList<Dive> mDives;
//		
//		public GalleryCarouselPagerAdapter(FragmentManager fragmentManager, ArrayList<Dive> dives) {
//            super(fragmentManager);
//            mDives = dives;
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return new GalleryCarouselFragment(mModel.getDives().get(getIntent().getIntExtra("index", 0)), position);
//        }
//
//        @Override
//        public int getCount() {
//            return mNbPages;
//        }
//	}
}
