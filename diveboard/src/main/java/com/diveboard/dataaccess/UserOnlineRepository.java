package com.diveboard.dataaccess;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.StoredSession;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.dataaccess.datamodel.UserResponse;
import com.diveboard.model.AuthenticationService;
import com.diveboard.util.DiveboardRequest;
import com.diveboard.util.ResponseCallback;

import java.util.Map;

import static com.diveboard.mobile.ApplicationController.getGsonWithExclude;

public class UserOnlineRepository {
    private Context context;
    private AuthenticationService authenticationService;

    public UserOnlineRepository(Context context, AuthenticationService authenticationService) {
        this.context = context;
        this.authenticationService = authenticationService;
    }

    public void getAsync(ResponseCallback<User> callback) {
        StoredSession session = authenticationService.getSession();
        if (session == null) {
            throw new RuntimeException("User is not authenticated or session expired");
        }
        Volley.newRequestQueue(context).add(getGetUserRequest(callback, session.userId));
    }

    private DiveboardRequest getGetUserRequest(final ResponseCallback<User> callback, int userId) {
        String url = AppConfig.SERVER_URL + "/api/V2/user/" + userId;
        return new DiveboardRequest<UserResponse>(Request.Method.POST,
                url,
                UserResponse.class,
                response -> callback.success(response.result),
                callback::error) {
            @Override
            protected Map<String, String> getParams() {
                return RequestHelper.getCommonRequestArgs(authenticationService);
            }
        };
    }

    private DiveboardRequest getSaveUserRequest(final ResponseCallback<User> callback, User user) {
        String url = AppConfig.SERVER_URL + "/api/V2/user/";
        return new DiveboardRequest<UserResponse>(Request.Method.POST,
                url,
                UserResponse.class,
                response -> callback.success(response.result),
                callback::error) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> args = RequestHelper.getCommonRequestArgs(authenticationService);
                args.put("arg", getGsonWithExclude().toJson(user));
                return args;
            }
        };
    }

    public void saveAsync(ResponseCallback<User> callback, User user) {
        Volley.newRequestQueue(context).add(getSaveUserRequest(callback, user));
    }
}
