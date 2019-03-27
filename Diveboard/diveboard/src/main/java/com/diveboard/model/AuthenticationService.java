package com.diveboard.model;

import android.content.Context;

import com.diveboard.dataaccess.LoginRepository;
import com.diveboard.dataaccess.LoginWithFacebookRepository;
import com.diveboard.dataaccess.SignUpRepository;
import com.diveboard.dataaccess.datamodel.LoginResponse;
import com.diveboard.dataaccess.datamodel.SignUpResponse;
import com.diveboard.util.Callback;

public class AuthenticationService {
    private Context context;
    private boolean tokenExpired = true;

    public AuthenticationService(Context context) {
        this.context = context;
    }

    public boolean isLoggedIn() {
        return false;
    }

    public boolean isTokenExpired() {
        return tokenExpired;
    }

    public void setTokenExpired(boolean value) {
        tokenExpired = value;
    }

    public void loginAsync(final String login, final String password, final Callback<LoginResponse> callback, Callback<String> errorCallback) {
        LoginRepository loginRepository = new LoginRepository(context);
        Callback<LoginResponse> intCallback = new Callback<LoginResponse>() {
            @Override
            public void execute(LoginResponse data) {
                // TODO: store id and token and expiration date
                if (callback != null) {
                    callback.execute(data);
                }
            }
        };
        loginRepository.login(login, password, intCallback, errorCallback);
    }

    public void facebookLoginAsync(final String userId, final String token, final Callback<LoginResponse> callback, Callback<String> errorCallback) {
        LoginWithFacebookRepository loginRepository = new LoginWithFacebookRepository(context);
        Callback<LoginResponse> intCallback = new Callback<LoginResponse>() {
            @Override
            public void execute(LoginResponse data) {
                // TODO: store id and token and expiration date
                if (callback != null) {
                    callback.execute(data);
                }
            }
        };
        loginRepository.login(userId, token, intCallback, errorCallback);
    }

    public void signUp(String email, String password, String confirmPassword, String nickname, boolean enableNewsletter, final Callback<SignUpResponse> callback) {
        //TODO: check internet available
        SignUpRepository repository = new SignUpRepository(context);
        Callback<SignUpResponse> intCallback = new Callback<SignUpResponse>() {
            @Override
            public void execute(SignUpResponse data) {
                // TODO: store id and token and expiration date
                if (callback != null) {
                    callback.execute(data);
                }
            }
        };
        repository.signUp(email, password, confirmPassword, nickname, enableNewsletter, intCallback);
    }

    public void logout() {
        throw new UnsupportedOperationException();
    }
}
