package com.diveboard.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class UserPreferenceService {
    private Context _context;

    public UserPreferenceService(final Context context) {
        _context = context;
    }

    public Boolean isRestricDataUsage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        return sharedPreferences.getBoolean("data_usage", true);
    }

    public PictureSize getPictureQuality() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        String value = sharedPreferences.getString("picquality", null);
        if ("High Definition".equals(value)) {
            return PictureSize.LARGE;
        } else {
            return PictureSize.MEDIUM;
        }
    }

    public Units.UnitsType getUnits() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        return Units.UnitsType.valueOf(sharedPreferences.getString("units", Units.UnitsType.Metric.toString()));
    }
}
