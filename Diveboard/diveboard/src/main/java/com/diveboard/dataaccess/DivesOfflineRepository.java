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

    public void saveDive(Dive dive, ResponseCallback<Dive> callback, boolean toBeSynced, String shakenId, boolean newDive) {
        getAsync(new ResponseCallback<DivesResponse>() {
            @Override
            public void success(DivesResponse data) {
                if (newDive) {
                    //TODO: it is not always a new dive, it could be a dive which was created offline and now edited
                    //new dive
                    data.result.add(dive);
                    if (toBeSynced) {
                        syncService.newDive(dive);
                    } else {
                        syncService.markSynced(shakenId);
                    }
                } else {
                    //update dive
                    for (int i = 0; i < data.result.size(); i++) {
                        if (data.result.get(i).shakenId.equals(dive.shakenId)) {
                            data.result.set(i, dive);
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

    public void deleteDive(Dive dive, ResponseCallback<DeleteResponse> callback, boolean toBeSynced) {
        getAsync(new ResponseCallback<DivesResponse>() {
            @Override
            public void success(DivesResponse data) {
                for (int i = 0; i < data.result.size(); i++) {
                    if (data.result.get(i).shakenId.equals(dive.shakenId)) {
                        data.result.remove(i);
                        break;
                    }
                }
                if (!dive.existsOnline()) {
                    //this dive is not yet synced so remove from synchronization log
                    syncService.markSynced(dive.shakenId);
                }
                if (dive.existsOnline() && toBeSynced) {
                    syncService.deleteDive(dive);
                }
                save(data);
                callback.success(new DeleteResponse());
            }

            @Override
            public void error(Exception e) {
                callback.error(e);
            }
        });
    }
}
