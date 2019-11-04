package com.diveboard.dataaccess.datamodel;

import com.google.gson.annotations.SerializedName;

public class DiveResponse {
    @SerializedName("success")
    public Boolean success;
    @SerializedName("error")
    public ErrorResponse error;
    @SerializedName("result")
    public Dive result;
}
