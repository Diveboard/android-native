package com.diveboard.util;

import androidx.room.TypeConverter;

import com.diveboard.dataaccess.datamodel.SyncObject;

import java.util.Date;

public class RoomConverters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String actionToString(SyncObject.Action action) {
        return action == null ? null : action.toString();
    }

    @TypeConverter
    public static SyncObject.Action stringToAction(String value) {
        return value == null ? null : SyncObject.Action.valueOf(value);
    }
}
