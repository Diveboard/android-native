package com.diveboard.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.diveboard.util.RoomConverters;

import java.util.Date;

public class UserPreferenceService {
    private SharedPreferences sharedPreferences;

    public UserPreferenceService(final Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Boolean isRestricDataUsage() {
        return sharedPreferences.getBoolean("data_usage", true);
    }

    public PictureSize getPictureQuality() {
        String value = sharedPreferences.getString("picquality", null);
        if ("High Definition".equals(value)) {
            return PictureSize.LARGE;
        } else {
            return PictureSize.MEDIUM;
        }
    }

    public Units.UnitsType getUnits() {
        return Units.UnitsType.valueOf(sharedPreferences.getString("units", Units.UnitsType.Metric.toString()));
    }

    public Date getLastSyncTime() {
        long lastSyncTime = sharedPreferences.getLong("lastSyncTime", -1);
        return lastSyncTime == -1 ? null : RoomConverters.fromTimestamp(lastSyncTime);
    }

    public void setLastSyncTime(Date date) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong("lastSyncTime", RoomConverters.dateToTimestamp(date));
        edit.apply();
    }
}
