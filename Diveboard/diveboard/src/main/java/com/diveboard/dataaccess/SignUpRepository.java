package com.diveboard.dataaccess;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.SignUpResponse;
import com.diveboard.util.Callback;
import com.diveboard.util.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class SignUpRepository {
    private Context context;

    public SignUpRepository(Context context) {
        this.context = context;
    }

    public void signUp(String email, String password, String confirmPassword, String nickname, boolean enableNewsletter, final Callback<SignUpResponse> callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        GsonRequest<SignUpResponse> stringRequest = getRequest(email, password, confirmPassword, nickname, enableNewsletter, callback);
        queue.add(stringRequest);
    }

    private GsonRequest<SignUpResponse> getRequest(final String email, final String password, final String confirmPassword, final String nickname, final boolean enableNewsletter, final Callback<SignUpResponse> callback) {
        String url = AppConfig.SERVER_URL + "/api/register_email";

        return new GsonRequest<SignUpResponse>(
                Request.Method.POST, url,
                SignUpResponse.class,
                new Response.Listener<SignUpResponse>() {
                    @Override
                    public void onResponse(SignUpResponse response) {
                        if (callback != null) {
                            callback.execute(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) {
                            SignUpResponse data = new SignUpResponse();
                            data.fatalError = error.getMessage();
                            callback.execute(data);
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
