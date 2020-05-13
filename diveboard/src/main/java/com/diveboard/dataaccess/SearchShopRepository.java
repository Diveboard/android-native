package com.diveboard.dataaccess;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.SearchShopResponse;
import com.diveboard.model.AuthenticationService;
import com.diveboard.util.DiveboardJsonRequest;
import com.diveboard.util.ResponseCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchShopRepository {

    private Context context;
    private AuthenticationService authenticationService;

    public SearchShopRepository(Context context, AuthenticationService authenticationService) {
        this.context = context;
        this.authenticationService = authenticationService;
    }

    public void search(String q, final ResponseCallback<SearchShopResponse> callback) {
        Log.d(SearchShopRepository.class.toString(), "Search by: " + q);
        Volley.newRequestQueue(context).add(getSearchRequest(q, callback));
    }

    private Request getSearchRequest(String q, final ResponseCallback<SearchShopResponse> callback) {
        String url;
        try {
            url = RequestHelper.addCommonRequestArgs(AppConfig.SERVER_URL + "/api/search/shop.json?q=" + URLEncoder.encode(q, "UTF-8") + "&", authenticationService);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return new DiveboardJsonRequest<>(Request.Method.GET, url, SearchShopResponse.class, callback);
    }
}
