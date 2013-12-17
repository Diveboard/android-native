package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.diveboard.mobile.editdive.EditDiveActivity;
import com.diveboard.mobile.newdive.NewDiveActivity;
import com.diveboard.model.DataRefreshListener;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.ScreenSetup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.facebook.*;
import com.facebook.model.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.uservoice.uservoicesdk.UserVoice;


public class DivesActivity extends FragmentActivity implements TaskFragment.TaskCallbacks
{

	// Number of pages in Dives
	private int mNbPages = 1;
	
	// All you need to make the carousel
	private ScreenSetup mScreenSetup;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private ViewGroup mLayout;
	private SeekBar mSeekBar;
	private RelativeLayout mScreen;
	private ImageView mBackground1;
	private ImageView mBackground2;
	private	 int mBackground = 1;
	
	//Tracking bar
	public class TrackingBarPosition
	{
		int x;
		int y;
		int X;
		int Y;
		public TrackingBarPosition(int x, int X, int y, int Y)
		{
			this.x = x;
			this.y = y;
			this.X = X;
			this.Y = Y;
		}
		public int getx() {
			return x;
		}
		public void setx(int x) {
			this.x = x;
		}
		public int gety() {
			return y;
		}
		public void sety(int y) {
			this.y = y;
		}
		public int getX() {
			return X;
		}
		public void setX(int x) {
			X = x;
		}
		public int getY() {
			return Y;
		}
		public void setY(int y) {
			Y = y;
		}

	}
	private Integer max_strokes_possible;
	private Double nb_dives_per_stroke;
	private Integer nb_strokes = 0;
	private Integer position_stroke;
	private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    private OnTouchListener mGestureListener;
    private boolean mIsScrolling = false;
    private ArrayList<TrackingBarPosition> mTrackingBarPosition = new ArrayList<TrackingBarPosition>();
	
	// Thread for the data loading & the views associated
	private TaskFragment mTaskFragment;
	//private LoadDataTask mAuthTask = null;
	private View mLoadDataFormView;
	private View mLoadDataStatusView;
	private TextView mLoadDataStatusMessageView;
	// Model to display
	private DiveboardModel mModel;
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
		AC.setCurrentTab(0);
		if (AC.getRefresh() == 1)
		{
////			AC.setPageIndex(AC.getPageIndex() + 1);
//			AC.setPageIndex(0);
//			AC.setDataReady(false);
//			AC.setRefresh(0);
//			finish();
//			startActivity(getIntent());
			
			AC.setRefresh(0);
			AC.setPageIndex(0);
			AC.setDataReady(false);
			AC.getModel().stopPreloadPictures();
			AC.setModel(null);
			finish();
		}
		else if (AC.getRefresh() == 3)
		{
//			AC.setDataReady(false);
//			AC.setRefresh(0);
//			finish();
//			startActivity(getIntent());
			
			AC.setRefresh(0);
			AC.setDataReady(false);
			AC.getModel().stopPreloadPictures();
			AC.setModel(null);
			finish();
		}
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
	//@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		// Set the action bar
		ApplicationController AC = (ApplicationController)getApplicationContext();
		if (AC.getModel() == null)
			System.out.println("model null");
		
		setContentView(R.layout.activity_dives);
		mLoadDataFormView = findViewById(R.id.load_data_form);
		mLoadDataStatusView = findViewById(R.id.load_data_status);
		mLoadDataStatusMessageView = (TextView) findViewById(R.id.load_data_status_message);
		// Initialize data
		mModel = AC.getModel();
		// Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this,new MyGestureListener());
        
		if (AC.isDataReady() == false)
			loadData();
		else
			createPages();
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
    	finish();
	}
	
	@Override
	public void onBackPressed() {
	}
	
	// The three methods below are called by the TaskFragment when new
	// progress updates or results are available. The MainActivity 
	// should respond by updating its UI to indicate the change.
	@Override
	public void onPreExecute()
	{
	}
	
	@Override
	public void onCancelled()
	{
		mTaskFragment = null;
		showProgress(false);
	}
	
	@Override
	public void onPostExecute(final Boolean success)
	{
		mTaskFragment = null;
		showProgress(false);

		if (success == true) {
			mModel.preloadPictures();
			createPages();
		}
		else
		{
			//finish();
//			System.out.println("logout");
//			logout();
		}
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event)
//	{
//		int action = MotionEventCompat.getActionMasked(event);
//		final String DEBUG_TAG = "DivesActivity";
//		
//		switch(action) {
//			case (MotionEvent.ACTION_DOWN) :
//	            Log.d(DEBUG_TAG,"Action was DOWN");
//	            return true;
//	        case (MotionEvent.ACTION_MOVE) :
//	            Log.d(DEBUG_TAG,"Action was MOVE");
//	            return true;
//	        case (MotionEvent.ACTION_UP) :
//	            Log.d(DEBUG_TAG,"Action was UP");
//	            return true;
//	        case (MotionEvent.ACTION_CANCEL) :
//	            Log.d(DEBUG_TAG,"Action was CANCEL");
//	            return true;
//	        case (MotionEvent.ACTION_OUTSIDE) :
//	            Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
//	                    "of current screen element");
//	            return true;      
//	        default : 
//	            return super.onTouchEvent(event);
//		}
//	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {    
//	    if (mDataLoaded == false)
//	    {
//	    	super.onConfigurationChanged(newConfig);
//	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	    }
	}
	
