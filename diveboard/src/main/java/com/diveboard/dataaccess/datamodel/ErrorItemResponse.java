package com.diveboard.dataaccess.datamodel;

import com.google.gson.annotations.SerializedName;

public class ErrorItemResponse {
    @SerializedName("error_code")
    public String errorCode;
    @SerializedName("message")
    public String message;
}
