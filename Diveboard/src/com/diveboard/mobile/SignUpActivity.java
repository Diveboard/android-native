package com.diveboard.mobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class SignUpActivity extends FragmentActivity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	public static final int TEXT_SIZE = 15;

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private FBLoginFragment mFBLoginFragment;
	private UserLoginTask mAuthTask = null;
	private LoginTask mLoginTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private String mConfirmPassword;
	private String mURL;
	private String mNickname;
	private Boolean mNewsletter = false;
	private Boolean mTerms = false;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mConfirmPasswordView;
	private EditText mURLView;
	private EditText mNicknameView;
	private CheckBox mNewsletterView;
	private CheckBox mTermsView;
	private View mLoginFormView;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		Typeface faceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
		Typeface faceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
		((TextView)findViewById(R.id.title)).setText("SIGNUP FOR DIVEBOARD");
		((TextView)findViewById(R.id.title)).setTypeface(faceB);
		((TextView)findViewById(R.id.email)).setTypeface(faceR);
		((TextView)findViewById(R.id.email)).setTextSize(TEXT_SIZE);
		((TextView)findViewById(R.id.password)).setTypeface(faceR);
		((TextView)findViewById(R.id.password)).setTextSize(TEXT_SIZE);
		((TextView)findViewById(R.id.confirm_password)).setTypeface(faceR);
		((TextView)findViewById(R.id.confirm_password)).setTextSize(TEXT_SIZE);
		((TextView)findViewById(R.id.url)).setTypeface(faceR);
		((TextView)findViewById(R.id.url)).setTextSize(TEXT_SIZE);
		((TextView)findViewById(R.id.nickname)).setTypeface(faceR);
		((TextView)findViewById(R.id.nickname)).setTextSize(TEXT_SIZE);
		((TextView)findViewById(R.id.newsletter)).setTypeface(faceR);
		((TextView)findViewById(R.id.newsletter)).setTextSize(TEXT_SIZE);
		((TextView)findViewById(R.id.terms)).setTypeface(faceR);
		((TextView)findViewById(R.id.terms)).setTextSize(TEXT_SIZE);
		((TextView)findViewById(R.id.sign_in_button)).setTypeface(faceR);
		((TextView)findViewById(R.id.authButton)).setTypeface(faceR);
		// Set up the login form.
		//mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		//mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		mConfirmPasswordView = (EditText)findViewById(R.id.confirm_password);
		mURLView = (EditText)findViewById(R.id.url);
		mNicknameView = (EditText)findViewById(R.id.nickname);
		mNewsletterView = (CheckBox)findViewById(R.id.newsletter_check);
		mTermsView = (CheckBox)findViewById(R.id.terms_check);
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
//		if (savedInstanceState == null)
//		{
//			// Add the fragment on initial activity setup
//			mFBLoginFragment = new FBLoginFragment();
//			getSupportFragmentManager()
//			.beginTransaction()
//			.add(R.id.login_fields, mFBLoginFragment)
//			.commit();
//		}
//		else
//		{
//			// Or set the fragment from restored state info
//			mFBLoginFragment = (FBLoginFragment) getSupportFragmentManager()
//					.findFragmentById(R.id.login_fields);
//		}
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	public void goToFBLogin(View view)
	{
		Intent editDiveActivity = new Intent(SignUpActivity.this, FBLoginActivity.class);
	    startActivity(editDiveActivity);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
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

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mConfirmPasswordView.setError(null);
		mURLView.setError(null);
		mNicknameView.setError(null);
		mNewsletterView.setError(null);
		mTermsView.setError(null);
		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mEmail = mEmail.replace(" ", "");
		mPassword = mPasswordView.getText().toString();
		mConfirmPassword = mConfirmPasswordView.getText().toString();
		mURL = mURLView.getText().toString();
		mURL = mURL.replace(" ", "");
		mNickname = mNicknameView.getText().toString();
		mNewsletter = mNewsletterView.isChecked();
		mTerms = mTermsView.isChecked();

		boolean cancel = false;
		View focusView = null;

		// Check for a check terms.
		if (!mTermsView.isChecked()) {
			mTermsView.setError(getString(R.string.error_check_required));
			focusView = mTermsView;
			cancel = true;
		}
		
		// Check for a valid Nickname.
		if (TextUtils.isEmpty(mNickname)) {
			mNicknameView.setError(getString(R.string.error_field_required));
			focusView = mNicknameView;
			cancel = true;
		} else if (mNickname.length() < 3 || mNickname.length() > 30) {
			mNicknameView.setError(getString(R.string.error_invalid_nickname));
			focusView = mNicknameView;
			cancel = true;
		}
		String pattern = "^[A-Za-z\\.0-9\\-\\_]*$";
		// Check for a valid URL.
		if (TextUtils.isEmpty(mURL)) {
			mURLView.setError(getString(R.string.error_field_required));
			focusView = mURLView;
			cancel = true;
		} else if (mURL.length() < 4) {
			mURLView.setError(getString(R.string.error_short_url));
			focusView = mURLView;
			cancel = true;
		} else if (!mURL.matches(pattern))
		{
			mURLView.setError(getString(R.string.error_invalid_url));
			focusView = mURLView;
			cancel = true;
		}
		
		// Check for a confirm password.
		if (TextUtils.isEmpty(mConfirmPassword)) {
			mConfirmPasswordView.setError(getString(R.string.error_field_required));
			focusView = mConfirmPasswordView;
			cancel = true;
		} else if (!mPassword.contentEquals(mConfirmPassword))
		{
			mConfirmPasswordView.setError(getString(R.string.error_same_field));
			focusView = mConfirmPasswordView;
			cancel = true;
		}
		
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
		} else if (!mEmail.contains("@") || !mEmail.contains(".")) {
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
			return AC.getModel().doRegister(mEmail, mPassword, mConfirmPassword, mURL, mNickname, mNewsletter);
		}

		@Override
		protected void onPostExecute(final JSONObject json) {
			System.out.println(json.toString());
			mAuthTask = null;
			showProgress(false);
			try {
				Boolean success = json.getBoolean("success");
				if (success == true)
				{
					showProgress(true);
					mLoginTask = new LoginTask();
					mLoginTask.execute((Void) null);
				}
				else
				{
					JSONArray jsonArray = (JSONArray)json.getJSONArray("errors");
					for (int i = 0; i < jsonArray.length(); i++)
					{
						String error = ((JSONObject)jsonArray.get(i)).getString("error");
						String params = ((JSONObject)jsonArray.get(i)).getString("params");
						if (params.contentEquals("nickname"))
						{
							mNicknameView.setError(error);
							mNicknameView.requestFocus();
						}
						else if (params.contentEquals("vanity_url"))
						{
							mURLView.setError(error);
							mURLView.requestFocus();
						}
						else if (params.contentEquals("password_check"))
						{
							mConfirmPasswordView.setError(error);
							mConfirmPasswordView.requestFocus();
						}
						else if (params.contentEquals("password"))
						{
							mPasswordView.setError(error);
							mPasswordView.requestFocus();
						}
						else if (params.contentEquals("email"))
						{
							mEmailView.setError(error);
							mEmailView.requestFocus();
						}
					}
//					String error = ((JSONObject)json.getJSONObject("errors")).getString("error");
//					String params = json.getString("params");
//					System.out.println(error + " " + params);
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	public class LoginTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			ApplicationController AC = (ApplicationController)getApplicationContext();
			return AC.getModel().doLogin(mEmail, mPassword);
		}

		@Override
		protected void onPostExecute(final Integer success) {
			mLoginTask = null;
			showProgress(false);
			if (success != -1)
			{
				Intent editDiveActivity = new Intent(SignUpActivity.this, DivesActivity.class);
			    startActivity(editDiveActivity);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
