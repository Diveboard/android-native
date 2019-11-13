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

    public enum Action {
        New("new"),
        Update("update"),
        Delete("delete");
        private final String text;

        Action(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
