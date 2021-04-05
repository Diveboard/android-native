package com.diveboard.dataaccess.datamodel;

import com.diveboard.model.SearchSpot;

import java.util.ArrayList;
import java.util.List;

public class SpotsSearchResponse {
    public Boolean success;
    public List<SearchSpot> data;

    public List<Spot> getSpots() {
        if (data == null) {
            return new ArrayList<>();
        }
        ArrayList<Spot> result = new ArrayList<>();
        for (SearchSpot ss : data) {
            result.add(ss.data);
        }
        return result;
    }
}
