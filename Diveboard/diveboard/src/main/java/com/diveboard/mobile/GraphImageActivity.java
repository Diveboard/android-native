package com.diveboard.mobile;

import java.util.ArrayList;
import java.util.List;

import com.diveboard.GalleryWidget.FilePagerAdapter;
import com.diveboard.GalleryWidget.GalleryViewPager;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class GraphImageActivity extends FragmentActivity {
	private GalleryViewPager mPager;
	private FilePagerAdapter mPagerAdapter;
	// Number of pages in Dives
	private int mNbPages = 1;
	// Model to display
	private DiveboardModel mModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph_image);
		ApplicationController AC = (ApplicationController)getApplicationContext();
		if (AC.handleLowMemory() == true)
			return ;
		mModel = AC.getModel();
		List<Picture> items = new ArrayList<Picture>();
		items.add(mModel.getDives().get(getIntent().getIntExtra("index", 0)).getProfileV3());
		mPager = (GalleryViewPager) findViewById(R.id.pager);
		mPagerAdapter = new FilePagerAdapter(getApplicationContext(), items);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(2);
        //mPager.setPageTransformer(true, new DepthPageTransformer());
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
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
	}

	@Override
	public void onBackPressed() {
		ApplicationController AC = ((ApplicationController)getApplicationContext());
		//AC.setCarouselIndex(0);
		finish();
	}

}
