package com.diveboard.mobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			getActionBar().hide();
		}
		else
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
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
		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mConfirmPassword = mConfirmPasswordView.getText().toString();
		mURL = mURLView.getText().toString();
		mNickname = mNicknameView.getText().toString();
		mNewsletter = mNewsletterView.isChecked();
		mTerms = mTermsView.isChecked();

		boolean cancel = false;
		View focusView = null;

		// Check for a confirm password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
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
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword);
				}
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
