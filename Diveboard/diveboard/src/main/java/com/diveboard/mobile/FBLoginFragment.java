package com.diveboard.mobile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.diveboard.model.DiveboardModel;
import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;

public class FBLoginFragment extends Fragment{
	private UserLoginTask mAuthTask = null;
	private String mToken;
	private String mId;
	private static final String TAG = "FBLoginFragment";
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			//onSessionStateChange(session, state, exception);
			mToken = session.getAccessToken();
			// make request to the /me API
			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

				// callback after Graph API response with user object
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						System.out.println("yo");
						mId = user.getId();
						mAuthTask = new UserLoginTask();
						mAuthTask.execute((Void) null);
					}
				}
			});
		}
	};
	private UiLifecycleHelper uiHelper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fblogin, container, false);
		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
		DiveboardModel model = new DiveboardModel(getActivity().getApplicationContext());
		//DiveboardModel model = new DiveboardModel(30, getApplicationContext());
		AC.setModel(model);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		// For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception)
	{
		if (state.isOpened())
		{
			Log.i(TAG, "Logged in...");
			
		}
		else if (state.isClosed())
		{
			Log.i(TAG, "Logged out...");
		}
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
			System.out.println(mId + " " + mToken);
			return AC.getModel().doFbLogin(mId, mToken);
		}

		@Override
		protected void onPostExecute(final Integer success) {
			mAuthTask = null;

			if (success != -1) {
				Intent editDiveActivity = ApplicationController.getDivesActivity(getActivity());
			    startActivity(editDiveActivity);
			}
//			else if (success == 0 ){
//				mPasswordView
//						.setError(getString(R.string.error_incorrect_password));
//				mPasswordView.requestFocus();
//			}
			else
			{
				Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.could_not_connect_db), Toast.LENGTH_SHORT);
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
