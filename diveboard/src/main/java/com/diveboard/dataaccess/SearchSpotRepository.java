package com.diveboard.dataaccess;

import com.diveboard.dataaccess.datamodel.Spot;
import com.diveboard.util.ResponseCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public interface SearchSpotRepository {
    void search(String term, LatLng position, LatLngBounds bounds, ResponseCallback<List<Spot>> callback);
}