//	public void goToSettings(View view)
//	{
//		PopupMenu popup = new PopupMenu(this, view);
//	    MenuInflater inflater = popup.getMenuInflater();
//	    inflater.inflate(R.menu.settings, popup.getMenu());
//	    popup.show();
//	    popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
//	    {
//
//			@Override
//			public boolean onMenuItemClick(MenuItem item) {
//				switch (item.getItemId()) {
//		        case R.id.menu_settings:
//		    		Intent editDiveActivity = new Intent(DivesActivity.this, SettingsActivity.class);
////		    		//editDiveActivity.putExtra("index", mPager.getCurrentItem());
//		    	    startActivity(editDiveActivity);
//		            return true;
//		        default:
//		            return false;
//				}
//			}
//	    	
//	    });
//
//	}
	
	public void goToMenuV3(View view)
	{
		PopupMenu popup = new PopupMenu(this, view);
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.settings, popup.getMenu());
	    popup.show();
	    popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
	    {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.refresh:
					ApplicationController AC = (ApplicationController)getApplicationContext();
					AC.setDataReady(false);
					AC.getModel().stopPreloadPictures();
					AC.setModel(null);
					finish();
					return true;
				case R.id.add_dive:
					Intent newDiveActivity = new Intent(DivesActivity.this, NewDiveActivity.class);
		    	    startActivity(newDiveActivity);
		    	    return true;
				case R.id.menu_settings:
		    		Intent settingsActivity = new Intent(DivesActivity.this, SettingsActivity.class);
		    	    startActivity(settingsActivity);
		    	    return true;
		        case R.id.report_bug:
		        	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
		        	{
		        		UserVoice.launchContactUs(DivesActivity.this);
		        	}
		        	else
		        	{
		        		Intent intent = new Intent(Intent.ACTION_SEND);
		        		intent.setType("text/plain");
		        		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@diveboard.com"});
		        		startActivity(Intent.createChooser(intent, "Send Email"));
		        	}
		            return true;
		        case R.id.menu_logout:
		        	logout();
		            return true;
		        default:
		            return false;
				}
			}
	    	
	    });

	}
	
	public void goToMenuV2(View view)
	{
		// Settings floating menu
		registerForContextMenu(view);
		openContextMenu(view);
		unregisterForContextMenu(view);
	}
	
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void openMenu(View view)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			goToMenuV3(view);
		}
		else
			goToMenuV2(view);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
		    case R.id.refresh:
		    	ApplicationController AC = (ApplicationController)getApplicationContext();
		    	AC.setDataReady(false);
		    	AC.getModel().stopPreloadPictures();
				AC.setModel(null);
				finish();
				return true;
	    	case R.id.add_dive:
	    		Intent newDiveActivity = new Intent(DivesActivity.this, NewDiveActivity.class);
	    	    startActivity(newDiveActivity);
	    	    return true;
	    	case R.id.menu_settings:
	    		Intent settingsActivity = new Intent(DivesActivity.this, SettingsActivity.class);
	    	    startActivity(settingsActivity);
	            return true;
	    	case R.id.report_bug:
	    		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
	        	{
	        		UserVoice.launchContactUs(DivesActivity.this);
	        	}
	        	else
	        	{
	        		Intent intent = new Intent(Intent.ACTION_SEND);
	        		intent.setType("text/plain");
	        		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@diveboard.com"});
	        		startActivity(Intent.createChooser(intent, "Send Email"));
	        	}
	            return true;
	    	case R.id.menu_logout:
	        	logout();
	            return true;
	    	
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	public void goToEditDive(View view)
	{	
		Intent editDiveActivity = new Intent(DivesActivity.this, EditDiveActivity.class);
		editDiveActivity.putExtra("index", mPager.getCurrentItem());
	    startActivity(editDiveActivity);
	}
	
	public void goToDiveDetails(View view)
	{
		Intent diveDetailsActivity = new Intent(DivesActivity.this, DiveDetailsActivity.class);
		diveDetailsActivity.putExtra("index", mPager.getCurrentItem());
		startActivity(diveDetailsActivity);
	}
	
	public void goToGalleryCarousel(View view)
	{
		ApplicationController AC = (ApplicationController)getApplicationContext();
		try {
			if (AC.getModel().getDives().get(AC.getPageIndex()).getPictures().size() != 0 && AC.getModel().getDives().get(AC.getPageIndex()).getPictures().get(0).getPicture(getApplicationContext()) != null)
			{
				Intent galleryCarousel = new Intent(DivesActivity.this, GalleryCarouselActivity.class);
				galleryCarousel.putExtra("index", mPager.getCurrentItem());
				startActivity(galleryCarousel);
			}
			else
			{
				Intent diveDetailsActivity = new Intent(DivesActivity.this, DiveDetailsActivity.class);
				diveDetailsActivity.putExtra("index", mPager.getCurrentItem());
				startActivity(diveDetailsActivity);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadData()
	{
		if (mTaskFragment != null)
		{
			return;
		}
		// Show a progress spinner, and kick off a background task to
		// perform the data loading
		mLoadDataStatusMessageView.setText(R.string.progress_load_data);
		showProgress(true);
		FragmentManager fm = getSupportFragmentManager();
		// If the Fragment is non-null, then it is currently being
	    // retained across a configuration change.
		mTaskFragment = (TaskFragment) fm.findFragmentByTag("task");
		if (mTaskFragment == null)
		{
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, "task").commit();
		}
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoadDataStatusView.setVisibility(View.VISIBLE);
			mLoadDataStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoadDataStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoadDataFormView.setVisibility(View.VISIBLE);
			mLoadDataFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoadDataFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoadDataStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoadDataFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		ApplicationController AC = ((ApplicationController)getApplicationContext());
		if (mPager != null)
			AC.setPageIndex(mPager.getCurrentItem());
		else
			AC.setPageIndex(0);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings, menu);
	}

	/**
	 * Footer text
	 */
	public static String getPositon(int i, DiveboardModel model)
	{
		String pos = "";
		if (model.getDives().get(i).getLat() == null)
		{
			pos += "0° ";
			pos += "N";
		}
		else if (model.getDives().get(i).getLat() >= 0)
		{
			pos += String.valueOf(model.getDives().get(i).getLat()) + "° ";
			pos += "N";
		}
		else if (model.getDives().get(i).getLat() < 0)
		{
			pos += String.valueOf(model.getDives().get(i).getLat() * (-1)) + "° ";
			pos += "S";
		}
		pos += ", ";
		if (model.getDives().get(i).getLng() == null)
		{
			pos += "0° ";
			pos += "E";
		}
		else if (model.getDives().get(i).getLng() >= 0)
		{
			pos += String.valueOf(model.getDives().get(i).getLng()) + "° ";
			pos += "E";
		}
		else if (model.getDives().get(i).getLng() < 0)
		{
			pos += String.valueOf(model.getDives().get(i).getLng() * (-1)) + "° ";
			pos += "W";
		}
		if ((model.getDives().get(i).getLat() == null || model.getDives().get(i).getLat() == 0) && 
				(model.getDives().get(i).getLng() == null || model.getDives().get(i).getLng() == 0))
			pos = "";
		return (pos);
	}
	
	/**
	 * ViewPager creation with all the fragments associated, resized according the screen size
	 */
	private void createPages()
	{
		ApplicationController AC = ((ApplicationController)getApplicationContext());
		AC.setDataReady(true);
		//setRequestedOrientation(mOrientation);
		//ViewTreeObserver allows to load all the layouts of the page before applying calculation
		((LinearLayout)findViewById(R.id.load_data_form)).setVisibility(View.VISIBLE);
		mLayout = (ViewGroup)findViewById(R.id.pager);
		ViewTreeObserver vto = mLayout.getViewTreeObserver(); 
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override 
		    public void onGlobalLayout() { 
		    	ApplicationController AC = ((ApplicationController)getApplicationContext());
		        mLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		        //We do all calculation of the dimension of the elements of the page according to the UI mobile guide
		        mScreenSetup = new ScreenSetup(mLayout.getMeasuredWidth(), mLayout.getMeasuredHeight());
				int margin = (mScreenSetup.getScreenWidth() - mScreenSetup.getDiveListFragmentWidth()) * (-1);
				int offset = 0;
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
					offset = (mScreenSetup.getScreenWidth() - mScreenSetup.getDiveListFragmentWidth());
				else
					offset = ((mScreenSetup.getScreenWidth() - mScreenSetup.getDiveListFragmentWidth()) / 2) * 70 / 100;
				//We set the parameters and content of the footer
				RelativeLayout diveFooter = (RelativeLayout) findViewById(R.id.dive_footer);
				LinearLayout.LayoutParams diveFooterParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mScreenSetup.getDiveListFooterHeight());
				diveFooter.setLayoutParams(diveFooterParams);
				Typeface faceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
				Typeface faceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
				// Footer static text
				((TextView)diveFooter.findViewById(R.id.title_footer)).setText("CURRENTLY VIEWING DIVES FROM LOGBOOK");
				((TextView)diveFooter.findViewById(R.id.title_footer)).setTypeface(faceR);
				((TextView)diveFooter.findViewById(R.id.title_footer)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFooterHeight() * 20 / 100));
				((TextView)diveFooter.findViewById(R.id.content_footer)).setText("No dive found!");
				((TextView)diveFooter.findViewById(R.id.content_footer)).setTypeface(faceR);
				((TextView)diveFooter.findViewById(R.id.content_footer)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFooterHeight() * 45 / 100));
				//We create the pager with the associated pages
				if (mModel.getDives() == null || mModel.getDives().size() == 0)
				{
//					Toast toast = Toast.makeText(getApplicationContext(), "You are offline or there is no dive!", Toast.LENGTH_SHORT);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					toast.show();
					//logout();
					return ;
				}
				else
				{
					mNbPages = mModel.getDives().size();
					//Pager size setting
					mPager = (ViewPager) findViewById(R.id.pager);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListFooterHeight() - mScreenSetup.getDiveListSeekBarHeight() - mScreenSetup.getDiveListWhiteSpace4() - - mScreenSetup.getDiveListWhiteSpace3());
					mPager.setLayoutParams(params);
					//Returns the bitmap of each fragment (page) corresponding to the circle layout of the main picture of a page
					//Each circle must be white with a transparent circle in the center
					Bitmap bitmap = ImageHelper.getRoundedLayer(mScreenSetup);
					Bitmap bitmap_small = ImageHelper.getRoundedLayerSmall(mScreenSetup);
					//We load the first background of the activity
					mScreen = (RelativeLayout)findViewById(R.id.screen);
					mBackground1 = (ImageView)findViewById(R.id.background1);
					mBackground2 = (ImageView)findViewById(R.id.background2);
					DownloadImageTask task = new DownloadImageTask();
					task.execute(AC.getPageIndex());
					//Pager setting
					
			        mPagerAdapter = new DivesPagerAdapter(getSupportFragmentManager(), mModel.getDives(), mScreenSetup, bitmap, bitmap_small);
			        if (mPagerAdapter == null)
			        	System.out.println("mPagerAdapter == null");
			        else if (mPager == null)
			        	System.out.println("mPager == null");
			        else
			        	System.out.println("??? == null");
			        mPager.setAdapter(mPagerAdapter);
			        mPager.setPageMargin(margin + offset);
			        mPager.setOffscreenPageLimit(2);
			        if (AC.getPageIndex() >= mModel.getDives().size())
			        {
			        	mPager.setCurrentItem(mModel.getDives().size() - 1);
			        	AC.setPageIndex(mModel.getDives().size() - 1);
			        }
			        else
			        	mPager.setCurrentItem(AC.getPageIndex());
			        ((TextView)diveFooter.findViewById(R.id.content_footer)).setText(DivesActivity.getPositon(AC.getPageIndex(), mModel));
					((TextView)diveFooter.findViewById(R.id.content_footer)).setTypeface(faceR);
					((TextView)diveFooter.findViewById(R.id.content_footer)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFooterHeight() * 45 / 100));
			        mPager.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							final String DEBUG_TAG = "DivesActivity";
							int action = MotionEventCompat.getActionMasked(event);
