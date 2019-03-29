package com.diveboard.model;

import android.content.Context;

import com.diveboard.dataaccess.LoginRepository;
import com.diveboard.dataaccess.LoginWithFacebookRepository;
import com.diveboard.dataaccess.SessionRepository;
import com.diveboard.dataaccess.SignUpRepository;
import com.diveboard.dataaccess.UserOfflineRepository;
import com.diveboard.dataaccess.datamodel.LoginResponse;
import com.diveboard.dataaccess.datamodel.SignUpResponse;
import com.diveboard.dataaccess.datamodel.StoredSession;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.util.Callback;
import com.diveboard.util.NetworkUtils;

public class AuthenticationService {
    private final SessionRepository sessionRepository;
    private final UserOfflineRepository userOfflineRepository;
    private Context context;

    public AuthenticationService(Context context) {
        this.context = context;
        sessionRepository = ((ApplicationController) context).getSessionRepository();
        userOfflineRepository = ((ApplicationController) context).getUserOfflineRepository();
    }

    public boolean isLoggedIn() {
        //TODO: check that userRepository.getUser(); returns value and initialized
        //TODO: move it out of cache directory?
        //TODO: user might be logged in in FB but not in the app. in this case login in the app should be reinitiated
        StoredSession session = sessionRepository.getSession();
        return session != null && session.isActive();
    }

    public void setTokenExpired() {
        sessionRepository.resetSession();
    }

    public void loginAsync(final String login, final String password, final Callback<LoginResponse> callback, Callback<String> errorCallback) {
        if (!NetworkUtils.isConnected(context)) {
            if (errorCallback != null) {
                errorCallback.execute(context.getString(R.string.no_internet));
            }
            return;
        }
        LoginRepository loginRepository = new LoginRepository(context);
        Callback<LoginResponse> intCallback = data -> {
            sessionRepository.saveSession(data);
            userOfflineRepository.save(data.user);
            if (callback != null) {
                callback.execute(data);
            }
        };
        loginRepository.login(login, password, intCallback, errorCallback);
    }

    public void loginWithFacebookAsync(final String userId, final String token, final Callback<LoginResponse> callback, Callback<String> errorCallback) {
        if (!NetworkUtils.isConnected(context)) {
            if (errorCallback != null) {
                errorCallback.execute(context.getString(R.string.no_internet));
            }
            return;
        }
        LoginWithFacebookRepository loginRepository = new LoginWithFacebookRepository(context);
        Callback<LoginResponse> intCallback = data -> {
            sessionRepository.saveSession(data);
            userOfflineRepository.save(data.user);
            if (callback != null) {
                callback.execute(data);
            }
        };
        loginRepository.login(userId, token, intCallback, errorCallback);
    }

    public void signUp(String email, String password, String confirmPassword, String nickname, boolean enableNewsletter, final Callback<SignUpResponse> callback) {
        if (!NetworkUtils.isConnected(context)) {
            if (callback != null) {
                SignUpResponse data = new SignUpResponse();
                data.fatalError = context.getString(R.string.no_internet);
                callback.execute(data);
            }
            return;
        }
        SignUpRepository repository = new SignUpRepository(context);
        repository.signUp(email, password, confirmPassword, nickname, enableNewsletter, callback);
    }

    public void logout() {
        throw new UnsupportedOperationException();
    }

    public StoredSession getSession() {
        return sessionRepository.getSession();
    }
}
