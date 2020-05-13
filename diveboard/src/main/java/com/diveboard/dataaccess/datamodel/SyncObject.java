package com.diveboard.dataaccess.datamodel;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.diveboard.util.RoomConverters;

import java.util.Date;

@Entity(tableName = "sync_object")
@TypeConverters({RoomConverters.class})
public class SyncObject {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "change_date")
    public Date actionDate;

    @ColumnInfo(name = "action")
    public Action action;

    @ColumnInfo(name = "object")
    public String object;

    @ColumnInfo(name = "sync_attempts_count", defaultValue = "0")
    public int syncAttemptsCount;

    public enum Action {
        New,
        Update,
        Delete
    }
}
