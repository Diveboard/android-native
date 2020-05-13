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
import com.diveboard.mobile.R;
import com.diveboard.util.NetworkUtils;
import com.diveboard.util.ResponseCallback;

public class AuthenticationService {
    private final SessionRepository sessionRepository;
    private final UserOfflineRepository userOfflineRepository;
    private Context context;

    public AuthenticationService(Context context,
                                 SessionRepository sessionRepository,
                                 UserOfflineRepository userOfflineRepository) {
        this.context = context;
        this.sessionRepository = sessionRepository;
        this.userOfflineRepository = userOfflineRepository;
    }

    public boolean isLoggedIn() {
        //TODO: check that userRepository.getUser(); returns diveType and initialized
        //TODO: user might be logged in in FB but not in the app. in this case login in the app should be reinitiated
        StoredSession session = sessionRepository.getSession();
        return session != null && session.isActive();
    }

    public void loginAsync(final String login, final String password, final ResponseCallback<LoginResponse> callback) {
        if (!NetworkUtils.isConnected(context)) {
            callback.error(new Exception(context.getString(R.string.no_internet)));
            return;
        }
        ResponseCallback<LoginResponse> intCallback = new ResponseCallback<LoginResponse>() {
            @Override
            public void success(LoginResponse data) {
                sessionRepository.saveSession(data);
                userOfflineRepository.save(data.user);
                if (callback != null) {
                    callback.success(data);
                }
            }

            @Override
            public void error(Exception error) {
                callback.error(error);
            }
        };
        LoginRepository loginRepository = new LoginRepository(context);
        loginRepository.login(login, password, intCallback);
    }

    public void loginWithFacebookAsync(final String userId, final String token, final ResponseCallback<LoginResponse> callback) {
        if (!NetworkUtils.isConnected(context)) {
            callback.error(new Exception(context.getString(R.string.no_internet)));
            return;
        }
        LoginWithFacebookRepository loginRepository = new LoginWithFacebookRepository(context);
        ResponseCallback<LoginResponse> intCallback = new ResponseCallback<LoginResponse>() {
            @Override
            public void success(LoginResponse data) {
                sessionRepository.saveSession(data);
                userOfflineRepository.save(data.user);
                callback.success(data);
            }

            @Override
            public void error(Exception error) {
                callback.error(error);
            }
        };
        loginRepository.login(userId, token, intCallback);
    }

    public void signUp(String email, String password, String confirmPassword, String nickname, boolean enableNewsletter, final ResponseCallback<SignUpResponse> callback) {
        if (!NetworkUtils.isConnected(context)) {
            callback.error(new Exception(context.getString(R.string.no_internet)));
            return;
        }
        SignUpRepository repository = new SignUpRepository(context);
        repository.signUp(email, password, confirmPassword, nickname, enableNewsletter, callback);
    }

    public StoredSession getSession() {
        return sessionRepository.getSession();
    }
}
