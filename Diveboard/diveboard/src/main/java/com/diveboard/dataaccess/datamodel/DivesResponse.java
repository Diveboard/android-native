package com.diveboard.dataaccess.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DivesResponse {
    @SerializedName("result")
    public List<Dive> dives = new ArrayList<>();

    public List<String> getTripNames() {
        ArrayList<String> result = new ArrayList<>();
        for (Dive dive : dives) {
            if (dive.tripName == null) {
                continue;
            }
            boolean found = false;
            for (String tripName : result) {
                if (tripName != null && tripName.equals(dive.tripName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(dive.tripName);
            }
        }
        return result;
    }

    public Dive getDive(int diveId) {
        for (Dive dive : this.dives) {
            if (dive.id == diveId) {
                return dive;
            }
        }
        return null;
    }

    public int getMaxDiveNumber() {
        int result = 1;
        for (Dive dive : this.dives) {
            if (dive.diveNumber > result) {
                result = dive.diveNumber;
            }
        }
        return result;
    }

    public String getLastTripName() {
        //TODO: implement. take into consideration not yet synced dives
        return null;
    }
}