//							switch(action) {
//						        case (MotionEvent.ACTION_DOWN) :
//						            Log.d(DEBUG_TAG,"Action was DOWN");
//						            return true;
//						        case (MotionEvent.ACTION_MOVE) :
//						            Log.d(DEBUG_TAG,"Action was MOVE");
//						            return true;
//						        case (MotionEvent.ACTION_UP) :
//						            Log.d(DEBUG_TAG,"Action was UP");
//						            return true;
//						        case (MotionEvent.ACTION_CANCEL) :
//						            Log.d(DEBUG_TAG,"Action was CANCEL");
//						            return true;
//						        case (MotionEvent.ACTION_OUTSIDE) :
//						            Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
//						                    "of current screen element");
//						            return true;      
//						        default : 
//						            return false;
//						    }    
							return false;
						}
					});
			        //mPager.setPageTransformer(true, new ZoomOutPageTransformer());
			        //The tracking bar is set
			        RelativeLayout.LayoutParams tracking_bar_params = new RelativeLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, mScreenSetup.getDiveListSeekBarHeight());
			        tracking_bar_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			        tracking_bar_params.setMargins(0, mScreenSetup.getDiveListWhiteSpace3(), 0, mScreenSetup.getDiveListWhiteSpace4());
			        ((RelativeLayout)findViewById(R.id.tracking_bar)).setLayoutParams(tracking_bar_params);
			        
			        
			        RelativeLayout.LayoutParams left_side_params = new RelativeLayout.LayoutParams(mScreenSetup.getScreenWidth() * 6 / 100, mScreenSetup.getDiveListSeekBarHeight());
			        left_side_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			        ((RelativeLayout)findViewById(R.id.left_side)).setLayoutParams(left_side_params);
			        
			        RelativeLayout.LayoutParams left_number_params = new RelativeLayout.LayoutParams(mScreenSetup.getScreenWidth() * 10 / 100, mScreenSetup.getDiveListSeekBarHeight() / 3 * 2);
			        left_number_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			        left_number_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			        ((RelativeLayout)findViewById(R.id.left_number)).setLayoutParams(left_number_params);
