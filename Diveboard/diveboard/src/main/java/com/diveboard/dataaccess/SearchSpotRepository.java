package com.diveboard.dataaccess;

import com.diveboard.model.SearchSpot;
import com.diveboard.util.Callback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public interface SearchSpotRepository {
    void search(String term, LatLng position, LatLngBounds bounds, Callback<List<SearchSpot>> callback, Callback<String> errorCallback);
}
