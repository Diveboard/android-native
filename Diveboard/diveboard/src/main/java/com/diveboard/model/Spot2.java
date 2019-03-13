package com.diveboard.model;

import com.google.gson.annotations.SerializedName;

public class Spot2 {
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
}
