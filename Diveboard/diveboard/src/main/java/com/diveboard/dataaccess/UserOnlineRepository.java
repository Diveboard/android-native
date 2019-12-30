package com.diveboard.dataaccess;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.dataaccess.datamodel.UserResponse;
import com.diveboard.model.AuthenticationService;
import com.diveboard.util.DiveboardRequest;
import com.diveboard.util.ResponseCallback;

import java.util.Map;

public class UserOnlineRepository {
    private Context context;
    private AuthenticationService authenticationService;

    public UserOnlineRepository(Context context, AuthenticationService authenticationService) {
        this.context = context;
        this.authenticationService = authenticationService;
    }

    public void getAsync(ResponseCallback<User> callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        DiveboardRequest stringRequest = getGetUserRequest(callback, authenticationService.getSession().userId);
        stringRequest.getTimeoutMs();
        queue.add(stringRequest);
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
}
