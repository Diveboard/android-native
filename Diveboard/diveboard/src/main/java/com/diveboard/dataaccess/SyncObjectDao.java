package com.diveboard.dataaccess;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.diveboard.dataaccess.datamodel.SyncObject;

import java.util.List;

@Dao
public interface SyncObjectDao {

    @Query("SELECT * FROM sync_object")
    List<SyncObject> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SyncObject... syncObjects);

    @Delete
    void delete(SyncObject syncObject);

    @Update
    void update(SyncObject syncObject);
}
