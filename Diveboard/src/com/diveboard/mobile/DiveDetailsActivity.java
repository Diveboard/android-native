package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;

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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class DiveDetailsActivity extends TabActivity {
	private DiveboardModel mModel;
	private TabHost	 mTabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the action bar
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			getActionBar().hide();
		}
		else
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		setContentView(R.layout.activity_dive_details);
		ApplicationController AC = ((ApplicationController)getApplicationContext());
		mModel = AC.getModel();
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		Intent intent = new Intent(this, TabEditGearActivity.class);
	    //intent.putExtra("index", mIndex);
	    setupTab(new TextView(this), getResources().getString(R.string.tab_details_label), intent);
	    	    
	    intent = new Intent(this, TabEditGearActivity.class);
	    setupTab(new TextView(this), getResources().getString(R.string.tab_photos_label), intent);
	
	    intent = new Intent(this, TabEditGearActivity.class);
	    setupTab(new TextView(this), getResources().getString(R.string.tab_species_label), intent);
	    
	    intent = new Intent(this, TabEditGearActivity.class);
	    setupTab(new TextView(this), getResources().getString(R.string.tab_map_label), intent);
		Typeface faceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
		Typeface faceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
		((TextView)findViewById(R.id.trip_name)).setText(mModel.getDives().get(getIntent().getIntExtra("index", -1)).getTripName());
		((TextView)findViewById(R.id.trip_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
		((TextView)findViewById(R.id.trip_name)).setTypeface(faceB);
		((TextView)findViewById(R.id.place_name)).setText(mModel.getDives().get(getIntent().getIntExtra("index", -1)).getSpot().getLocationName() + ", " + mModel.getDives().get(getIntent().getIntExtra("index", -1)).getSpot().getCountryName());
		((TextView)findViewById(R.id.place_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
		((TextView)findViewById(R.id.place_name)).setTypeface(faceR);
		DownloadImageTask task = new DownloadImageTask();
		task.execute(AC.getPageIndex());
		//mModel.getDives().get(getIntent().getIntExtra("index", -1));
	}
	
	private void				setupTab(final View view, final String tag, final Intent content)
	{
		View tabview = createTabView(mTabHost.getContext(), tag);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(content);
		mTabHost.addTab(setContent);
	}
	
	private View				createTabView(final Context context, final String text)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		Typeface mFaceB = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Bold.otf");
		tv.setTypeface(mFaceB);
		tv.setTextSize(12);
		tv.setText(text);
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
				else
					result = mModel.getDives().get(mItemNb).getThumbnailImageUrl().getPicture(getApplicationContext());
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
				((RelativeLayout)findViewById(R.id.root)).setBackgroundDrawable(new BitmapDrawable(getResources(), result));
			}
		}
	}
}
