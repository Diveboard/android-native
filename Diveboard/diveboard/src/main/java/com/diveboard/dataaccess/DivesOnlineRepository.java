package com.diveboard.dataaccess;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.DeleteResponse;
import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DiveResponse;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.dataaccess.datamodel.ResponseBase;
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
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class DivesOnlineRepository {

    private Context context;
    private AuthenticationService authenticationService;
    private UserOnlineRepository userOnlineRepository;

    public DivesOnlineRepository(Context context, AuthenticationService authenticationService, UserOnlineRepository userOnlineRepository) {
        this.context = context;
        this.authenticationService = authenticationService;
        this.userOnlineRepository = userOnlineRepository;
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
        userOnlineRepository.getAsync(intCallback);
        Log.d(DivesOnlineRepository.class.toString(), "Get dives online");
    }

    private VolleyMultipartRequest getGetDivesRequest(final ResponseCallback<DivesResponse, String> callback, List<Integer> dives) {
        String url = AppConfig.SERVER_URL + "/api/V2/dive";
        return new VolleyMultipartRequest(Request.Method.POST, url, response -> {
            try {
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                DivesResponse data = (new Gson()).fromJson(json, DivesResponse.class);
                if (data.er)
                callback.success(data);
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
            handleResponse(response, callback, DiveResponse.class);
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

    public void deleteDive(Dive dive, ResponseCallback<DeleteResponse, Exception> callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        VolleyMultipartRequest request = getDeleteDiveRequest(callback, dive);
        queue.add(request);
    }

    private VolleyMultipartRequest getDeleteDiveRequest(ResponseCallback<DeleteResponse, Exception> callback, Dive dive) {
        String url = AppConfig.SERVER_URL + "/api/V2/dive/" + dive.id + "?";
        Map<String, String> params = RequestHelper.getCommonRequestArgs(authenticationService);
        for (String key : params.keySet()) {
            try {
                url += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params.get(key), "UTF-8") + "&";
            } catch (UnsupportedEncodingException e) {
            }
        }
        url = url.substring(0, url.length() - 1);
        return new VolleyMultipartRequest(Request.Method.DELETE, url, response -> {
            handleResponse(response, callback, DeleteResponse.class);
        }, error -> callback.error(new NetworkException(error.getMessage()))) {
        };
    }

    private <R extends ResponseBase<?>> void handleResponse(NetworkResponse response, ResponseCallback<R, Exception> callback, Class<R> classOfR) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            R data = (new Gson()).fromJson(json, classOfR);
            if (data.success) {
                callback.success(data);
            } else {
                callback.error(new DiveboardApiException(data.errors));
            }
        } catch (UnsupportedEncodingException e) {
            callback.error(e);
        } catch (JsonSyntaxException e) {
            callback.error(e);
        }
    }
}
