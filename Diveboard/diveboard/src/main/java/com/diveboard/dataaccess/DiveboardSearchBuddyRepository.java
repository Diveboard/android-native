package com.diveboard.dataaccess;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.SearchBuddyResponse;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.VolleyMultipartRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DiveboardSearchBuddyRepository {

    private Context context;

    public DiveboardSearchBuddyRepository(Context context) {
        this.context = context;
    }

    public void search(String q, final ResponseCallback<SearchBuddyResponse, Exception> callback) {
        Log.d(DiveboardSearchBuddyRepository.class.toString(), "Search by: " + q);
        RequestQueue queue = Volley.newRequestQueue(context);
        VolleyMultipartRequest request = getSearchRequest(q, callback);
        queue.add(request);
    }

    private VolleyMultipartRequest getSearchRequest(String q, final ResponseCallback<SearchBuddyResponse, Exception> callback) {
        String url;
        try {
            url = AppConfig.SERVER_URL + "/api/search/user.json?q=" + URLEncoder.encode(q, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return new VolleyMultipartRequest(Request.Method.GET, url, response -> {
            try {
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                SearchBuddyResponse data = (new Gson()).fromJson(json, SearchBuddyResponse.class);
                callback.success(data);
            } catch (UnsupportedEncodingException e) {
                callback.error(e);
            } catch (JsonSyntaxException e) {
                callback.error(e);
            }
        }, error -> callback.error(error)) {
        };
    }
}
