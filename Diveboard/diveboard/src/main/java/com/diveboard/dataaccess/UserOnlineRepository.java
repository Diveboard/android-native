package com.diveboard.dataaccess;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.model.AuthenticationService;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.VolleyMultipartRequest;

import java.util.Map;

public class UserOnlineRepository {
    private Context context;
    private AuthenticationService authenticationService;

    public UserOnlineRepository(Context context, AuthenticationService authenticationService) {
        this.context = context;
        this.authenticationService = authenticationService;
    }

    public void getAsync(ResponseCallback<User, Exception> callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        VolleyMultipartRequest stringRequest = getGetUserRequest(callback, authenticationService.getSession().userId);
        queue.add(stringRequest);
    }

    private VolleyMultipartRequest getGetUserRequest(final ResponseCallback<User, Exception> callback, int userId) {
        String url = AppConfig.SERVER_URL + "/api/V2/user/" + userId;
        return new VolleyMultipartRequest(Request.Method.POST, url, response -> {

        }, error -> {
            callback.error(new NetworkException(error.getMessage()));
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> args = RequestHelper.getCommonRequestArgs(authenticationService);
                return args;
            }
        };
    }
}
