package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import com.diveboard.mobile.newdive.NewDiveActivity;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.util.ExitDialog;
import com.facebook.Session;

import android.R.id;
import android.net.Uri;
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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
	private int mIndex;

	//controls for navigation drawer
	private DrawerLayout 					mDrawerLayout;
	protected ListView 						mDrawerList;
	private LinearLayout 					mDrawerContainer;
	protected ArrayList<String> 			mLinksTitles;
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
		if (AC.getRefresh() == 2)
		{
			AC.setRefresh(3);
			finish();
			startActivity(getIntent());
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		((ApplicationController) getApplication()).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		((ApplicationController) getApplication()).activityStop(this);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            navigateTo(position);
        }
    }
    
    public void navigateTo(final int position) {
    	final ApplicationController AC = (ApplicationController)getApplicationContext();
    	int currDive = AC.getModel().getDives().size() - AC.getPageIndex() - 1;
    	Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
    	//Check there are not unsaved changes
    	if (mModel.getDives().size() > 0 &&
    			(currDive >= 0 && mModel.getDives().get(currDive).getEditList().size() > 0)
    			|| (mModel.getUser().getEditList().size() > 0)
    			|| (mDive != null && mDive.getEditList() != null && mDive.getEditList().size() > 0)) 
    	{			
    		final ExitDialog saveDialog = new ExitDialog(this);
    		saveDialog.setTitle(getResources().getString(R.string.exit_title));
    		saveDialog.setBody(getResources().getString(R.string.edit_confirm_title));
    		saveDialog.setPositiveListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mModel.getUser().clearEditList();
			    	Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
			    	if (mDive != null)
			    		mDive.clearEditList();
			    	int currDive = AC.getModel().getDives().size() - AC.getPageIndex() - 1;
			    	if(currDive >= 0){
			    		mModel.getDives().get(currDive).clearEditList(); 
			    	}
			    	navigateTo(position);
			    	saveDialog.dismiss();
				}
			});
    		
    		saveDialog.setNegativeListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mDrawerLayout.closeDrawer(mDrawerContainer);
			    	mDrawerList.setItemChecked(position, false);
			    	saveDialog.dismiss();
				}
			});
    		
    		saveDialog.show();
    	}else{
    		switch (position) {	
    		// Logbook
    		case 0:
    			if(!((Activity)this instanceof DivesActivity)){
    				((ApplicationController)getApplicationContext()).setRefresh(1);
    				finish();
    			}

    			break;

    		// New Dive
    		case 1:
    			if(!((Activity)this instanceof NewDiveActivity)){
    				Intent newDiveActivity = new Intent(this, NewDiveActivity.class);
    				startActivity(newDiveActivity);
    				if(!((Activity)this instanceof DivesActivity))
    					finish();
    			}
    			break;

    		// Wallet Activity
    		case 2:
    			if(!((Activity)this instanceof WalletActivity)){
    				Intent walletActivity = new Intent(this, WalletActivity.class);
    				startActivity(walletActivity);
    				if(!((Activity)this instanceof DivesActivity))
    					finish();
    			}
    			break;

    		// Closest Shop
    		case 3:
    			if(!((Activity)this instanceof ClosestShopActivity)){
    				Intent closestShopActivity = new Intent(this, ClosestShopActivity.class);
    				startActivity(closestShopActivity);
    				if(!((Activity)this instanceof DivesActivity))
    					finish();
    			}
    			break;



    		// Refresh
    		case 4:
    			AC.setDataReady(false);
    			AC.getModel().stopPreloadPictures();
    			ApplicationController.mForceRefresh = true;
    			AC.setModel(null);
    			finish();
    			break;

    		// Settings
    		case 5:
    			Intent settingsActivity = new Intent(this, SettingsActivity.class);
    			startActivity(settingsActivity);
    			if(!((Activity)this instanceof DivesActivity))
    				finish();
    			break;

    		// Logout
    		case 6:
    			final ExitDialog exitDialog = new ExitDialog(this);
    			exitDialog.setTitle(getResources().getString(R.string.exit_title));
    			exitDialog.setBody(getResources().getString(R.string.confirm_logout));
    			exitDialog.setPositiveListener(new View.OnClickListener() {

    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					logout();
    				}
    			});

    			exitDialog.setNegativeListener(new View.OnClickListener() {

    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					exitDialog.dismiss();
    				}
    			});

    			exitDialog.show();
    			break;

    		// Rate app
    		case 7:
    			mModel.setHasRatedApp(true);
    			try {
    				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + AppRater.APP_PNAME)));
    			} catch (android.content.ActivityNotFoundException anfe) {
    				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + AppRater.APP_PNAME)));
    			}
    			break;

    		default:
    			break;

    		}

    		// update selected item and title, then close the drawer
    		mDrawerList.setItemChecked(position, false);
    		mDrawerLayout.closeDrawer(mDrawerContainer);
    	}
    }

    public void logout()
   	{
   		if (Session.getActiveSession() != null)
   			Session.getActiveSession().closeAndClearTokenInformation();
   		Session.setActiveSession(null);
   		ApplicationController AC = (ApplicationController)getApplicationContext();
       	AC.setDataReady(false);
       	AC.setPageIndex(0);
       	AC.getModel().doLogout();
       	Intent loginActivity = new Intent(this, DiveboardLoginActivity.class);
       	loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
       	startActivity(loginActivity);
       	
   	}
    
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
				mFaceR = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
				mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
				mIndex = getIntent().getIntExtra("index", 0);
				//We built the tabs
				Intent intent = new Intent(DiveDetailsActivity.this, DiveDetailsMainActivity.class);
				intent.putExtra("index", mIndex);
				setupTab(getResources().getString(R.string.tab_details_label), intent, R.drawable.ic_details_grey);

				intent = new Intent(DiveDetailsActivity.this, PhotosActivity.class);
				intent.putExtra("index", mIndex);
				setupTab(getResources().getString(R.string.tab_photos_label), intent, R.drawable.ic_photos_white);

