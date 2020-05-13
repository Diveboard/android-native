package com.diveboard.dataaccess.datamodel;

import androidx.annotation.NonNull;

import com.diveboard.util.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User {
    public Integer id;

    @Exclude
    public String nickname;

    @SerializedName("wallet_pictures")
    @Exclude
    public List<Picture> walletPictures;

    @SerializedName("wallet_picture_ids")
    public List<Integer> walletPicturesIds = new ArrayList<>();

    @SerializedName("picture")
    @Exclude
    public String pictureUrl;

    @SerializedName("all_dive_ids")
    @Exclude
    public List<Integer> dives;

    public User() {
        dives = new ArrayList<>();
    }

    public String getSanitizedPictureUrl() {
        return pictureUrl != null && pictureUrl.startsWith("//") ? "https:" + pictureUrl : pictureUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return nickname;
    }
}