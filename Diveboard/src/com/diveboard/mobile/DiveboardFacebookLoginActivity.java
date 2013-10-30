package com.diveboard.mobile;

import com.diveboard.mobile.DiveboardLoginActivity.UserLoginTask;
import com.facebook.*;
import com.facebook.model.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class DiveboardFacebookLoginActivity extends Activity {
	private UserLoginTask mAuthTask = null;
	private String mToken;
	private String mId;
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diveboard_facebook_login);

		// start Facebook Login
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					mToken = session.getAccessToken();
					// make request to the /me API
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

						// callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user != null) {
								mId = user.getId();
								mAuthTask = new UserLoginTask();
								mAuthTask.execute((Void) null);
							}
						}
					});
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.diveboard_facebook_login, menu);
		return true;
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			ApplicationController AC = (ApplicationController)getApplicationContext();
			return AC.getModel().doFbLogin(mId, mToken);
		}

		@Override
		protected void onPostExecute(final Integer success) {
			mAuthTask = null;

			if (success != -1) {
				Intent editDiveActivity = new Intent(DiveboardFacebookLoginActivity.this, DivesActivity.class);
			    startActivity(editDiveActivity);
			}
//			else if (success == 0 ){
//				mPasswordView
//						.setError(getString(R.string.error_incorrect_password));
//				mPasswordView.requestFocus();
//			}
			else
			{
				Toast toast = Toast.makeText(getApplicationContext(), "Could not connect with the database", Toast.LENGTH_SHORT);
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
