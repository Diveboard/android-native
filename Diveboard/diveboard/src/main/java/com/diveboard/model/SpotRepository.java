package com.diveboard.model;

import com.diveboard.util.Callback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface SpotRepository {
    void search(String term, LatLng position, LatLng positionSW, LatLng positionNE, String token, int userId, Callback<List<Spot2>> callback, Callback<String> errorCallback);
}
