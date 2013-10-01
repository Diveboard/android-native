package com.diveboard.mobile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DivesActivity extends FragmentActivity {

	// Number of pages in Dives
	private int mNbPages = 1;
	
	// All you need to make the carousel
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private ViewGroup mLayout;
	private SeekBar mSeekBar;
	private View mRootView;
	private int mFragmentHeight;
	private int mFragmentWidth;
	
	// Thread for the data loading & the views associated
	private LoadDataTask mAuthTask = null;
	private View mLoadDataFormView;
	private View mLoadDataStatusView;
	private TextView mLoadDataStatusMessageView;
	
	// Model to display
	private DiveboardModel mModel;
	private TextView mNickname;
	private TextView mId;
	private TextView mLocation;
	private TextView mPublicNbDives;
	private TextView mTotalNbDives;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the view layout
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			getActionBar().hide();
		}
		else
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		setContentView(R.layout.activity_dives);
		// Initialize data
		if (savedInstanceState == null)
		{
			mModel = new DiveboardModel(48, this);		
			mLoadDataFormView = findViewById(R.id.load_data_form);
			mLoadDataStatusView = findViewById(R.id.load_data_status);
			mLoadDataStatusMessageView = (TextView) findViewById(R.id.load_data_status_message);
			loadData();
		}
		else
		{
			mModel = savedInstanceState.getParcelable("model");
			createPages();
		}
	}
	
	public void loadData()
	{
		if (mAuthTask != null)
		{
			return;
		}
		// Show a progress spinner, and kick off a background task to
		// perform the user login attempt.
		mLoadDataStatusMessageView.setText(R.string.progress_load_data);
		showProgress(true);
		mAuthTask = new LoadDataTask();
		mAuthTask.execute((Void) null);
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
		//super.onSaveInstanceState(outState);mModel
		outState.putParcelable("model", mModel);
	}
	
	/**
	 * ViewPager creation with all the fragments associated, resized according the screen size
	 */
	private void createPages()
	{
		//ViewTreeObserver allows to load all the layouts of the page before applying calculation
		((LinearLayout)findViewById(R.id.load_data_form)).setVisibility(View.VISIBLE);
		mLayout = (ViewGroup)findViewById(R.id.pager);
		ViewTreeObserver vto = mLayout.getViewTreeObserver(); 
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
		    @Override 
		    public void onGlobalLayout() { 
		        mLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		        //We do all calculation of the dimension of the elements of the page according to the UI mobile guide
		        int width  = mLayout.getMeasuredWidth();
		        int screenheight = mLayout.getMeasuredHeight(); 
				mFragmentHeight = screenheight * 74 / 100;
				mFragmentWidth = (mFragmentHeight * 10) / 13;
				int screenwidth = getWindowManager().getDefaultDisplay().getWidth();
				int margin = (screenwidth - mFragmentHeight) * (-1);
				int offset = (mFragmentHeight / 2);
				//We set dynamically the size of each page
				mRootView = (View)findViewById(R.id.screen);
				mRootView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, screenheight));
				//Returns the bitmap of each fragment (page) corresponding to the circle layout of the main picture of a page
				//Each circle must be white with a transparent circle in the center
				int contentheight = screenheight * 74 / 100;
				int size = contentheight * 60 / 100;
				Bitmap bitmap = ImageHelper.getRoundedLayer(size, contentheight, 0);
				//We load the first background of the activity
				new DownloadImageTask().execute(0);
				//We create the pager with the associated pages
				mNbPages = mModel.getDives().size();
				mPager = (ViewPager) findViewById(R.id.pager);
		        mPagerAdapter = new DivesPagerAdapter(getSupportFragmentManager(), mModel.getDives(), screenheight, bitmap);
		        mPager.setAdapter(mPagerAdapter);
		        mPager.setPageMargin(margin + offset);
		        mPager.setOffscreenPageLimit(((screenwidth / mFragmentWidth) + 1));
		        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
		        //The tracking bar is set
		        mSeekBar = (SeekBar)findViewById(R.id.seekBar);
		        mSeekBar.setMax(mModel.getDives().size() - 1);
		        mPager.setOnPageChangeListener(new OnPageChangeListener()
		        {

					@Override
					public void onPageScrollStateChanged(int arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						mSeekBar.setProgress(arg0);		
					}

					@Override
					public void onPageSelected(int arg0) {
						new DownloadImageTask().execute(arg0);
					}
		        	
		        });     
		        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		        {
		        	@Override
		        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		        	{
		        		if (fromUser == true)
		        		{
		        			mPager.setCurrentItem(progress, true);
		        		}
		        	}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
		        });
		    } 
		});
	}
	
	private class DownloadImageTask extends AsyncTask<Integer, Void, Bitmap>
	{
		protected Bitmap doInBackground(Integer... args)
		{
			try {
				return mModel.getDives().get(args[0]).getThumbnailImageUrl().getPicture(getApplicationContext());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Bitmap result)
		{
			//Bitmap dest = Bitmap.createScaledBitmap(result, 10, 10, true);
			if (result != null)
				mRootView.setBackgroundDrawable(new BitmapDrawable(getResources(), fastblur(result, 30)));
		}
		
		public Bitmap fastblur(Bitmap sentBitmap, int radius) {

	        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

	        if (radius < 1) {
	            return (null);
	        }

	        int w = bitmap.getWidth();
	        int h = bitmap.getHeight();

	        int[] pix = new int[w * h];
	        Log.e("pix", w + " " + h + " " + pix.length);
	        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

	        int wm = w - 1;
	        int hm = h - 1;
	        int wh = w * h;
	        int div = radius + radius + 1;

	        int r[] = new int[wh];
	        int g[] = new int[wh];
	        int b[] = new int[wh];
	        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
	        int vmin[] = new int[Math.max(w, h)];

	        int divsum = (div + 1) >> 1;
	        divsum *= divsum;
	        int dv[] = new int[256 * divsum];
	        for (i = 0; i < 256 * divsum; i++) {
	            dv[i] = (i / divsum);
	        }

	        yw = yi = 0;

	        int[][] stack = new int[div][3];
	        int stackpointer;
	        int stackstart;
	        int[] sir;
	        int rbs;
	        int r1 = radius + 1;
	        int routsum, goutsum, boutsum;
	        int rinsum, ginsum, binsum;

	        for (y = 0; y < h; y++) {
	            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
	            for (i = -radius; i <= radius; i++) {
	                p = pix[yi + Math.min(wm, Math.max(i, 0))];
	                sir = stack[i + radius];
	                sir[0] = (p & 0xff0000) >> 16;
	                sir[1] = (p & 0x00ff00) >> 8;
	                sir[2] = (p & 0x0000ff);
	                rbs = r1 - Math.abs(i);
	                rsum += sir[0] * rbs;
	                gsum += sir[1] * rbs;
	                bsum += sir[2] * rbs;
	                if (i > 0) {
	                    rinsum += sir[0];
	                    ginsum += sir[1];
	                    binsum += sir[2];
	                } else {
	                    routsum += sir[0];
	                    goutsum += sir[1];
	                    boutsum += sir[2];
	                }
	            }
	            stackpointer = radius;

	            for (x = 0; x < w; x++) {

	                r[yi] = dv[rsum];
	                g[yi] = dv[gsum];
	                b[yi] = dv[bsum];

	                rsum -= routsum;
	                gsum -= goutsum;
	                bsum -= boutsum;

	                stackstart = stackpointer - radius + div;
	                sir = stack[stackstart % div];

	                routsum -= sir[0];
	                goutsum -= sir[1];
	                boutsum -= sir[2];

	                if (y == 0) {
	                    vmin[x] = Math.min(x + radius + 1, wm);
	                }
	                p = pix[yw + vmin[x]];

	                sir[0] = (p & 0xff0000) >> 16;
	                sir[1] = (p & 0x00ff00) >> 8;
	                sir[2] = (p & 0x0000ff);

	                rinsum += sir[0];
	                ginsum += sir[1];
	                binsum += sir[2];

	                rsum += rinsum;
	                gsum += ginsum;
	                bsum += binsum;

	                stackpointer = (stackpointer + 1) % div;
	                sir = stack[(stackpointer) % div];

	                routsum += sir[0];
	                goutsum += sir[1];
	                boutsum += sir[2];

	                rinsum -= sir[0];
	                ginsum -= sir[1];
	                binsum -= sir[2];

	                yi++;
	            }
	            yw += w;
	        }
	        for (x = 0; x < w; x++) {
	            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
	            yp = -radius * w;
	            for (i = -radius; i <= radius; i++) {
	                yi = Math.max(0, yp) + x;

	                sir = stack[i + radius];

	                sir[0] = r[yi];
	                sir[1] = g[yi];
	                sir[2] = b[yi];

	                rbs = r1 - Math.abs(i);

	                rsum += r[yi] * rbs;
	                gsum += g[yi] * rbs;
	                bsum += b[yi] * rbs;

	                if (i > 0) {
	                    rinsum += sir[0];
	                    ginsum += sir[1];
	                    binsum += sir[2];
	                } else {
	                    routsum += sir[0];
	                    goutsum += sir[1];
	                    boutsum += sir[2];
	                }

	                if (i < hm) {
	                    yp += w;
	                }
	            }
	            yi = x;
	            stackpointer = radius;
	            for (y = 0; y < h; y++) {
	                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
	                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

	                rsum -= routsum;
	                gsum -= goutsum;
	                bsum -= boutsum;

	                stackstart = stackpointer - radius + div;
	                sir = stack[stackstart % div];

	                routsum -= sir[0];
	                goutsum -= sir[1];
	                boutsum -= sir[2];

	                if (x == 0) {
	                    vmin[y] = Math.min(y + r1, hm) * w;
	                }
	                p = x + vmin[y];

	                sir[0] = r[p];
	                sir[1] = g[p];
	                sir[2] = b[p];

	                rinsum += sir[0];
	                ginsum += sir[1];
	                binsum += sir[2];

	                rsum += rinsum;
	                gsum += ginsum;
	                bsum += binsum;

	                stackpointer = (stackpointer + 1) % div;
	                sir = stack[stackpointer];

	                routsum += sir[0];
	                goutsum += sir[1];
	                boutsum += sir[2];

	                rinsum -= sir[0];
	                ginsum -= sir[1];
	                binsum -= sir[2];

	                yi += w;
	            }
	        }

	        Log.e("pix", w + " " + h + " " + pix.length);
	        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

	        return (bitmap);
	    }
	}
	
	/**
	 * Represents an asynchronous task used to load data
	 */
	private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			mModel.loadData();
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				createPages();
			}
		}
		
		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	/**
	 * Generate the dives pages
	 */
	private class DivesPagerAdapter extends FragmentStatePagerAdapter
	{
		private ArrayList<Dive> mDives;
		private int mScreenheight;
		private Bitmap mRoundedLayer;
		
		public DivesPagerAdapter(FragmentManager fragmentManager, ArrayList<Dive> dives, int screenheight, Bitmap rounded_layer) {
            super(fragmentManager);
			mDives = dives;
			mScreenheight = screenheight;
			mRoundedLayer = rounded_layer;
        }

        @Override
        public Fragment getItem(int position) {
            return new DivesFragment(mDives.get(position), mScreenheight, mRoundedLayer);
        }

        @Override
        public int getCount() {
            return mNbPages;
        }
	}
}