//			        ((RelativeLayout)findViewById(R.id.left_number)).setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							mPager.setCurrentItem(0);
//							
//						}
//					});
//			        ((RelativeLayout)findViewById(R.id.right_number)).setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							mPager.setCurrentItem(((int) (mModel.getDives().size() - 1)));
//							
//						}
//					});
			        RelativeLayout.LayoutParams right_number_params = new RelativeLayout.LayoutParams(mScreenSetup.getScreenWidth() * 10 / 100, mScreenSetup.getDiveListSeekBarHeight() / 3 * 2);
			        right_number_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			        right_number_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			        ((RelativeLayout)findViewById(R.id.right_number)).setLayoutParams(right_number_params);
			        ((TextView)findViewById(R.id.left_data)).setTypeface(faceR);
			        ((TextView)findViewById(R.id.left_data)).setText(Integer.toString(AC.getPageIndex() + 1));
			        ((TextView)findViewById(R.id.right_data)).setTypeface(faceR);
			        ((TextView)findViewById(R.id.right_data)).setText(Integer.toString(mModel.getDives().size()));
			        
			        RelativeLayout.LayoutParams center_bar_params = new RelativeLayout.LayoutParams(mScreenSetup.getScreenWidth() * 68 / 100, mScreenSetup.getDiveListSeekBarHeight());
			        center_bar_params.addRule(RelativeLayout.CENTER_IN_PARENT);
			        ((RelativeLayout)findViewById(R.id.center_bar)).setLayoutParams(center_bar_params);
			        max_strokes_possible = (mScreenSetup.getScreenWidth() * 68 / 100) / (mScreenSetup.getDiveListSeekBarHeight() * 33 / 100);
			        nb_dives_per_stroke = (Double.parseDouble(Integer.toString(mModel.getDives().size())) / max_strokes_possible);
			        
			        System.out.println("nb_dives_per_stroke = " + nb_dives_per_stroke + "max_strokes_possible = " + max_strokes_possible);
			        
			        if (nb_dives_per_stroke < 1)
			        {
			        	nb_dives_per_stroke = (double) 1;
			        	nb_strokes = mModel.getDives().size();
			        }
			        else
			        {
			        	nb_strokes = max_strokes_possible;
			        }
			        position_stroke = (int) Math.ceil((AC.getPageIndex() + 1) / nb_dives_per_stroke);
			        if (position_stroke > max_strokes_possible)
			        	position_stroke = max_strokes_possible;
			        //System.out.println(max_strokes_possible);
			        //System.out.println(mScreenSetup.getScreenWidth() * 68 / 100 + " " + (mScreenSetup.getDiveListSeekBarHeight() * 33 / 100) + " " + (mScreenSetup.getScreenWidth() * 68 / 100) / (mScreenSetup.getDiveListSeekBarHeight() * 33 / 100));
			        int i = 1;
			        for (; i < nb_strokes + 1; i++)
			        {
			        	RelativeLayout child = new RelativeLayout(DivesActivity.this);
				        RelativeLayout.LayoutParams child_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListSeekBarHeight() * 33 / 100, mScreenSetup.getDiveListSeekBarHeight());
