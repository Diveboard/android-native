package com.diveboard.dataaccess.datamodel;

import androidx.annotation.NonNull;

import com.diveboard.util.Exclude;
import com.diveboard.util.Utils;
import com.google.gson.annotations.SerializedName;

//exclude all the field from serialization but id so e.g. on dive save (afterg duplicate) they will not be sent to server
public class Spot {
    @SerializedName("id")
    public Integer id;
    @Exclude
    @SerializedName("country_name")
    public String countryName;
    @Exclude
    @SerializedName("country_code")
    public String countryCode;
    @Exclude
    @SerializedName("country_flag_big")
    public String countryFlagBig;
    @Exclude
    @SerializedName("country_flag_small")
    public String countryFlagSmall;
    @Exclude
    @SerializedName("within_country_bounds")
    public Boolean withinCountryBounds;
    @Exclude
    @SerializedName("region_name")
    public String regionName;
    @Exclude
    @SerializedName("location_name")
    public String locationName;
    @Exclude
    @SerializedName("permalink")
    public String permalink;
    @Exclude
    @SerializedName("fullpermalink")
    public String fullpermalink;
    @Exclude
    @SerializedName("staticmap")
    public String staticmap;
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
}