//				intent = new Intent(DiveDetailsActivity.this, EmptyActivity.class);
//				setupTab(getResources().getString(R.string.tab_species_label), intent, R.drawable.ic_species_white);
//
				intent = new Intent(DiveDetailsActivity.this, MapActivity.class);
				intent.putExtra("index", mIndex);
				setupTab(getResources().getString(R.string.tab_map_label), intent, R.drawable.ic_map_white);

				((TextView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsText)).setTypeface(mFaceB);
				mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
					@Override
					public void onTabChanged(String tabId) {
						TabWidget tab_widget = mTabHost.getTabWidget();
						ApplicationController AC = (ApplicationController)getApplicationContext();
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
						{
							AC.setCurrentTab(0);
							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_details_grey));
							((LinearLayout)findViewById(R.id.white_banner)).setVisibility(View.VISIBLE);
						}
						else if (mTabHost.getCurrentTab() == 1)
						{
							
							AC.setCurrentTab(1);
							if (AC.getModel().getDives().get(mIndex).getPictures() != null
									&& AC.getModel().getDives().get(mIndex).getPictures().size() != 0)
							{
								((FrameLayout)findViewById(id.tabcontent)).setBackgroundColor(Color.TRANSPARENT);
							}
							else
								((FrameLayout)findViewById(id.tabcontent)).setBackgroundColor(Color.WHITE);
							if (AC.getModel().getDives().get(mIndex).getPictures() != null
									&& AC.getModel().getDives().get(mIndex).getPictures().size() != 0)
							{
								((LinearLayout)findViewById(R.id.white_banner)).setVisibility(View.GONE);
							}
							else
							{
								((LinearLayout)findViewById(R.id.white_banner)).setVisibility(View.VISIBLE);
							}
							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_photos_grey));
						}
//						else if (mTabHost.getCurrentTab() == 2)
//							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_species_grey));
						else if (mTabHost.getCurrentTab() == 2)
						{
							AC.setCurrentTab(2);
							((ImageView)(mTabHost.getCurrentTabView()).findViewById(R.id.tabsIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_map_grey));
							if ((AC.getModel().getDives().get(mIndex).getLat() == null || AC.getModel().getDives().get(mIndex).getLng() == null)
									|| AC.getModel().getDives().get(mIndex).getLat() == 0 && AC.getModel().getDives().get(mIndex).getLng() == 0)
								((LinearLayout)findViewById(R.id.white_banner)).setVisibility(View.VISIBLE);
							else
								((LinearLayout)findViewById(R.id.white_banner)).setVisibility(View.GONE);
						}
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
				if (!mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getJson().isNull("id") && mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getId() != 1){
					if(mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getLocationName() != null){
						place_name += mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getLocationName();
						if(mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName() != null)
							place_name += ", " + mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName();
					}
					else if(mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName() != null)
						place_name += mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName();
				}
				
				((TextView)findViewById(R.id.place_name)).setText(place_name);
				((TextView)findViewById(R.id.place_name)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				((TextView)findViewById(R.id.place_name)).setTypeface(mFaceR);
				if (mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getLocationName() == null && mModel.getDives().get(getIntent().getIntExtra("index", 0)).getSpot().getCountryName() == null)
					((TextView)findViewById(R.id.place_name)).setVisibility(View.GONE);
				((ScrollView)findViewById(R.id.scroll)).smoothScrollTo(0, 0);
				mTabHost.setCurrentTab(AC.getCurrentTab());
				DownloadImageTask task = new DownloadImageTask();
				task.execute(mIndex);
		    }
		});
		
		//Set up the navDrawer
		mLinksTitles = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.menu_links_has_rated_nobug)));
		if((AC.getModel().hasRatedApp() != null && !AC.getModel().hasRatedApp()))
			mLinksTitles.add(getString(R.string.menu_links_has_not_rated));
		//Setting up controls for the navigation drawer 
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerContainer = (LinearLayout) findViewById(R.id.left_drawer_cont);
		mDrawerList = (ListView) findViewById(R.id.menu_links);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		final Typeface faceR = ((ApplicationController)getApplicationContext()).getModel().getLatoR();
		
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(AC, R.layout.drawer_list_item, mLinksTitles){
			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
        		if (convertView == null) {  
        			convertView = inflater.inflate(R.layout.drawer_list_item, null);  
        		} 
        		LinearLayout ll = (LinearLayout) convertView;
        		View line = (View)ll.getChildAt(0);
        		TextView t = (TextView)ll.getChildAt(1);
				t.setTypeface(faceR);
				t.setText(mLinksTitles.get(position));
				line.setVisibility(View.GONE);
				
				if(position == 0)
					line.setVisibility(View.VISIBLE);

				return convertView;
			}
		});
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		ImageView mDrawerTitle = (ImageView)findViewById(R.id.drawer_title);
		mDrawerTitle.setImageDrawable(getResources().getDrawable(R.drawable.logo_250));
		ImageView mDrawerMenu = (ImageView)findViewById(R.id.ic_drawer);
		if(mDrawerMenu != null)
			mDrawerMenu.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mDrawerLayout.openDrawer(mDrawerContainer);
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