//				        child.setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								//mPager.setCurrentItem(((int) (v.getId() * nb_dives_per_stroke)) - 1);
//								System.out.println(v.getX() + mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100);
//								System.out.println((v.getX() + mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100) + (mScreenSetup.getDiveListSeekBarHeight() * 33 / 100));
//								System.out.println(mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListSeekBarHeight());
//								
//								System.out.println((mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListSeekBarHeight()) + 
//										mScreenSetup.getDiveListSeekBarHeight());
//							}
//						});
				        
				        
				        
				        mGestureListener = new View.OnTouchListener() {
				            public boolean onTouch(View v, MotionEvent event) {

				                if (mDetector.onTouchEvent(event)) {
				                    return true;
				                }

				                if(event.getAction() == MotionEvent.ACTION_UP) {
				                    {
				                        //System.out.println("UP" + event.toString());
				                    	//System.out.println("UP");
				                    	if (position_stroke == 1)
				                    	{
				                    		ApplicationController AC = ((ApplicationController)getApplicationContext());
				                    		AC.setPageIndex(0);
				                    		mPager.setCurrentItem(0, true);
				                    	}
				                    	else
				                    		mPager.setCurrentItem((int) (position_stroke * nb_dives_per_stroke - 1), true);
				                    	//System.out.println("false");
				                        mIsScrolling  = false;
				                    };
				                }

				                return false;
				            }
				        };
				        ((RelativeLayout)findViewById(R.id.screen)).setOnTouchListener(mGestureListener);
				        ((RelativeLayout)findViewById(R.id.dive_footer)).setOnTouchListener(mGestureListener);
				        child.setId(i);
				        if (i > 1)
				        	child_params.addRule(RelativeLayout.RIGHT_OF, i - 1);
				        child.setLayoutParams(child_params);
				        //child.setBackgroundColor(Color.RED);
				        
				        RelativeLayout child_2 = new RelativeLayout(DivesActivity.this);
				        RelativeLayout.LayoutParams child_2_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListSeekBarHeight() * 14 / 100, mScreenSetup.getDiveListSeekBarHeight() / 3 * 2);
				        child_2_params.addRule(RelativeLayout.CENTER_IN_PARENT);
				        child_2.setBackgroundColor(Color.WHITE);
				        child_2.setLayoutParams(child_2_params);
				        if (i == position_stroke)
				        {
					        child_2_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					        child_2.getBackground().setAlpha(230);
				        }
				        else
				        {
					        child_2_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					        child_2.getBackground().setAlpha(125);
				        }
				        child.addView(child_2);
