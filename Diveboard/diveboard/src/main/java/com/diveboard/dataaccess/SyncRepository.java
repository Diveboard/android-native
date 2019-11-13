package com.diveboard.dataaccess;

import androidx.room.Room;

import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.SyncObject;
import com.diveboard.mobile.ApplicationController;
import com.google.gson.Gson;

import java.util.Date;


public class SyncRepository {
    private final ApplicationController ac;
    private final SyncObjectDatabase db;

    public SyncRepository() {
        ac = ApplicationController.getInstance();
        db = Room.databaseBuilder(ac, SyncObjectDatabase.class, "sync-objects").build();
    }

    public void newDive(Dive dive) {
        addAction(dive, SyncObject.Action.New);
    }

    private String getString(Dive dive) {
        Gson gson = new Gson();
        return gson.toJson(dive);
    }

    public void updateDive(Dive dive) {
        addAction(dive, SyncObject.Action.Update);
    }

    public void deleteDive(Dive dive) {
        addAction(dive, SyncObject.Action.Delete);
    }

    private void addAction(Dive dive, SyncObject.Action action) {
        SyncObject syncObject = new SyncObject();
        syncObject.id = dive.shakenId;
        syncObject.action = action;
        syncObject.actionDate = new Date();
        syncObject.object = getString(dive);
        db.userDao().insert(syncObject);
    }

    public void markSynced(String shakenId) {
        SyncObject so = new SyncObject();
        so.id = shakenId;
        db.userDao().delete(so);
    }
}
