package com.diveboard.dataaccess.datamodel;

import com.google.gson.annotations.SerializedName;

public class Picture {
    @SerializedName("flavour")
    public String flavour;
    @SerializedName("thumbnail")
    public String thumbnail;
    @SerializedName("medium")
    public String medium;
    @SerializedName("large")
    public String large;
    @SerializedName("small")
    public String small;
    @SerializedName("notes")
    public String notes;
    @SerializedName("media")
    public String media;
    //        @SerializedName("player")
    //        public Object player;
    @SerializedName("full_redirect_link")
    public String fullRedirectLink;
    @SerializedName("fullpermalink")
    public String fullpermalink;
    @SerializedName("permalink")
    public String permalink;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("id")
    public Integer id;
}
