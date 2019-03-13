package com.diveboard.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.diveboard.util.Callback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SpotService {
    private ConnectivityManager connectivityManager;
    private Context context;

    public SpotService(Context context) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = context;
    }

    public List<Spot> searchSpot(String term, LatLng position, LatLng positionSW, LatLng positionNE, String token, int userId, Callback<List<Spot2>> callback, Callback<String> errorCallback) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        SpotRepository repository;
        if (networkInfo != null && networkInfo.isConnected()) {
            repository = new SpotOnlineRepository(context);
        } else {
            repository = new SpotOfflineRepository(context);
        }
        repository.search(term, position, positionSW, positionNE, token, userId, callback, errorCallback);
        return null;
    }
}
