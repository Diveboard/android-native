package com.diveboard.dataaccess;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.mobile.R;
import com.diveboard.dataaccess.datamodel.LoginResponse;
import com.diveboard.util.Callback;
import com.diveboard.util.GsonRequest;
import com.diveboard.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginRepository {
    private Context context;

    public LoginRepository(Context context) {
        this.context = context;
    }

    public void login(final String login, final String password, final Callback<LoginResponse> callback, final Callback<String> errorCallback) {
        if (!NetworkUtils.isConnected(context)) {
            if (errorCallback != null) {
                errorCallback.execute(context.getString(R.string.no_internet));
            }
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        GsonRequest<LoginResponse> stringRequest = getLoginRequest(login, password, callback, errorCallback);
        queue.add(stringRequest);
    }

    private GsonRequest<LoginResponse> getLoginRequest(final String login,
                                                       final String password, final Callback<LoginResponse> callback,
                                                       final Callback<String> errorCallback) {
        String url = AppConfig.SERVER_URL + "/api/login_email";

        return new GsonRequest<LoginResponse>(
                Request.Method.POST, url,
                LoginResponse.class,
                new Response.Listener<LoginResponse>() {
                    @Override
                    public void onResponse(LoginResponse response) {
                        if (response.success) {
                            if (callback != null) {
                                callback.execute(response);
                            }
                        } else {
                            if (errorCallback != null) {
                                errorCallback.execute(context.getString(R.string.cannot_login_message) + " '" + response.message + "'");
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (errorCallback != null) {
                            errorCallback.execute(error.getMessage());
                        }
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return getRequestBody(login, password).toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
    }

    private JSONObject getRequestBody(String login, String password) {
        JSONObject args = new JSONObject();
        try {
            args.put("email", login);
            args.put("password", password);
            args.put("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF");
            args.put("extralong_token", "true");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return args;
    }
}
