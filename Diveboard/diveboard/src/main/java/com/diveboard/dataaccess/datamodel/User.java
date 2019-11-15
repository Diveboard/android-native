package com.diveboard.dataaccess.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User {
    public Integer id;
    public String nickname;
    @SerializedName("picture")
    public String pictureUrl;
    @SerializedName("all_dive_ids")
    public List<Integer> dives;

    public User() {
        dives = new ArrayList<>();
    }

    public String getSanitizedPictureUrl() {
        return pictureUrl != null && pictureUrl.startsWith("//") ? "https:" + pictureUrl : pictureUrl;
    }
}