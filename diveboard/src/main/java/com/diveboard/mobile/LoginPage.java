package com.diveboard.mobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.diveboard.dataaccess.datamodel.LoginResponse;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginPage extends Fragment {

    /**
     * The default email to populate the email field with.
     */
    public static final int TEXT_SIZE = 13;
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private ApplicationController ac;
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);
        ac = (ApplicationController) getActivity().getApplicationContext();

        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = view.findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions(Arrays.asList("email"));
        fbLoginButton.setFragment(this);
        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                showProgress(true);
                AccessToken accessToken = loginResult.getAccessToken();
                ac.getAuthenticationService().loginWithFacebookAsync(accessToken.getUserId(), accessToken.getToken(), new ResponseCallback<LoginResponse>() {
                    @Override
                    public void success(LoginResponse data) {
                        ac.initCurrentUser();
                        Navigation.findNavController(view).navigate(R.id.logbook);
                    }

                    @Override
                    public void error(Exception error) {
                        Toast.makeText(ac, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Utils.logError(LoginPage.class, "Cannot login with facebook", error);
                    }
                });
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(ac, exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Typeface faceR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Light.ttf");
        ((TextView) view.findViewById(R.id.email)).setTypeface(faceR);
        ((TextView) view.findViewById(R.id.email)).setTextSize(TEXT_SIZE);
        ((TextView) view.findViewById(R.id.password)).setTypeface(faceR);
        ((TextView) view.findViewById(R.id.password)).setTextSize(TEXT_SIZE);
        ((Button) view.findViewById(R.id.sign_in_button)).setTypeface(faceR);
        ((Button) view.findViewById(R.id.sign_in_button)).setTextSize(TEXT_SIZE);
        if (savedInstanceState != null) {
            mEmail = savedInstanceState.getString(EXTRA_EMAIL);
        }
        mEmailView = view.findViewById(R.id.email);
        mPasswordView = view.findViewById(R.id.password);
        mLoginFormView = view.findViewById(R.id.login_form);
        mLoginStatusView = view.findViewById(R.id.login_status);
        mLoginStatusMessageView = view.findViewById(R.id.login_status_message);

        view.findViewById(R.id.sign_in_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin();
                    }
                });

        view.findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.signup);
            }
        });
        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
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
            ac.getAuthenticationService().loginAsync(mEmail, mPassword, new ResponseCallback<LoginResponse>() {
                @Override
                public void success(LoginResponse data) {
                    showProgress(false);
                    mEmailView.setText("");
                    mPasswordView.setText("");
                    Navigation.findNavController(mEmailView).navigate(R.id.logbook);
                }

                @Override
                public void error(Exception error) {
                    showProgress(false);
                    Toast.makeText(ac, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showProgress(final boolean show) {
        Utils.hideKeyboard(ac, mEmailView);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
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
}
