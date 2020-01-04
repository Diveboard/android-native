package com.diveboard.dataaccess.datamodel;

import androidx.annotation.NonNull;

import com.diveboard.util.Exclude;
import com.diveboard.util.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Spot {
    @SerializedName("id")
    public Integer id;
    @Exclude
    @SerializedName("country_name")
    public String countryName;
    @Exclude
    @SerializedName("country_code")
    public String countryCode;
    /*@SerializedName("country_flag_big")
    public String countryFlagBig;
    @SerializedName("country_flag_small")
    public String countryFlagSmall;
    @SerializedName("within_country_bounds")
    public Boolean withinCountryBounds;*/
    @Exclude
    @SerializedName("region_name")
    public String regionName;
    @Exclude
    @SerializedName("location_name")
    public String locationName;
    /*@SerializedName("permalink")
    public String permalink;
    @SerializedName("fullpermalink")
    public String fullpermalink;
    @SerializedName("staticmap")
    public String staticmap;*/
    @Exclude
    @SerializedName("name")
    public String name;
    @Exclude
    @SerializedName("lat")
    public Double lat;
    @Exclude
    @SerializedName("lng")
    public Double lng;

    @NonNull
    @Override
    public String toString() {
        if (Utils.isNullOrEmpty(countryName)) {
            return name;
        }
        return name + ", " + countryName;
    }

    public LatLng getLatLng() {
        if (lat == null || lng == null) {
            return null;
        }
        return new LatLng(lat, lng);
    }
}