//				        System.out.println("add bar");
				        ((RelativeLayout)findViewById(R.id.center_bar)).addView(child);
//				        System.out.println(i- 1);
//				        System.out.println(((RelativeLayout)findViewById(R.id.center_bar)).getChildAt(i - 1).getX() + mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100);
//						System.out.println(mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListSeekBarHeight());
//						System.out.println((((RelativeLayout)findViewById(R.id.center_bar)).getChildAt(i - 1).getX() + mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100) + (mScreenSetup.getDiveListSeekBarHeight() * 33 / 100));
//						System.out.println((mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListSeekBarHeight()) + 
//								mScreenSetup.getDiveListSeekBarHeight());
			        }
			        i++;
			        RelativeLayout.LayoutParams right_side_params = new RelativeLayout.LayoutParams(mScreenSetup.getScreenWidth() * 6 / 100, mScreenSetup.getDiveListSeekBarHeight());
			        right_side_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			        ((RelativeLayout)findViewById(R.id.right_side)).setLayoutParams(right_side_params);
			        
//			        for (int index = 0; index < ((RelativeLayout)findViewById(R.id.center_bar)).getChildCount() - 1; index++)
//			        {
//			        	TrackingBarPosition childPos = new TrackingBarPosition( 
//			        			(int)((RelativeLayout)findViewById(R.id.center_bar)).getChildAt(index).getX()
//			        			+ mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100,
//				        		mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListSeekBarHeight(),
//				        		(
//				        				(int)((RelativeLayout)findViewById(R.id.center_bar)).getChildAt(index).getX()
//				        		+ mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100) + (mScreenSetup.getDiveListSeekBarHeight() * 33 / 100),
//				        		(mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListSeekBarHeight()) + 
//								mScreenSetup.getDiveListSeekBarHeight());
//				        mTrackingBarPosition.add(childPos);
//			        }
			        new Thread(new Runnable()
					{
						public void run()
						{
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
//							System.out.println(v.getX() + mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100);
//							System.out.println((v.getX() + mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100) + (mScreenSetup.getDiveListSeekBarHeight() * 33 / 100));
//							System.out.println(mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListSeekBarHeight());
//							System.out.println((mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListSeekBarHeight()) + 
//									mScreenSetup.getDiveListSeekBarHeight());
					        for (int index = 0; index < ((RelativeLayout)findViewById(R.id.center_bar)).getChildCount(); index++)
					        {
					        	TrackingBarPosition childPos = new TrackingBarPosition( 
					        			(int)((RelativeLayout)findViewById(R.id.center_bar)).getChildAt(index).getLeft() + mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100,
					        			((int)((RelativeLayout)findViewById(R.id.center_bar)).getChildAt(index).getLeft() + mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100) + (mScreenSetup.getDiveListSeekBarHeight() * 33 / 100),
						        		mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListFooterHeight() - mScreenSetup.getDiveListSeekBarHeight(),
						        		(mScreenSetup.getScreenHeight()));
						        mTrackingBarPosition.add(childPos);
//						        System.out.println(index);
						        //mTrackingBarPosition.get(index).getx()
						        System.out.println(mTrackingBarPosition.get(index).getx());
						        System.out.println(mTrackingBarPosition.get(index).getX());
						        System.out.println(mTrackingBarPosition.get(index).gety());
						        System.out.println(mTrackingBarPosition.get(index).getY());
//						        System.out.println(((RelativeLayout)findViewById(R.id.center_bar)).getChildAt(index).getX() + mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100);
//								System.out.println(mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListSeekBarHeight());
//								System.out.println((((RelativeLayout)findViewById(R.id.center_bar)).getChildAt(index).getX() + mScreenSetup.getScreenWidth() * 10 / 100 + mScreenSetup.getScreenWidth() * 6 / 100) + (mScreenSetup.getDiveListSeekBarHeight() * 33 / 100));
//								System.out.println((mScreenSetup.getScreenHeight() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListWhiteSpace4() - mScreenSetup.getDiveListSeekBarHeight()) + 
//										mScreenSetup.getDiveListSeekBarHeight());
					        }
						}
					}).start();
			        
			        
