package com.diveboard.dataaccess;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.LoginResponse;
import com.diveboard.mobile.R;
import com.diveboard.util.DiveboardJsonRequest;
import com.diveboard.util.ResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginRepository {
    private Context context;

    public LoginRepository(Context context) {
        this.context = context;
    }

    public void login(final String login, final String password, final ResponseCallback<LoginResponse> callback) {
        Volley.newRequestQueue(context).add(getLoginRequest(login, password, callback));
    }

    private Request getLoginRequest(final String login, final String password, final ResponseCallback<LoginResponse> callback) {
        String url = AppConfig.SERVER_URL + "/api/login_email";

        return new DiveboardJsonRequest<LoginResponse>(
                Request.Method.POST, url,
                LoginResponse.class,
                new ResponseCallback<LoginResponse>() {
                    @Override
                    public void success(LoginResponse response) {
                        if (response.success) {
                            callback.success(response);
                        } else {
                            callback.error(new Exception(context.getString(R.string.cannot_login_message) + " '" + response.message + "'"));
                        }
                    }

                    @Override
                    public void error(Exception error) {
                        callback.error(error);
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
