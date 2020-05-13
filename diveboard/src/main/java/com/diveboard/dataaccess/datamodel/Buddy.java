package com.diveboard.dataaccess.datamodel;

import com.google.gson.annotations.SerializedName;

public class Buddy extends User {
    //set of fields to save buddy or to link buddy
    @SerializedName("name")
    public String writeName;
    @SerializedName("db_id")
    public Integer writeId;
    @SerializedName("fb_id")
    public Integer writeFacebookId;
    @SerializedName("email")
    public String writeEmail;
    @SerializedName("picturl")
    public String writePictureUrl;

    public void setId(Integer id) {
        this.id = id;
        writeId = id;
    }

    public void setName(String name) {
        this.nickname = name;
//        this.writeName = name;
    }

    public void setPictureUrl(String url) {
        this.pictureUrl = url;
//        this.writePictureUrl = url;
    }
}