/*			        mSeekBar = (SeekBar)findViewById(R.id.seekBar);
			        RelativeLayout.LayoutParams seekBarParams = new RelativeLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, mScreenSetup.getDiveListSeekBarHeight());
			        seekBarParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			        seekBarParams.setMargins(0, mScreenSetup.getDiveListWhiteSpace3(), 0, mScreenSetup.getDiveListWhiteSpace4());
			        mSeekBar.setLayoutParams(seekBarParams);
			        mSeekBar.setMax(mModel.getDives().size() - 1);
			        mSeekBar.setProgress(AC.getPageIndex());*/
			        //Events when the user changes a page
			        mPager.setOnPageChangeListener(new OnPageChangeListener()
			        {
			        	
			        	
						@Override
						public void onPageScrollStateChanged(int arg0) {
							if (arg0 == 0)
							{
								ApplicationController AC = ((ApplicationController)getApplicationContext());
								lowerStroke(position_stroke);
								AC.setPageIndex(mPager.getCurrentItem());
								position_stroke = (int) Math.ceil((AC.getPageIndex() + 1) / nb_dives_per_stroke);
								if (position_stroke > max_strokes_possible)
									position_stroke = max_strokes_possible;
								//nb_dives_per_stroke = new Integer((mModel.getDives().size()) / max_strokes_possible).doubleValue();
								upperStroke(position_stroke);
								((TextView)findViewById(R.id.left_data)).setText(Integer.toString(AC.getPageIndex() + 1));
/*								mSeekBar.setProgress(mPager.getCurrentItem());
								mSeekBar.setEnabled(true);*/
								RelativeLayout diveFooter = (RelativeLayout) findViewById(R.id.dive_footer);
								Typeface faceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
								((TextView)diveFooter.findViewById(R.id.content_footer)).setText(DivesActivity.getPositon(mPager.getCurrentItem(), mModel));
								((TextView)diveFooter.findViewById(R.id.content_footer)).setTypeface(faceR);
								((TextView)diveFooter.findViewById(R.id.content_footer)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFooterHeight() * 45 / 100));
								DownloadImageTask task = new DownloadImageTask();
								task.execute(mPager.getCurrentItem());
								
							}
						}

						@Override
						public void onPageScrolled(int arg0, float arg1, int arg2) {	
						}

						@Override
						public void onPageSelected(int arg0) {
						}
			        	
			        });
			        //Events when the user changes the seek bar
/*			        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
			        {
			        	@Override
			        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			        	{
			        	}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							mPager.setCurrentItem(seekBar.getProgress(), true);
							mSeekBar.setEnabled(false);
						}
			        });*/
			        
			        if (AC.isDataRefreshed() != true)
			        {
			        	AC.setDataRefreshed(true);
				        mModel.setOnDataRefreshComplete(new DataRefreshListener()
				        {
		
							@Override
							public void onDataRefreshComplete() {
								//System.out.println("Data refresh complete");
								try {
									mModel.overwriteData();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
				        	
				        });
				        mModel.refreshData();
//				        new Thread(new Runnable()
//						{
//							public void run()
//							{
//								mModel.refreshData();
//							}
//						}).start();
			        }
				}
		    } 
		});
	}

	public final void upperStroke(final int index)
	{
		//System.out.println(index);
    	RelativeLayout.LayoutParams child_2_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListSeekBarHeight() * 14 / 100, mScreenSetup.getDiveListSeekBarHeight() / 3 * 2);
    	((ViewGroup)((RelativeLayout)findViewById(R.id.center_bar)).findViewById(index)).getChildAt(0).getBackground().setAlpha(230);
    	child_2_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    	//((ViewGroup)((RelativeLayout)findViewById(R.id.center_bar)).findViewById(index)).setBackgroundColor(Color.RED);
    	child_2_params.addRule(RelativeLayout.CENTER_IN_PARENT);
    	((ViewGroup)((RelativeLayout)findViewById(R.id.center_bar)).findViewById(index)).getChildAt(0).setLayoutParams(child_2_params);
	}
	
	public final void lowerStroke(final int index)
	{
		//System.out.println(index);
    	RelativeLayout.LayoutParams child_2_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListSeekBarHeight() * 14 / 100, mScreenSetup.getDiveListSeekBarHeight() / 3 * 2);
    	((ViewGroup)((RelativeLayout)findViewById(R.id.center_bar)).findViewById(index)).getChildAt(0).getBackground().setAlpha(125);
    	child_2_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    	child_2_params.addRule(RelativeLayout.CENTER_IN_PARENT);
    	((ViewGroup)((RelativeLayout)findViewById(R.id.center_bar)).findViewById(index)).getChildAt(0).setLayoutParams(child_2_params);
	}
	
	private class DownloadImageTask extends AsyncTask<Integer, Void, Bitmap>
	{
		private final WeakReference<RelativeLayout> screenReference;
		private Integer mItemNb;
		
		public DownloadImageTask()
		{
			screenReference = new WeakReference<RelativeLayout>(mScreen);
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
			} catch (Exception e) {
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
			if (screenReference != null && result != null)
			{
				final RelativeLayout screenView = screenReference.get();
				if (screenView != null)
				if (true)
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
					{
						
						if (mBackground == 1)
						{
							mBackground2.setBackgroundDrawable(new BitmapDrawable(getResources(), result));
							AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
							anim.setAnimationListener(new AnimationListener()
							{
								@Override
								public void onAnimationEnd(Animation arg0)
								{
									mBackground2.setAlpha(1.0f);
								}
	
								@Override
								public void onAnimationRepeat(Animation animation) {
									// TODO Auto-generated method stub
									
								}
	
								@Override
								public void onAnimationStart(Animation animation) {
									// TODO Auto-generated method stub
									ImageView back2 = (ImageView) findViewById(R.id.background2);
									back2.setAlpha(1.0f);
								}
							});
							anim.setDuration(500);
							mBackground2.startAnimation(anim);
							mBackground = 2;
						}
						else
						{
							mBackground1.setBackgroundDrawable(new BitmapDrawable(getResources(), result));
							AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
							anim.setDuration(500);
							mBackground2.startAnimation(anim);
							anim.setAnimationListener(new AnimationListener()
							{
								@Override
								public void onAnimationEnd(Animation arg0)
								{
									mBackground2.setAlpha(0.0f);
								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {
									// TODO Auto-generated method stub
									
								}

								@Override
								public void onAnimationStart(Animation animation) {
									// TODO Auto-generated method stub
									
								}
							});
							mBackground = 1;
						}
					}
					else
					{
						mBackground1.setBackgroundDrawable(new BitmapDrawable(getResources(), result));
					}
				}
			}
		}
	}
	
	/**
	 * Represents an asynchronous task used to load data
	 */
