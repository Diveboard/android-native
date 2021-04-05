package com.diveboard.dataaccess;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.Spot;
import com.diveboard.dataaccess.datamodel.SpotsSearchResponse;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.model.AuthenticationService;
import com.diveboard.util.DiveboardJsonRequest;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class SearchSpotOnlineRepository implements SearchSpotRepository {
    private ApplicationController context;
    private AuthenticationService authenticationService;

    public SearchSpotOnlineRepository(ApplicationController context, AuthenticationService authenticationService) {
        this.context = context;
        this.authenticationService = authenticationService;
    }

    @Override
    public void search(final String term, final LatLng position, final LatLngBounds bounds, final ResponseCallback<List<Spot>> callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = AppConfig.SERVER_URL + "/api/search/spot?";
        url = RequestHelper.addCommonRequestArgs(url, authenticationService);
        if (!Utils.isNullOrEmpty(term)) {
            url += "&q=" + RequestHelper.encode(term);
        }
        if (position != null) {
            url += "&lng=" + position.longitude + "&lat=" + position.latitude;
        }

        DiveboardJsonRequest<SpotsSearchResponse> stringRequest = new DiveboardJsonRequest<>(
                Request.Method.GET, url,
                SpotsSearchResponse.class,
                new ResponseCallback<SpotsSearchResponse>() {
                    @Override
                    public void success(SpotsSearchResponse data) {
                        if (data.success) {
                            callback.success(data.getSpots());
                        } else {
                            callback.error(new Exception("Cannot get spots"));
                        }
                    }

                    @Override
                    public void error(Exception error) {
                        callback.error(error);
                    }
                });
        queue.add(stringRequest);
    }
}
