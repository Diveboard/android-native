package com.diveboard.dataaccess;

import android.content.Context;

import com.diveboard.dataaccess.datamodel.DeleteResponse;
import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.model.SyncService;
import com.diveboard.util.ResponseCallback;

public class DivesOfflineRepository extends FileRepository<DivesResponse> {

    private SyncService syncService;

    public DivesOfflineRepository(Context context, SyncService syncService) {
        super(context);
        this.syncService = syncService;
    }

    public void saveDive(Dive dive, ResponseCallback<Dive, Exception> callback, boolean toBeSynced, String shakenId) {
        getAsync(new ResponseCallback<DivesResponse, Exception>() {
            @Override
            public void success(DivesResponse data) {
                if (dive.id == null) {
                    //TODO: it is not always a new dive, it could be a dive which was created offline and now edited
                    //new dive
                    data.dives.add(dive);
                    if (toBeSynced) {
                        syncService.newDive(dive);
                    } else {
                        syncService.markSynced(shakenId);
                    }
                } else {
                    //update dive
                    for (int i = 0; i < data.dives.size(); i++) {
                        if (data.dives.get(i).id.equals(dive.id)) {
                            data.dives.set(i, dive);
                            break;
                        }
                    }
                    if (toBeSynced) {
                        syncService.updateDive(dive);
                    } else {
                        syncService.markSynced(shakenId);
                    }
                }
                save(data);
                callback.success(dive);
            }

            @Override
            public void error(Exception e) {
                callback.error(e);
            }
        });
    }

    @Override
    protected String getFileName() {
        return "dives.json";
    }

    @Override
    protected Class getClazz() {
        return DivesResponse.class;
    }

    public void deleteDive(Dive dive, ResponseCallback<DeleteResponse, Exception> callback, boolean toBeSynced) {
        getAsync(new ResponseCallback<DivesResponse, Exception>() {
            @Override
            public void success(DivesResponse data) {
                for (int i = 0; i < data.dives.size(); i++) {
                    if (data.dives.get(i).shakenId.equals(dive.shakenId)) {
                        data.dives.remove(i);
                        break;
                    }
                }
                if (dive.id == null) {
                    //this dive is not yet synced
                    syncService.markSynced(dive.shakenId);
                }
                if (dive.id != null && toBeSynced) {
                    syncService.deleteDive(dive);
                }
                save(data);
            }

            @Override
            public void error(Exception e) {
                callback.error(e);
            }
        });
    }
}
