package com.diveboard.dataaccess;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.dataaccess.datamodel.UserResponse;
import com.diveboard.model.AuthenticationService;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.VolleyMultipartRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
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
            //TODO: refactor this, extract method. This code is duplicated everywhere in online repos
            try {
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                UserResponse data = (new Gson()).fromJson(json, UserResponse.class);
                if (data.success) {
                    callback.success(data.result);
                } else {
                    callback.error(new DiveboardApiException(data.errors));
                }
            } catch (UnsupportedEncodingException e) {
                callback.error(e);
            } catch (JsonSyntaxException e) {
                callback.error(e);
            }
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
