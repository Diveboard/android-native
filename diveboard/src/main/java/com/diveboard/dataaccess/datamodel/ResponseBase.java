package com.diveboard.dataaccess.datamodel;

import com.google.gson.annotations.SerializedName;

public abstract class ResponseBase<T> {
    @SerializedName("success")
    public Boolean success;
    @SerializedName("error")
    public ErrorItemResponse[] errors;
    @SerializedName("result")
    public T result;
}
