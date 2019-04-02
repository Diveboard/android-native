package com.diveboard.dataaccess.datamodel;

import com.google.gson.annotations.SerializedName;

public class Spot {
    @SerializedName("id")
    public Integer id;
    @SerializedName("country_name")
    public String countryName;
    @SerializedName("country_code")
    public String countryCode;
    @SerializedName("country_flag_big")
    public String countryFlagBig;
    @SerializedName("country_flag_small")
    public String countryFlagSmall;
    @SerializedName("within_country_bounds")
    public Boolean withinCountryBounds;
    @SerializedName("region_name")
    public String regionName;
    @SerializedName("location_name")
    public String locationName;
    @SerializedName("permalink")
    public String permalink;
    @SerializedName("fullpermalink")
    public String fullpermalink;
    @SerializedName("staticmap")
    public String staticmap;
    @SerializedName("name")
    public String name;
    @SerializedName("lat")
    public Double lat;
    @SerializedName("lng")
    public Double lng;
}