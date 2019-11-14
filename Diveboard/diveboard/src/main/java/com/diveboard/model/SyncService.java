package com.diveboard.model;

import com.diveboard.dataaccess.DiveboardApiException;
import com.diveboard.dataaccess.DivesOnlineRepository;
import com.diveboard.dataaccess.SyncObjectDao;
import com.diveboard.dataaccess.datamodel.DeleteResponse;
import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DiveResponse;
import com.diveboard.dataaccess.datamodel.ResponseBase;
import com.diveboard.dataaccess.datamodel.SyncObject;
import com.diveboard.util.ResponseCallback;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class SyncService {
    private static final int MAX_SYNC_ATTEMPTS = 3;
    private final Gson gson;
    private DivesOnlineRepository onlineRepository;
    private SyncObjectDao syncObjectDao;

    public SyncService(SyncObjectDao syncObjectDao, DivesOnlineRepository onlineRepository) {
        this.syncObjectDao = syncObjectDao;
        this.onlineRepository = onlineRepository;
        gson = new Gson();
    }

    public Exception syncChanges() {
        List<SyncObject> changes = syncObjectDao.getAll();
        Semaphore semaphore = new Semaphore(0);
        final Exception[] error = {null};
        //submit changes sequentially
        for (SyncObject syncObject : changes) {
            Dive dive = getDive(syncObject.object);
            switch (syncObject.action) {
                case New:
                case Update:
                    onlineRepository.saveDive(dive, (ResponseCallback<DiveResponse, Exception>) getCallback(semaphore, error, syncObject, dive));
                    break;
                case Delete:
                    onlineRepository.deleteDive(dive, (ResponseCallback<DeleteResponse, Exception>) getCallback(semaphore, error, syncObject, dive));
            }
            try {
                if (!semaphore.tryAcquire(30, TimeUnit.SECONDS)) {
                    return new Exception("Timeout during synchronizing offline changes of dive #" + dive.diveNumber);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return error[0];
    }

    private ResponseCallback<?, Exception> getCallback(Semaphore semaphore, Exception[] error, SyncObject syncObject, Dive dive) {
        return new ResponseCallback<ResponseBase<?>, Exception>() {
            @Override
            public void success(ResponseBase<?> data) {
                markSynced(syncObject.id);
                semaphore.release();
            }

            @Override
            public void error(Exception e) {
                //synchronize as most as possible, do not interrupt the syncronization process but remember error
                if (e instanceof DiveboardApiException) {
                    error[0] = new Exception("Cannot sync dive #" + dive.diveNumber + "\n" + e.getMessage());
                    //increase attempt count and give another chance to sync in future
                    if (syncObject.syncAttemptsCount < MAX_SYNC_ATTEMPTS) {
                        syncObject.syncAttemptsCount++;
                        syncObjectDao.update(syncObject);
                    } else {
                        syncObjectDao.delete(syncObject);
                    }
                } else {
                    //these errors happen because of some network or other errors, not business so do not not increase attempts
                    error[0] = new Exception("Cannot sync dive #" + dive.diveNumber + "\n" + e.getMessage());
                }
                semaphore.release();
            }
        };
    }

    public void newDive(Dive dive) {
        addAction(dive, SyncObject.Action.New);
    }

    private String getString(Dive dive) {
        return gson.toJson(dive);
    }

    private Dive getDive(String string) {
        return gson.fromJson(string, Dive.class);
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
//        make all these calls with rxjava usage? and test dive update in offline
        syncObjectDao.insert(syncObject);
    }

    public void markSynced(String shakenId) {
        SyncObject so = new SyncObject();
        so.id = shakenId;
        syncObjectDao.delete(so);
    }
}
