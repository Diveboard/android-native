package com.diveboard.dataaccess;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.model.AuthenticationService;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.VolleyMultipartRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DivesOnlineRepository {

    private Context context;
    private AuthenticationService authenticationService;
    private UserOfflineRepository userOfflineRepository;

    public DivesOnlineRepository(Context context, AuthenticationService authenticationService, UserOfflineRepository userOfflineRepository) {
        this.context = context;
        this.authenticationService = authenticationService;
        this.userOfflineRepository = userOfflineRepository;
    }

    public void load(final ResponseCallback<DivesResponse, String> callback) {
        ResponseCallback<User, Exception> intCallback = new ResponseCallback<User, Exception>() {
            @Override
            public void success(User data) {
                if (data != null) {
                    if (data.dives.size() == 0) {
                        callback.success(new DivesResponse());
                    } else {
                        RequestQueue queue = Volley.newRequestQueue(context);
                        VolleyMultipartRequest stringRequest = getRequest(callback, data.dives);
                        queue.add(stringRequest);
                    }
                }
            }

            @Override
            public void error(Exception s) {
                callback.error(s.getMessage());
            }
        };
        userOfflineRepository.getAsync(intCallback);
    }

    private VolleyMultipartRequest getRequest(final ResponseCallback<DivesResponse, String> callback, List<Integer> dives) {
        String url = AppConfig.SERVER_URL + "/api/V2/dive";
        return new VolleyMultipartRequest(Request.Method.POST, url, response -> {
            try {
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                if (callback != null) {
                    callback.success((new Gson()).fromJson(json, DivesResponse.class));
                }
            } catch (UnsupportedEncodingException e) {
                callback.error(e.getMessage());
            } catch (JsonSyntaxException e) {
                callback.error(e.getMessage());
            }
        }, error -> {
            if (callback != null) {
                callback.error(error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> args = new HashMap<>();
                args.put("auth_token", authenticationService.getSession().token);
                args.put("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF");
                args.put("flavour", "mobile");
                args.put("arg", getDives(dives).toString());
                return args;
            }
        };
    }

    private JSONArray getDives(List<Integer> dives) {
        JSONArray result = new JSONArray();
        try {
            for (Integer diveId : dives) {
                JSONObject value = new JSONObject();
                value.put("id", diveId);
                result.put(value);
            }
        } catch (JSONException e) {
            throw new RuntimeException();
        }
        return result;
    }
}
