package com.diveboard.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

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

    public Units getUnitsTyped() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        Units.UnitsType units = Units.UnitsType.valueOf(sharedPreferences.getString("unit", Units.UnitsType.Metric.toString()));
        return new Units(units);
    }
}
