package com.diveboard.dataaccess.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DivesResponse {
    @SerializedName("result")
    public List<Dive2> dives;

    public DivesResponse() {
        dives = new ArrayList<>();
    }
}
