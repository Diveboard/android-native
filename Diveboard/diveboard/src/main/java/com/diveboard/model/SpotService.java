package com.diveboard.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.diveboard.dataaccess.SearchSpotOfflineRepository;
import com.diveboard.dataaccess.SearchSpotOnlineRepository;
import com.diveboard.dataaccess.SearchSpotRepository;
import com.diveboard.dataaccess.SessionRepository;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.util.Callback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class SpotService {
    private ConnectivityManager connectivityManager;
    private ApplicationController context;
    private SessionRepository sessionRepository;

    public SpotService(ApplicationController context, SessionRepository sessionRepository) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = context;
        this.sessionRepository = sessionRepository;
    }

    public void searchSpot(String term, LatLng position, LatLngBounds bounds, Callback<List<SearchSpot>> callback, Callback<String> errorCallback) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        SearchSpotRepository repository;
        if (networkInfo != null && networkInfo.isConnected()) {
            repository = new SearchSpotOnlineRepository(context, sessionRepository.getSession().token);
        } else {
            repository = new SearchSpotOfflineRepository(context, sessionRepository.getSession().userId);
        }
        repository.search(term, position, bounds, callback, errorCallback);
    }
}
