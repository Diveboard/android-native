package com.ksso.diveboard;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.ksso.model.Dive;
import com.ksso.model.DiveboardModel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class UserActivity extends FragmentActivity {
	private int NUM_PAGES = 1;
	
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	
	
	private LoadDataTask mAuthTask = null;

	private DiveboardModel mModel;
	private TextView mNickname;
	private TextView mId;
	private TextView mLocation;
	private TextView mPublicNbDives;
	private TextView mTotalNbDives;
	

	private View mLoadDataFormView;
	private View mLoadDataStatusView;
	private TextView mLoadDataStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user);
		
		mModel = new DiveboardModel(48, this);
/*		mNickname = (TextView)findViewById(R.id.nickname);
		mId = (TextView)findViewById(R.id.user_id);
		mLocation = (TextView)findViewById(R.id.location);
		mPublicNbDives = (TextView)findViewById(R.id.public_nb_dives);
		mTotalNbDives = (TextView)findViewById(R.id.total_nb_dives);*/
		
		mLoadDataFormView = findViewById(R.id.load_data_form);
		mLoadDataStatusView = findViewById(R.id.load_data_status);
		mLoadDataStatusMessageView = (TextView) findViewById(R.id.load_data_status_message);
		attemptLogin();
//		findViewById(R.id.load_data_button).setOnClickListener(
//				new View.OnClickListener() {
//					@Override
//					public void onClick(View view) {
//						attemptLogin();
//					}
//				});
	}

	public void loadData(View view) {
		 attemptLogin();
	}
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
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

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
		
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
				/*ArrayList<Dive> dives = mModel.getDives();
				ArrayList<String> myStringArray = new ArrayList<String>();
				for (int i = 0; i < mModel.getDives().size(); i++)
				{
					myStringArray.add(mModel.getDives().get(i).getDate());
				}
				//String[] myStringArray = {"Cheese", "Pepperoni", "Black Olives"};
				ListView listView = (ListView) findViewById(R.id.list_dives);
				DiveAdapter adapter = new DiveAdapter(UserActivity.this, dives);
				listView.setAdapter(adapter);*/
				
				NUM_PAGES = mModel.getDives().size();
				mPager = (ViewPager) findViewById(R.id.pager);
		        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), mModel.getDives());
		        mPager.setAdapter(mPagerAdapter);
		        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
				
				/*mNickname.setText(mModel.getUser().getNickname());
				mId.setText(Integer.toString(mModel.getUser().getId()));
				mLocation.setText(mModel.getUser().getLocation());
				mPublicNbDives.setText(Integer.toString(mModel.getUser().getPublicNbDives()));
				mTotalNbDives.setText(Integer.toString(mModel.getUser().getTotalNbDives()));*/
			}
		}
		
		public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		    private static final float MIN_SCALE = 0.85f;
		    private static final float MIN_ALPHA = 0.5f;

		    public void transformPage(View view, float position) {
		        int pageWidth = view.getWidth();
		        int pageHeight = view.getHeight();

		        if (position < -1) { // [-Infinity,-1)
		            // This page is way off-screen to the left.
		            view.setAlpha(0);

		        } else if (position <= 1) { // [-1,1]
		            // Modify the default slide transition to shrink the page as well
		            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
		            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
		            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
		            if (position < 0) {
		                view.setTranslationX(horzMargin - vertMargin / 2);
		            } else {
		                view.setTranslationX(-horzMargin + vertMargin / 2);
		            }

		            // Scale the page down (between MIN_SCALE and 1)
		            view.setScaleX(scaleFactor);
		            view.setScaleY(scaleFactor);

		            // Fade the page relative to its size.
		            view.setAlpha(MIN_ALPHA +
		                    (scaleFactor - MIN_SCALE) /
		                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

		        } else { // (1,+Infinity]
		            // This page is way off-screen to the right.
		            view.setAlpha(0);
		        }
		    }
		}
		
		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
	{
		private ArrayList<Dive> mDives;
		
		public ScreenSlidePagerAdapter(FragmentManager fragmentManager, ArrayList<Dive> dives) {
            super(fragmentManager);
			mDives = dives;
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlidePageFragment(mDives.get(position));
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
	}
}
