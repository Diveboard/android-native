package com.diveboard.dataaccess;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.SearchBuddyResponse;
import com.diveboard.util.DiveboardJsonRequest;
import com.diveboard.util.ResponseCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DiveboardSearchBuddyRepository {

    private Context context;

    public DiveboardSearchBuddyRepository(Context context) {
        this.context = context;
    }

    public void search(String q, final ResponseCallback<SearchBuddyResponse> callback) {
        Log.d(this.getClass().getSimpleName(), "Search by: " + q);
        Volley.newRequestQueue(context).add(getSearchRequest(q, callback));
    }

    private Request getSearchRequest(String q, final ResponseCallback<SearchBuddyResponse> callback) {
        String url;
        try {
            url = AppConfig.SERVER_URL + "/api/search/user.json?q=" + URLEncoder.encode(q, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return new DiveboardJsonRequest<>(Request.Method.GET, url, SearchBuddyResponse.class, callback);
    }
}
