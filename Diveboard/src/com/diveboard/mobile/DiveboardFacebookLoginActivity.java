package com.diveboard.mobile;

import com.diveboard.mobile.DiveboardLoginActivity.UserLoginTask;
import com.facebook.*;
import com.facebook.model.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class DiveboardFacebookLoginActivity extends FragmentActivity {
	
	private FBLoginFragment mFBLoginFragment;
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null)
		{
			// Add the fragment on initial activity setup
			mFBLoginFragment = new FBLoginFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, mFBLoginFragment)
			.commit();
		}
		else
		{
			// Or set the fragment from restored state info
			mFBLoginFragment = (FBLoginFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}
	}
	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_diveboard_facebook_login);
//
//		// start Facebook Login
//		Session.openActiveSession(this, true, new Session.StatusCallback() {
//
//			// callback when session changes state
//			@Override
//			public void call(Session session, SessionState state, Exception exception) {
//				if (session.isOpened()) {
//					mToken = session.getAccessToken();
//					// make request to the /me API
//					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//
//						// callback after Graph API response with user object
//						@Override
//						public void onCompleted(GraphUser user, Response response) {
//							if (user != null) {
//								mId = user.getId();
//								mAuthTask = new UserLoginTask();
//								mAuthTask.execute((Void) null);
//							}
//						}
//					});
//				}
//			}
//		});
//	}
	
	

}
