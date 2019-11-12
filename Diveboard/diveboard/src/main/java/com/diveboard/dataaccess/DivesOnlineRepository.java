package com.diveboard.dataaccess;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DiveResponse;
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
                        VolleyMultipartRequest stringRequest = getGetDivesRequest(callback, data.dives);
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

    private VolleyMultipartRequest getGetDivesRequest(final ResponseCallback<DivesResponse, String> callback, List<Integer> dives) {
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
                Map<String, String> args = RequestHelper.getCommonRequestArgs(authenticationService);
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

    public void saveDive(Dive dive, ResponseCallback<DiveResponse, Exception> callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        VolleyMultipartRequest request = getSaveDiveRequest(callback, dive);
        queue.add(request);
    }

    private VolleyMultipartRequest getSaveDiveRequest(final ResponseCallback<DiveResponse, Exception> callback, Dive dive) {
        String url = AppConfig.SERVER_URL + "/api/V2/dive";
        return new VolleyMultipartRequest(Request.Method.POST, url, response -> {
            try {
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                DiveResponse data = (new Gson()).fromJson(json, DiveResponse.class);
                if (data.success) {
                    callback.success(data);
                } else {
                    if (data.errors != null && data.errors.length > 0) {
//TODO: include all the errors not the first one only
                        callback.error(new NetworkException(data.errors[0].message));
                    } else {
                        callback.error(new NetworkException("Cannot save dive. Unknown error"));
                    }
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
                args.put("arg", (new Gson()).toJson(dive));
                return args;
            }
        };
    }
}
