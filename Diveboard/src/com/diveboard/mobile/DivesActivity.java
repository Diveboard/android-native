package com.diveboard.mobile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

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

public class DivesActivity extends FragmentActivity {
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

		setContentView(R.layout.activity_dives);
		
		mModel = new DiveboardModel(48, this);		
		mLoadDataFormView = findViewById(R.id.load_data_form);
		mLoadDataStatusView = findViewById(R.id.load_data_status);
		mLoadDataStatusMessageView = (TextView) findViewById(R.id.load_data_status_message);
		attemptLogin();
	}

	public void loadData(View view) {
		 attemptLogin();
	}
	
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
		        mPagerAdapter = new DivesPagerAdapter(getSupportFragmentManager(), mModel.getDives());
		        mPager.setAdapter(mPagerAdapter);
		        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
			}
		}
		
		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	private class DivesPagerAdapter extends FragmentStatePagerAdapter
	{
		private ArrayList<Dive> mDives;
		
		public DivesPagerAdapter(FragmentManager fragmentManager, ArrayList<Dive> dives) {
            super(fragmentManager);
			mDives = dives;
        }

        @Override
        public Fragment getItem(int position) {
            return new DivesFragment(mDives.get(position));
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
	}
}
