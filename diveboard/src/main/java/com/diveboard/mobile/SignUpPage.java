package com.diveboard.mobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.diveboard.dataaccess.datamodel.LoginResponse;
import com.diveboard.dataaccess.datamodel.SignUpResponse;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class SignUpPage extends Fragment {

    private static final int TEXT_SIZE = 13;
    private static final int TEXT_SIZE_BIG = 18;

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mNicknameView;
    private CheckBox mNewsletterView;
    private CheckBox mTermsView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private ApplicationController ac;
    private View mError;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_up, container, false);
        ac = (ApplicationController) getActivity().getApplicationContext();
        Typeface faceR = Typeface.createFromAsset(ac.getAssets(), "fonts/Lato-Light.ttf");
        Typeface faceB = ResourcesCompat.getFont(ac, R.font.lato_regular);
        ((TextView) view.findViewById(R.id.title)).setText(getResources().getString(R.string.signup_for_diveboard));
        ((TextView) view.findViewById(R.id.title)).setTypeface(faceB);
        ((TextView) view.findViewById(R.id.title)).setTextSize(TEXT_SIZE_BIG);
        ((TextView) view.findViewById(R.id.email)).setTypeface(faceR);
        ((TextView) view.findViewById(R.id.email)).setTextSize(TEXT_SIZE);
        ((TextView) view.findViewById(R.id.password)).setTypeface(faceR);
        ((TextView) view.findViewById(R.id.password)).setTextSize(TEXT_SIZE);
        ((TextView) view.findViewById(R.id.confirm_password)).setTypeface(faceR);
        ((TextView) view.findViewById(R.id.confirm_password)).setTextSize(TEXT_SIZE);
        ((TextView) view.findViewById(R.id.nickname)).setTypeface(faceR);
        ((TextView) view.findViewById(R.id.nickname)).setTextSize(TEXT_SIZE);
        ((TextView) view.findViewById(R.id.newsletter)).setTypeface(faceR);
        ((TextView) view.findViewById(R.id.newsletter)).setTextSize(TEXT_SIZE);
        ((TextView) view.findViewById(R.id.terms)).setTypeface(faceR);
        ((TextView) view.findViewById(R.id.terms)).setTextSize(TEXT_SIZE);
        ((TextView) view.findViewById(R.id.sign_in_button)).setTypeface(faceR);
        mEmailView = view.findViewById(R.id.email);
        mPasswordView = view.findViewById(R.id.password);
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
        mConfirmPasswordView = view.findViewById(R.id.confirm_password);
        mNicknameView = view.findViewById(R.id.nickname);
        mNewsletterView = view.findViewById(R.id.newsletter_check);
        mTermsView = view.findViewById(R.id.terms_check);
        mLoginFormView = view.findViewById(R.id.login_form);
        mLoginStatusView = view.findViewById(R.id.login_status);
        mLoginStatusMessageView = view.findViewById(R.id.login_status_message);
        mError = view.findViewById(R.id.error);
        view.findViewById(R.id.sign_in_button).setOnClickListener(view1 -> attemptLogin());
        //TODO: replace fb login button on sign up page as it is done on this page
        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mNicknameView.setError(null);
        mNewsletterView.setError(null);
        mTermsView.setError(null);
        mEmail = mEmailView.getText().toString();
        mEmail = mEmail.replace(" ", "");
        mPassword = mPasswordView.getText().toString();
        String mConfirmPassword = mConfirmPasswordView.getText().toString();
        String mNickname = mNicknameView.getText().toString();
        Boolean mNewsletter = mNewsletterView.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a check terms.
        if (!mTermsView.isChecked()) {
            mError.setVisibility(View.VISIBLE);
            focusView = mTermsView;
            cancel = true;
        } else
            mError.setVisibility(View.INVISIBLE);

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

        // Check for a confirm password.
        if (TextUtils.isEmpty(mConfirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (!mPassword.contentEquals(mConfirmPassword)) {
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

            ac.getAuthenticationService().signUp(mEmail, mPassword, mConfirmPassword, mNickname, mNewsletter, new SignUpCallback());
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        Utils.hideKeyboard(ac, mEmailView);

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
    }

    private class SignUpCallback implements ResponseCallback<SignUpResponse> {
        @Override
        public void success(SignUpResponse data) {
            if (data.success) {
                showProgress(true);
                ac.getAuthenticationService().loginAsync(mEmail, mPassword, new ResponseCallback<LoginResponse>() {
                    @Override
                    public void success(LoginResponse data) {
                        showProgress(false);
                        Navigation.findNavController(mEmailView).navigate(R.id.logbook);
                    }

                    @Override
                    public void error(Exception error) {
                        showProgress(false);
                        Utils.logError(SignUpPage.class, "Cannot login", error);
                        Toast.makeText(ac, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                showProgress(false);
                if (data.errors != null) {
                    for (int i = 0; i < data.errors.size(); i++) {
                        SignUpResponse.SignUpError error = data.errors.get(i);
                        if (error.params.contentEquals("nickname")) {
                            mNicknameView.setError(error.error);
                            mNicknameView.requestFocus();
                        } else if (error.params.contentEquals("password_check")) {
                            mConfirmPasswordView.setError(error.error);
                            mConfirmPasswordView.requestFocus();
                        } else if (error.params.contentEquals("password")) {
                            mPasswordView.setError(error.error);
                            mPasswordView.requestFocus();
                        } else if (error.params.contentEquals("email")) {
                            mEmailView.setError(error.error);
                            mEmailView.requestFocus();
                        }
                    }
                } else {
                    if (data.fatalError != null) {
                        Toast.makeText(ac, data.fatalError, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void error(Exception error) {
            Toast.makeText(ac, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
