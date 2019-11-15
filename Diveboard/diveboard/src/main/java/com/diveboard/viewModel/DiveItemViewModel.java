package com.diveboard.viewModel;

import android.text.TextUtils;

import com.diveboard.dataaccess.datamodel.Spot;

public class DiveItemViewModel implements Comparable {
    public final int index;
    public final int number;
    public final Integer id;
    public final String shakenId;
    public final String date;
    public final String location;
    public final String siteName;
    public final String durationStr;
    public final String maxDepthStr;

    public DiveItemViewModel(int index, Integer id, String shakenId, int number, String date, Spot spot, Integer minutes, Double maxDepth, String depthUnit) {
        this.index = index;
        this.shakenId = shakenId;
        this.number = number;
        this.id = id;
        this.date = date;
        if (spot == null) {
            location = "N/A";
            siteName = "N/A";
        } else {
            location = getString(spot.countryName);
            siteName = getString(spot.name);
        }
        this.durationStr = minutes == null ? "" : Integer.toString(minutes) + " min";
        this.maxDepthStr = maxDepth == null ? "" : Double.toString(maxDepth) + " " + depthUnit;
    }

    private String getString(String str) {
        return TextUtils.isEmpty(str) ? "N/A" : str;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof DiveItemViewModel) {
            return ((DiveItemViewModel) o).number - this.number;
        }
        return -1;
    }
}
