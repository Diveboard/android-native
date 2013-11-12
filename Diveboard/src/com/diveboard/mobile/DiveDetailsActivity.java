package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.diveboard.mobile.editdive.TabEditGearActivity;
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
	private Bitmap mDetailsGrey;
	private Bitmap mDetailsWhite;
	private Bitmap mPhotosGrey;
	private Bitmap mPhotosWhite;
	private Bitmap mSpeciesGrey;
	private Bitmap mSpeciesWhite;
	private Bitmap mMapGrey;
	private Bitmap mMapWhite;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
				mDetailsGrey = scaleBitmap(R.drawable.ic_details_grey);
				mDetailsWhite = scaleBitmap(R.drawable.ic_details_white);
				mPhotosGrey = scaleBitmap(R.drawable.ic_photos_grey);
				mPhotosWhite = scaleBitmap(R.drawable.ic_photos_white);
				mSpeciesGrey = scaleBitmap(R.drawable.ic_species_grey);
				mSpeciesWhite = scaleBitmap(R.drawable.ic_species_white);
				mMapGrey = scaleBitmap(R.drawable.ic_map_grey);
				mMapWhite = scaleBitmap(R.drawable.ic_map_white);
				//We built the tabs
				Intent intent = new Intent(DiveDetailsActivity.this, DiveDetailsMainActivity.class);
				intent.putExtra("index", AC.getPageIndex());
				setupTab(new TextView(DiveDetailsActivity.this), getResources().getString(R.string.tab_details_label), intent, mDetailsGrey);

				intent = new Intent(DiveDetailsActivity.this, TabEditGearActivity.class);
				setupTab(new TextView(DiveDetailsActivity.this), getResources().getString(R.string.tab_photos_label), intent, mPhotosWhite);

				intent = new Intent(DiveDetailsActivity.this, TabEditGearActivity.class);
				setupTab(new TextView(DiveDetailsActivity.this), getResources().getString(R.string.tab_species_label), intent, mSpeciesWhite);

				intent = new Intent(DiveDetailsActivity.this, TabEditGearActivity.class);
				setupTab(new TextView(DiveDetailsActivity.this), getResources().getString(R.string.tab_map_label), intent, mMapWhite);
				((TextView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsText)).setTypeface(mFaceB);
				mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
					@Override
					public void onTabChanged(String tabId) {
						TabWidget tab_widget = mTabHost.getTabWidget();
						for (int i = 0; i < tab_widget.getTabCount(); i++)
						{
							if (i == 0)
							{
								((ImageView)(tab_widget.getChildTabViewAt(i)).findViewById(R.id.tabsIcon)).setImageBitmap(mDetailsWhite);
							}
							else if (i == 1)
							{
								((ImageView)(tab_widget.getChildTabViewAt(i)).findViewById(R.id.tabsIcon)).setImageBitmap(mPhotosWhite);
							}
							else if (i == 2)
							{
								((ImageView)(tab_widget.getChildTabViewAt(i)).findViewById(R.id.tabsIcon)).setImageBitmap(mSpeciesWhite);
							}
							else if (i == 3)
							{
								((ImageView)(tab_widget.getChildTabViewAt(i)).findViewById(R.id.tabsIcon)).setImageBitmap(mMapWhite);
							}
							((TextView)(tab_widget.getChildTabViewAt(i)).findViewById(R.id.tabsText)).setTypeface(mFaceR);
						}
						((TextView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsText)).setTypeface(mFaceB);
						if (mTabHost.getCurrentTab() == 0)
							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageBitmap(mDetailsGrey);
						else if (mTabHost.getCurrentTab() == 1)
							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageBitmap(mPhotosGrey);
						else if (mTabHost.getCurrentTab() == 2)
							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageBitmap(mSpeciesGrey);
						else if (mTabHost.getCurrentTab() == 3)
							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageBitmap(mMapGrey);
						if (mTabHost.getCurrentTab() == 0)
							((ScrollView)findViewById(R.id.scroll)).smoothScrollTo(0, 0);
					}
				});
				((TextView)findViewById(R.id.trip_name)).setText(mModel.getDives().get(getIntent().getIntExtra("index", 0)).getTripName());
				((TextView)findViewById(R.id.trip_name)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				((TextView)findViewById(R.id.trip_name)).setTypeface(mFaceB);
				((TextView)findViewById(R.id.place_name)).setText(mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getLocationName() + ", " + mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName());
				((TextView)findViewById(R.id.place_name)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				((TextView)findViewById(R.id.place_name)).setTypeface(mFaceR);
				((ScrollView)findViewById(R.id.scroll)).smoothScrollTo(0, 0);
				DownloadImageTask task = new DownloadImageTask();
				task.execute(AC.getPageIndex());
		    }
		});
	}
	
	private void				setupTab(final View view, final String tag, final Intent content, final Bitmap bitmap)
	{
		View tabview = createTabView(mTabHost.getContext(), tag, bitmap);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
		setContent.setContent(content);
		mTabHost.addTab(setContent);
	}
	
	private Bitmap				scaleBitmap(final int drawable)
	{
		Bitmap output = BitmapFactory.decodeResource(getResources(), drawable);
//		Bitmap bitmap = Bitmap.createScaledBitmap(output, 200, 200, true);
//		output.recycle();
		return output;
	}
	
	private View				createTabView(final Context context, final String text, final Bitmap bitmap)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg_dive_details, null);
		TextView tv = ((TextView)view.findViewById(R.id.tabsText));
		tv.setTextSize(12);
		tv.setText(text);
		tv.setTypeface(mFaceR);
		ImageView imageview = ((ImageView)view.findViewById(R.id.tabsIcon));
		imageview.setImageBitmap(bitmap);
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
				((LinearLayout)findViewById(R.id.root)).setBackgroundDrawable(new BitmapDrawable(getResources(), result));
			}
		}
	}
}
