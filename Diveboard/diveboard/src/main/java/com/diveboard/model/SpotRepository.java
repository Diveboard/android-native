package com.diveboard.model;

import com.diveboard.util.Callback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public interface SpotRepository {
    void search(String term, LatLng position, LatLngBounds bounds, Callback<List<Spot2>> callback, Callback<String> errorCallback);
}
