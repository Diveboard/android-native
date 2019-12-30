package com.diveboard.dataaccess;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.SignUpResponse;
import com.diveboard.util.DiveboardJsonRequest;
import com.diveboard.util.ResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class SignUpRepository {
    private Context context;

    public SignUpRepository(Context context) {
        this.context = context;
    }

    public void signUp(String email, String password, String confirmPassword, String nickname, boolean enableNewsletter, final ResponseCallback<SignUpResponse> callback) {
        Volley.newRequestQueue(context).add(getRequest(email, password, confirmPassword, nickname, enableNewsletter, callback));
    }

    private Request getRequest(final String email, final String password, final String confirmPassword, final String nickname, final boolean enableNewsletter, final ResponseCallback<SignUpResponse> callback) {
        String url = AppConfig.SERVER_URL + "/api/register_email";

        return new DiveboardJsonRequest<SignUpResponse>(
                Request.Method.POST, url,
                SignUpResponse.class,
                callback) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return getRequestBody(email, password, confirmPassword, nickname, enableNewsletter).toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
    }

    private JSONObject getRequestBody(String email, String password, String confirmPassword, String nickname, boolean enableNewsletter) {
        JSONObject args = new JSONObject();
        try {
            args.put("email", email);
            args.put("assign_vanity_url", "true");
            args.put("password", password);
            args.put("nickname", nickname);
            args.put("password_check", confirmPassword);
            args.put("accept_newsletter_email", enableNewsletter ? "true" : "false");
            args.put("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return args;
    }
}
