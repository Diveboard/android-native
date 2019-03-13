package com.diveboard.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.util.Callback;
import com.diveboard.util.GsonRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class SpotOnlineRepository implements SpotRepository {
    private Context context;

    public SpotOnlineRepository(Context context) {
        this.context = context;
    }

    @Override
    public void search(final String term, final LatLng position, final LatLng positionSW, final LatLng positionNE, final String token, int userId, final Callback<List<Spot2>> callback, final Callback<String> errorCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = AppConfig.SERVER_URL + "/api/search_spot_text";

        GsonRequest<SpotsSearchResponse> stringRequest = new GsonRequest<SpotsSearchResponse>(
                Request.Method.POST, url,
                SpotsSearchResponse.class,
                new Response.Listener<SpotsSearchResponse>() {
                    @Override
                    public void onResponse(SpotsSearchResponse response) {
                        if (callback != null) {
                            callback.execute(response.spots);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (errorCallback != null) {
                            errorCallback.execute(error.getMessage());
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
                    String bodyString = getRequestBody(term, position, positionSW, positionNE, token).toString();
                    return bodyString.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
        queue.add(stringRequest);
    }

    private JSONObject getRequestBody(String term, LatLng position, LatLng positionSW, LatLng positionNE, String token) {
        JSONObject args = new JSONObject();
        try {
            args.put("term", term);
            args.put("auth_token", token);
            args.put("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF");
            if (position != null) {
                args.put("lat", Double.toString(position.latitude));
                args.put("lng", Double.toString(position.longitude));
            }
            if (positionSW != null) {
                args.put("latSW", Double.toString(positionSW.latitude));
                args.put("lngSW", Double.toString(positionSW.latitude));
            }
            if (positionNE != null) {
                args.put("latNE", Double.toString(positionNE.latitude));
                args.put("lngNE", Double.toString(positionNE.latitude));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return args;
    }
}
