package com.diveboard.viewModel;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.DiveDetailsActivity;
import com.diveboard.mobile.DivesActivity;
import com.diveboard.model.Spot;

public class DiveItemViewModel implements Comparable {
    public final int index;
    public final int number;
    public final String date;
    public final String location;
    public final String siteName;
    public final String durationStr;
    public final String maxDepthStr;

    public DiveItemViewModel(int index, int number, String date, Spot spot, Integer minutes, Double maxDepth, String depthUnit) {
        this.index = index;
        this.number = number;
        this.date = date;
        if (spot == null) {
            location = "N/A";
            siteName = "N/A";
        } else {
            location = getString(spot.getCountryName());
            siteName = getString(spot.getName());
        }
        this.durationStr = minutes == null ? "" : Integer.toString(minutes) + " min";
        this.maxDepthStr = maxDepth == null ? "" : Double.toString(maxDepth) + " " + depthUnit;
    }

    public void onClick(View view) {
        Context context = view.getContext();
        Intent diveDetailsActivity = new Intent(context, DiveDetailsActivity.class);
        diveDetailsActivity.putExtra("index", index);
        context.startActivity(diveDetailsActivity);
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
