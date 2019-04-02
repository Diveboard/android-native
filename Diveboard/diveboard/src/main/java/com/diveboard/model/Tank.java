package com.diveboard.model;

import com.google.gson.annotations.SerializedName;

public class Tank {
    @SerializedName("dive_id")
    public Integer diveId;
    @SerializedName("gas")
    //TODO: it will fail at runtime because EAN values are not supported: string in 'air','EANx32','EANx36','EANx40','custom'
    public String gas;
    @SerializedName("gas_type")
    public String gasType;
    @SerializedName("he")
    public Integer he;
    @SerializedName("id")
    public Integer id;
    @SerializedName("material")
    public String material;
    @SerializedName("multitank")
    public Integer cylindersCount;
    @SerializedName("n2")
    public Integer n2;
    @SerializedName("o2")
    public Integer o2;
    @SerializedName("order")
    public Integer order;
    @SerializedName("p_end")
    public Double endPressure;
    @SerializedName("p_start")
    public Double startPressure;
    @SerializedName("time_start")
    public Integer timeStart;
    @SerializedName("volume")
    public Double volume;
}