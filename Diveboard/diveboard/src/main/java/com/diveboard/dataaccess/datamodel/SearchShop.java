package com.diveboard.dataaccess.datamodel;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class SearchShop {
    @SerializedName("name")
    public String name;
    @SerializedName("id")
    public Integer id;
    @SerializedName("label")
    public String label;
    @SerializedName("value")
    public String value;
    @SerializedName("web")
    public String web;
    @SerializedName("home")
    public String home;
    @SerializedName("relative")
    public String relative;
    @SerializedName("country")
    public String country;
    @SerializedName("country_code")
    public String countryCode;
    @SerializedName("city")
    public String city;
    @SerializedName("picture")
    public String picture;
    @SerializedName("claimed")
    public Boolean claimed;
    @SerializedName("registrable")
    public Boolean registrable;
    @SerializedName("private")
    public Boolean _private;
    @SerializedName("has_review")
    public Boolean hasReview;

    public Shop getShop() {
        Shop result = new Shop();
        result.id = id;
        result.name = this.name;
        result.web = this.web;
        result.web = this.home;
        result.logo_url = this.picture;
        return result;
    }

    public String getLocation() {
        String result = "";
        if (city != null && !city.trim().isEmpty()) {
            result += city;
        }
        if (country != null && !country.trim().isEmpty()) {
            if (result.length() != 0) {
                result += ", " + country;
            } else {
                result += country;
            }
        }
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
