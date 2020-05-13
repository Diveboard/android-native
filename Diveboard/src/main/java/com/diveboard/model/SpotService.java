package com.diveboard.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.diveboard.dataaccess.SearchSpotOfflineRepository;
import com.diveboard.dataaccess.SearchSpotOnlineRepository;
import com.diveboard.dataaccess.SearchSpotRepository;
import com.diveboard.dataaccess.SessionRepository;
import com.diveboard.dataaccess.datamodel.Spot;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.util.ResponseCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class SpotService {
    private ConnectivityManager connectivityManager;
    private ApplicationController context;
    private SessionRepository sessionRepository;
    private AuthenticationService authenticationService;

    public SpotService(ApplicationController context, SessionRepository sessionRepository, AuthenticationService authenticationService) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = context;
        this.sessionRepository = sessionRepository;
        this.authenticationService = authenticationService;
    }

    public void searchSpot(String term, LatLng position, LatLngBounds bounds, ResponseCallback<List<Spot>> callback) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        SearchSpotRepository repository;
        if (networkInfo != null && networkInfo.isConnected()) {
            repository = new SearchSpotOnlineRepository(context, authenticationService);
        } else {
            repository = new SearchSpotOfflineRepository(context, sessionRepository.getSession().userId);
        }
        repository.search(term, position, bounds, callback);
    }
}
