package com.diveboard.dataaccess.datamodel;

import com.google.gson.annotations.SerializedName;

public class Shop {
    @SerializedName("id")
    public Integer id;
    @SerializedName("name")
    public String name;
    @SerializedName("lat")
    public Double lat;
    @SerializedName("lng")
    public Double lng;
    @SerializedName("address")
    public String address;
    @SerializedName("email")
    public String email;
    @SerializedName("web")
    public String web;
    @SerializedName("phone")
    public String phone;
    @SerializedName("logo_url")
    public String logo_url;
    @SerializedName("dive_count")
    public Integer dive_count;
//    @SerializedName("dive_ids")
//    public  dive_ids;
}
