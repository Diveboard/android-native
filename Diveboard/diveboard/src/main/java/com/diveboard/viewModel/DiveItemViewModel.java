package com.diveboard.viewModel;

import android.text.TextUtils;
import android.view.View;

import com.diveboard.dataaccess.datamodel.Spot2;

public class DiveItemViewModel implements Comparable {
    public final int index;
    public final int number;
    public final String date;
    public final String location;
    public final String siteName;
    public final String durationStr;
    public final String maxDepthStr;

    public DiveItemViewModel(int index, int number, String date, Spot2 spot, Integer minutes, Double maxDepth, String depthUnit) {
        this.index = index;
        this.number = number;
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

    public void onClick(View view) {

    }

    private String getString(String str) {
        return TextUtils.isEmpty(str) ? "N/A" : str;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !(o instanceof DiveItemViewModel)) {
            return -1;
        }
        return ((DiveItemViewModel) o).number - this.number;
    }
}
