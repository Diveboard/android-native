package com.diveboard.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

public class SearchSpot {
    public Integer id;
    public String name;
    public Double lat;
    @SerializedName("long")
    public Double lng;
    public String location;
    public String region;
    public String cname;
    @SerializedName("dive_count")
    public Integer diveCount;

    public LatLng getLatLng() {
        if (lat == null || lng == null) {
            return null;
        }
        return new LatLng(lat, lng);
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
