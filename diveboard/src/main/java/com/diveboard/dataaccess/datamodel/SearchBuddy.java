package com.diveboard.dataaccess.datamodel;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class SearchBuddy {
    @SerializedName("name")
    public String name;
    @SerializedName("id")
    public Integer id;
    @SerializedName("shaken_id")
    public String shakenId;
    @SerializedName("label")
    public String label;
    @SerializedName("value")
    public String value;
    @SerializedName("web")
    public String web;
    @SerializedName("relative")
    public String relative;
    @SerializedName("picture")
    public String pictureUrl;
    @SerializedName("db_id")
    public Integer diveboardId;

    public String getCleanPictureUrl() {
        if (pictureUrl != null && pictureUrl.startsWith("//")) {
            return "https:" + pictureUrl;
        }
        return pictureUrl;
    }

    public Buddy getBuddy() {
        Buddy result = new Buddy();
        result.setId(diveboardId);
        result.setName(name);
        result.setPictureUrl(pictureUrl);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
