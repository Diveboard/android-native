package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.diveboard.mobile.editdive.TabEditSpotsActivity;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class DiveDetailsActivity extends TabActivity {
	private DiveboardModel mModel;
	private TabHost	 mTabHost;
	private Typeface mFaceB;
	private Typeface mFaceR;
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
		if (AC.getRefresh() == 2)
		{
			AC.setRefresh(1);
			finish();
			startActivity(getIntent());
		}
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		//super.onActivityResult(requestCode, resultCode, data);
//		
//		onCreate(null);
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationController AC = (ApplicationController)getApplicationContext();
		if (AC.handleLowMemory() == true)
			return ;
		// Set the action bar
		setContentView(R.layout.activity_dive_details);
		ViewTreeObserver vto = ((ViewGroup)findViewById(R.id.root)).getViewTreeObserver(); 
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override 
		    public void onGlobalLayout() {
		    	((ViewGroup)findViewById(R.id.root)).getViewTreeObserver().removeGlobalOnLayoutListener(this);
		    	
				
				ApplicationController AC = ((ApplicationController)getApplicationContext());
				mModel = AC.getModel();
				mTabHost = (TabHost)findViewById(android.R.id.tabhost);
				mFaceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
				mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
				//We built the tabs
				Intent intent = new Intent(DiveDetailsActivity.this, DiveDetailsMainActivity.class);
				intent.putExtra("index", AC.getPageIndex());
				setupTab(getResources().getString(R.string.tab_details_label), intent, R.drawable.ic_details_grey);

				intent = new Intent(DiveDetailsActivity.this, PhotosActivity.class);
				intent.putExtra("index", AC.getPageIndex());
				setupTab(getResources().getString(R.string.tab_photos_label), intent, R.drawable.ic_photos_white);

//				intent = new Intent(DiveDetailsActivity.this, EmptyActivity.class);
//				setupTab(getResources().getString(R.string.tab_species_label), intent, R.drawable.ic_species_white);
//
				intent = new Intent(DiveDetailsActivity.this, MapActivity.class);
				intent.putExtra("index", AC.getPageIndex());
				setupTab(getResources().getString(R.string.tab_map_label), intent, R.drawable.ic_map_white);

				((TextView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsText)).setTypeface(mFaceB);
				mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
					@Override
					public void onTabChanged(String tabId) {
						TabWidget tab_widget = mTabHost.getTabWidget();
						for (int i = 0; i < tab_widget.getTabCount(); i++)
						{
							if (i == 0)
							{
								((ImageView)(tab_widget.getChildTabViewAt(i)).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_details_white));
							}
							else if (i == 1)
							{
								((ImageView)(tab_widget.getChildTabViewAt(i)).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_photos_white));
							}
//							else if (i == 2)
//							{
//								((ImageView)(tab_widget.getChildTabViewAt(i)).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_species_white));
//							}
							else if (i == 2)
							{
								((ImageView)(tab_widget.getChildTabViewAt(i)).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_map_white));
							}
							((TextView)(tab_widget.getChildTabViewAt(i)).findViewById(R.id.tabsText)).setTypeface(mFaceR);
						}
						((TextView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsText)).setTypeface(mFaceB);
						if (mTabHost.getCurrentTab() == 0)
							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_details_grey));
						else if (mTabHost.getCurrentTab() == 1)
						{
							ApplicationController AC = (ApplicationController)getApplicationContext();
							if (AC.getModel().getDives().get(AC.getPageIndex()).getPictures() != null && AC.getModel().getDives().get(AC.getPageIndex()).getPictures().size() != 0)
							{
								Intent galleryCarousel = new Intent(DiveDetailsActivity.this, GalleryCarouselActivity.class);
								galleryCarousel.putExtra("index", AC.getPageIndex());
								startActivity(galleryCarousel);
								mTabHost.setCurrentTab(0);
							}
							else
							{
								((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_photos_grey));
							}
						}
//						else if (mTabHost.getCurrentTab() == 2)
//							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_species_grey));
						else if (mTabHost.getCurrentTab() == 2)
							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_map_grey));
						if (mTabHost.getCurrentTab() == 0)
							((ScrollView)findViewById(R.id.scroll)).smoothScrollTo(0, 0);
					}
				});
				if (mModel.getDives().get(getIntent().getIntExtra("index", 0)).getTripName() == null)
				{
					((TextView)findViewById(R.id.trip_name)).setVisibility(View.GONE);
				}
				else
				{
					((TextView)findViewById(R.id.trip_name)).setText(mModel.getDives().get(getIntent().getIntExtra("index", 0)).getTripName());
					((TextView)findViewById(R.id.trip_name)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
					((TextView)findViewById(R.id.trip_name)).setTypeface(mFaceB);
				}
				String place_name = "";
				if (mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getId() != 1 && mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName() != null)
					place_name += mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getLocationName() + ", " + mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName();
				else if (mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getLocationName() == null && mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName() != null)
					place_name += mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName();
				else if (mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getLocationName() != null && mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName() == null)
					place_name += mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getLocationName();
				((TextView)findViewById(R.id.place_name)).setText(place_name);
				((TextView)findViewById(R.id.place_name)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				((TextView)findViewById(R.id.place_name)).setTypeface(mFaceR);
				if (mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getLocationName() == null && mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName() == null)
					((TextView)findViewById(R.id.place_name)).setVisibility(View.GONE);
				((ScrollView)findViewById(R.id.scroll)).smoothScrollTo(0, 0);
				DownloadImageTask task = new DownloadImageTask();
				task.execute(AC.getPageIndex());
		    }
		});
	}
	
	private Bitmap			scaleBitmap(final int resource)
	{
		return BitmapFactory.decodeResource(getResources(), resource);
	}
	
	private void				setupTab(final String tag, final Intent content, final int resource)
	{
		View tabview = createTabView(mTabHost.getContext(), tag, resource);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
		setContent.setContent(content);
		mTabHost.addTab(setContent);
	}
	
	private View				createTabView(final Context context, final String text, final int resource)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg_dive_details, null);
		TextView tv = ((TextView)view.findViewById(R.id.tabsText));
		tv.setTextSize(12);
		tv.setText(text);
		tv.setTypeface(mFaceR);
		ImageView imageview = ((ImageView)view.findViewById(R.id.tabsIcon));
		imageview.setImageResource(resource);
		return view;
	}
	
	private class DownloadImageTask extends AsyncTask<Integer, Void, Bitmap>
	{
		private Integer mItemNb;
		
		public DownloadImageTask()
		{
		}
		
		protected Bitmap doInBackground(Integer... args)
		{
			mItemNb = args[0];
			Bitmap result = null;
			try {
				if (mModel.getDives().get(mItemNb).getFeaturedPicture() != null)
					result = mModel.getDives().get(mItemNb).getFeaturedPicture().getPicture(getApplicationContext(), Picture.Size.THUMB);
				else if (mModel.getDives().get(mItemNb).getThumbnailImageUrl() != null)
					result = mModel.getDives().get(mItemNb).getThumbnailImageUrl().getPicture(getApplicationContext());
				else
					result = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result != null)
			{
				try
				{
					return ImageHelper.fastblur(result, 30);
				}
				catch (OutOfMemoryError e)
				{
					return null;
				}
			}
				
			return null;
		}
		
		/**
		 * We do here the fade in/out animation for the background
		 */
		@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
		protected void onPostExecute(Bitmap result)
		{
			if (result != null)
			{
				((LinearLayout)findViewById(R.id.root)).setBackgroundDrawable(new BitmapDrawable(getResources(), result));
			}
		}
	}
}
