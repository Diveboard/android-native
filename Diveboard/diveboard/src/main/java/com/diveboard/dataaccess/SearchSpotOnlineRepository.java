package com.diveboard.dataaccess;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.SpotsSearchResponse;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.model.SearchSpot;
import com.diveboard.util.DiveboardJsonRequest;
import com.diveboard.util.ResponseCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class SearchSpotOnlineRepository implements SearchSpotRepository {
    private ApplicationController context;
    private String token;

    public SearchSpotOnlineRepository(ApplicationController context, String token) {
        this.context = context;
        this.token = token;
    }

    @Override
    //TODO: add cancellation token
    public void search(final String term, final LatLng position, final LatLngBounds bounds, final ResponseCallback<List<SearchSpot>> callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = AppConfig.SERVER_URL + "/api/search_spot_text";

        DiveboardJsonRequest<SpotsSearchResponse> stringRequest = new DiveboardJsonRequest<SpotsSearchResponse>(
                Request.Method.POST, url,
                SpotsSearchResponse.class,
                new ResponseCallback<SpotsSearchResponse>() {
                    @Override
                    public void success(SpotsSearchResponse data) {
                        if (data.success) {
                            callback.success(data.spots);
                        } else {
                            callback.error(new Exception("Cannot get spots"));
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
                    String bodyString = getRequestBody(term, position, bounds, token).toString();
                    return bodyString.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
        queue.add(stringRequest);
    }

    private JSONObject getRequestBody(String term, LatLng position, LatLngBounds bounds, String token) {
        JSONObject args = new JSONObject();
        try {
            args.put("term", term);
            args.put("auth_token", token);
            args.put("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF");
            if (position != null) {
                args.put("lat", Double.toString(position.latitude));
                args.put("lng", Double.toString(position.longitude));
            }
            if (bounds != null) {
                args.put("latSW", Double.toString(bounds.southwest.latitude));
                args.put("lngSW", Double.toString(bounds.southwest.longitude));
                args.put("latNE", Double.toString(bounds.northeast.latitude));
                args.put("lngNE", Double.toString(bounds.northeast.longitude));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return args;
    }
}
