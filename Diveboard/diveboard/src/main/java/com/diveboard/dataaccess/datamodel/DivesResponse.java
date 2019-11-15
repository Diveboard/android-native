package com.diveboard.dataaccess.datamodel;

import java.util.ArrayList;
import java.util.List;

public class DivesResponse extends ResponseBase<List<Dive>> {
    public DivesResponse() {
        result = new ArrayList<>();
    }

    public List<String> getTripNames() {
        ArrayList<String> result = new ArrayList<>();
        for (Dive dive : this.result) {
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

    public Dive getDive(String shakenId) {
        for (Dive dive : this.result) {
            if (dive.shakenId.equals(shakenId)) {
                return dive;
            }
        }
        return null;
    }

    public int getMaxDiveNumber() {
        int result = 1;
        for (Dive dive : this.result) {
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
