package com.diveboard.dataaccess;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.diveboard.dataaccess.datamodel.SyncObject;

@Database(entities = {SyncObject.class}, version = 1)
public abstract class SyncObjectDatabase extends RoomDatabase {
    public abstract SyncObjectDao userDao();
}