//	private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
//		
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			mModel.loadData();
//			return true;
//		}
//
//		@Override
//		protected void onPostExecute(final Boolean success) {
//			mAuthTask = null;
//			showProgress(false);
//
//			if (success) {
//				createPages();
//			}
//		}
//		
//		@Override
//		protected void onCancelled() {
//			mAuthTask = null;
//			showProgress(false);
//		}
//	}
	
	/**
	 * Generate the dives pagesDive dive
	 */
	private class DivesPagerAdapter extends FragmentStatePagerAdapter
	{
		private ArrayList<Dive> mDives;
		private ScreenSetup mScreenSetup;
		private Bitmap mRoundedLayer;
		private Bitmap mRoundedLayerSmall;
		
		public DivesPagerAdapter(FragmentManager fragmentManager, ArrayList<Dive> dives, ScreenSetup screenSetup, Bitmap rounded_layer, Bitmap rounded_layer_small) {
            super(fragmentManager);
			mDives = dives;
			mScreenSetup = screenSetup;
			mRoundedLayer = rounded_layer;
			mRoundedLayerSmall = rounded_layer_small;
        }

        @Override
        public Fragment getItem(int position) {
            return new DivesFragment(mDives.get(position), mScreenSetup, mRoundedLayer, mRoundedLayerSmall);
        }

        @Override
        public int getCount() {
            return mNbPages;
        }
	}

    @Override 
    public boolean onTouchEvent(MotionEvent event){ 
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

	public int stroke_selected(int x, int y)
	{
		//System.out.println("x = " + x + ", y = " + y);
		for (int i = 0; i < mTrackingBarPosition.size(); i++)
		{
			//System.out.println("YOOOOOOOO x = " + x + ", y = " + y + "    " + mTrackingBarPosition.get(i).getx() + " " + mTrackingBarPosition.get(i).getX() + " " + mTrackingBarPosition.get(i).gety() + " " + mTrackingBarPosition.get(i).getY());
			if (x >= mTrackingBarPosition.get(i).getx() && x <= mTrackingBarPosition.get(i).getX() &&
					y >= mTrackingBarPosition.get(i).gety() && y <= mTrackingBarPosition.get(i).getY())
				return i + 1;
		}
		mIsScrolling = false;
		return 0;
	}
    
    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
		public boolean onSingleTapUp(MotionEvent e) {
			//System.out.println("UP");
        	//System.out.println("false");
			return super.onSingleTapUp(e);
//			mPager.setCurrentItem((int) (position_stroke * nb_dives_per_stroke), true);
        	
//            mIsScrolling  = false;
		}

		private static final String DEBUG_TAG = "Gestures"; 
        
        @Override
        public boolean onDown(MotionEvent event) { 
        	//Log.d(DEBUG_TAG, "onDown: " + event.toString());
        	//System.out.println("on down");
        	int stroke_selected = stroke_selected((int)event.getX(), (int)event.getY());
        	//System.out.println("Stroke selected = " + stroke_selected);
        	if (stroke_selected == 0)
        	{
        		//System.out.println("false");
        		mIsScrolling = false;
        	}
        	else
        	{
        		//System.out.println("true");
        		mIsScrolling = true;
        		lowerStroke(position_stroke);
        		position_stroke = stroke_selected;
        		//System.out.println("position stroke = " + position_stroke);
        		upperStroke(position_stroke);
        		//((RelativeLayout)findViewById(R.id.center_bar)).getChildAt(stroke_selected)
        	}
        	return true;
        }

		@Override
		public boolean onScroll(MotionEvent event1, MotionEvent event2,
				float distanceX, float distanceY) {
			//if (mIsScrolling == true)
			{
				int stroke_selected = stroke_selected((int)event2.getX(), (int)event2.getY());
				//System.out.println(stroke_selected);
				if (stroke_selected != 0)
				{
					lowerStroke(position_stroke);
	        		position_stroke = stroke_selected;
	        		//System.out.println("position stroke = " + position_stroke);
	        		upperStroke(position_stroke);
	        		((TextView)findViewById(R.id.left_data)).setText(Integer.toString((int) (position_stroke * nb_dives_per_stroke)));
				}
				
			}
			//Log.d(DEBUG_TAG, "onScroll: " + event1.toString()+event2.toString());
			return true;
		}
    }
}
