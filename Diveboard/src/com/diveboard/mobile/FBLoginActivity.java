package com.diveboard.mobile;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.diveboard.mobile.FBLoginFragment.UserLoginTask;
import com.diveboard.model.DiveboardModel;
import com.facebook.*;
import com.facebook.model.*;
import com.google.analytics.tracking.android.EasyTracker;

public class FBLoginActivity extends Activity {
	private String mId;
	private Session mSession;
	private String mToken;
	private UserLoginTask mAuthTask = null;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
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
	
	private static Session openActiveSession(Activity activity, boolean allowLoginUI, Session.StatusCallback callback, List<String> permissions) {
	    Session.OpenRequest openRequest = new Session.OpenRequest(activity).setPermissions(permissions).setCallback(callback);
	    Session session = new Session.Builder(activity).build();
	    if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
	        Session.setActiveSession(session);
	        session.openForRead(openRequest);
	        return session;
	    }
	    return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fblogin);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		// start Facebook Login
		mSession = new Session(this);
		List<String> permissions = new ArrayList<String>();
		permissions.add("email");
	    openActiveSession(this, true, new Session.StatusCallback() {

	      // callback when session changes state
	      @Override
	      public void call(Session session, SessionState state, Exception exception) {
	    	mToken = session.getAccessToken();
	    	System.out.println("Token = " + mToken);
	    	if (session.isClosed())
	    	{
	    		finish();
//	    		TextView welcome = (TextView) findViewById(R.id.welcome);
//	    		welcome.setText("close!");
	    	}
	        if (session.isOpened()) {

	          // make request to the /me API
	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

	            // callback after Graph API response with user object
	            @Override
	            public void onCompleted(GraphUser user, Response response) {
	              if (user != null) {
//	                TextView welcome = (TextView) findViewById(R.id.welcome);
//	                welcome.setText("Hello " + user.getName() + "!");
	            	System.out.println(user.getProperty("email").toString());
	                mId = user.getId();
	                mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
	                showProgress(true);
					mAuthTask = new UserLoginTask();
					mAuthTask.execute((Void) null);
	              }
	            }
	          });
	          
	        }
	      }
	    }, permissions);
	  }

	public void logout(View view)
	{
//		System.out.println("close");
//		if (Session.getActiveSession() != null)
//			Session.getActiveSession().closeAndClearTokenInformation();
//		Session.setActiveSession(null);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

//			mLoginFormView.setVisibility(View.VISIBLE);
//			mLoginFormView.animate().setDuration(shortAnimTime)
//					.alpha(show ? 0 : 1)
//					.setListener(new AnimatorListenerAdapter() {
//						@Override
//						public void onAnimationEnd(Animator animation) {
//							mLoginFormView.setVisibility(show ? View.GONE
//									: View.VISIBLE);
//						}
//					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			//mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			ApplicationController AC = (ApplicationController)getApplicationContext();
			//System.out.println(mId + " " + mToken);
			if (AC.getModel() == null)
				System.out.println("Model null");
			return AC.getModel().doFbLogin(mId, mToken);
		}

		@Override
		protected void onPostExecute(final Integer success) {
			mAuthTask = null;

			if (success != -1) {
				Intent editDiveActivity = new Intent(FBLoginActivity.this, DivesActivity.class);
			    startActivity(editDiveActivity);
			}
//			else if (success == 0 ){
//				mPasswordView
//						.setError(getString(R.string.error_incorrect_password));
//				mPasswordView.requestFocus();
//			}
			else
			{
				Toast toast = Toast.makeText(FBLoginActivity.this, "Could not connect with the database", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
	
}
