package com.diveboard.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class UserPreference {
    private Context _context;

    public UserPreference(final Context context) {
        _context = context;
    }

    public Boolean isRestricDataUsage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        return sharedPreferences.getBoolean("data_usage", true);
    }

    public Picture.Size getPictureQuality() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        String value = sharedPreferences.getString("picquality", null);
        if ("High Definition".equals(value)) {
            return Picture.Size.LARGE;
        } else {
            return Picture.Size.MEDIUM;
        }
    }

    public Units.UnitsType getUnitsTyped() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        return Units.UnitsType.valueOf(sharedPreferences.getString("units", Units.UnitsType.Metric.toString()));
    }
}
