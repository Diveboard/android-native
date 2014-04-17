package com.diveboard.mobile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.config.AppConfig;
import com.diveboard.model.DatabaseUpdater;
import com.diveboard.model.DiveboardModel;
import com.facebook.Session;
import com.google.analytics.tracking.android.EasyTracker;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;
import com.uservoice.uservoicesdk.activity.BaseActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bugsense.trace.BugSenseHandler;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class DiveboardLoginActivity extends FragmentActivity {
	
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private FBLoginFragment mFBLoginFragment;
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final int TEXT_SIZE = 13;
	public static final int TEXT_SIZE_BIG = 18;
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	private PascalLoginTask mPascalTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	public static Boolean TokenExpired = false;
	
	@Override
	protected void onResume()
	{
		super.onResume();
		BugSenseHandler.initAndStartSession(DiveboardLoginActivity.this, AppConfig.BUGSENSE_ID);
		
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.setDataReady(false);
		AC.setDataRefreshed(false);
		DiveboardModel model = new DiveboardModel(getApplicationContext());
		//DiveboardModel model = new DiveboardModel(30, getApplicationContext());
		AC.setModel(model);
		if (model.isLogged() == true)
		{
			Intent editDiveActivity = new Intent(DiveboardLoginActivity.this, DivesActivity.class);
		    startActivity(editDiveActivity);
		    return ;
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	public void goToSignUp(View view)
	{
		Intent signUpActivity = new Intent(DiveboardLoginActivity.this, SignUpActivity.class);
		startActivity(signUpActivity);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		Typeface faceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
		Typeface faceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
		((TextView)findViewById(R.id.sign_up)).setText(getResources().getString(R.string.signup_for_diveboard));
		((TextView)findViewById(R.id.sign_up)).setTypeface(faceB);
		((TextView)findViewById(R.id.sign_up)).setTextSize(TEXT_SIZE_BIG);
		((TextView)findViewById(R.id.email)).setTypeface(faceR);
		((TextView)findViewById(R.id.email)).setTextSize(TEXT_SIZE);
		((TextView)findViewById(R.id.password)).setTypeface(faceR);
		((TextView)findViewById(R.id.password)).setTextSize(TEXT_SIZE);
//		((TextView)findViewById(R.id.authButton)).setTypeface(faceR);
		((Button)findViewById(R.id.sign_in_button)).setTypeface(faceR);
		((Button)findViewById(R.id.sign_in_button)).setTextSize(TEXT_SIZE);
		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		//mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		Thread.UncaughtExceptionHandler mUEHandler = new Thread.UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				String timeTag = dateFormat.format(cal.getTime());
				System.err.println("Error occurred\n" + timeTag + ": " + e.getMessage());
				try {
					File root = new File(Environment.getExternalStorageDirectory(), "DiveboardErrors");
					if (!root.exists()) {
						root.mkdirs();
					}
					
					File gpxfile = new File(root, "Undhandled_errors.txt");
					FileWriter writer = new FileWriter(gpxfile, true);
					PrintWriter pw = new PrintWriter(writer);
					writer.append("\n\n" + timeTag + ": ");
					e.printStackTrace(pw);
					writer.flush();
					writer.close();
//					
				} catch (IOException er) {
					er.printStackTrace();
				}
				
				
	        }
	    };

		Thread.setDefaultUncaughtExceptionHandler(mUEHandler);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		if (ApplicationController.tokenExpired == true)
		{
			Toast.makeText(DiveboardLoginActivity.this, getResources().getString(R.string.token_expired), Toast.LENGTH_SHORT).show();
			ApplicationController.tokenExpired = false;
		}
		EasyTracker.getInstance(this).activityStart(this);
		DatabaseUpdater dbUpdater = new DatabaseUpdater(DiveboardLoginActivity.this);
		dbUpdater.launchUpdate();
		System.out.println("Attempting to download DB");
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	public void goToFBLogin(View view)
	{
		Intent editDiveActivity = new Intent(DiveboardLoginActivity.this, FBLoginActivity.class);
	    startActivity(editDiveActivity);
	}
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
//		if (mAuthTask != null) {
//			System.out.println("ya");
//			return;
//		}
		System.out.println("yo");
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 5 || mPassword.length() > 20) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
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

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(Void... params) {
			ApplicationController AC = (ApplicationController)getApplicationContext();
//			DiveboardModel model = new DiveboardModel(getApplicationContext());
			
//			AC.setModel(model);
			return AC.getModel().doLogin(mEmail, mPassword);
			//return 30;
		}

		@Override
		protected void onPostExecute(final JSONObject json) {
			if (json != null)
			{
				System.out.println("User Validated " + json.toString());
				//mAuthTask = null;
				showProgress(false);
				try {
					Boolean success = json.getBoolean("success");
					if (success == true)
					{
						showProgress(false);
						mEmailView.setText("");
						mPasswordView.setText("");
						Intent editDiveActivity = new Intent(DiveboardLoginActivity.this, DivesActivity.class);
					    startActivity(editDiveActivity);
					}
					else
					{
						Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrong_login), Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.BOTTOM, 0, 200);
						toast.show();
						showProgress(false);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.could_not_connect), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, 0, 200);
				toast.show();
				showProgress(false);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class PascalLoginTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			ApplicationController AC = (ApplicationController)getApplicationContext();
//			DiveboardModel model = new DiveboardModel(getApplicationContext());
			
//			AC.setModel(model);
			//return AC.getModel().doLogin(mEmail, mPassword);
			return 48;
		}

		@Override
		protected void onPostExecute(final Integer success) {
			mAuthTask = null;
			showProgress(false);

			if (success != -1) {
				Intent editDiveActivity = new Intent(DiveboardLoginActivity.this, DivesActivity.class);
			    startActivity(editDiveActivity);
			}
			else
			{
				Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.could_not_connect_db), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, 0, 200);
				toast.show();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
