package com.diveboard.model;

import android.content.Context;
import android.os.AsyncTask;

import com.diveboard.dataaccess.DivesOfflineRepository;
import com.diveboard.dataaccess.DivesOnlineRepository;
import com.diveboard.dataaccess.datamodel.DeleteResponse;
import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DiveResponse;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.mobile.R;
import com.diveboard.util.NetworkUtils;
import com.diveboard.util.ResponseCallback;

import java.util.Date;

public class DivesService {

    private Context context;
    private DivesOfflineRepository offlineRepository;
    private DivesOnlineRepository onlineRepository;
    private UserPreferenceService userPreferenceService;
    private SyncService syncService;

    public DivesService(Context context,
                        DivesOfflineRepository offlineRepository,
                        DivesOnlineRepository onlineRepository,
                        UserPreferenceService userPreferenceService,
                        SyncService syncService) {
        this.context = context;
        this.offlineRepository = offlineRepository;
        this.onlineRepository = onlineRepository;
        this.userPreferenceService = userPreferenceService;
        this.syncService = syncService;
    }

    public void getDivesAsync(final ResponseCallback<DivesResponse> callback, boolean forceOnline) {
        if (forceOnline) {
            onlineCall(callback);
            return;
        }
        final ResponseCallback<DivesResponse> intCallback = new ResponseCallback<DivesResponse>() {
            @Override
            public void success(DivesResponse data) {
                if (data != null) {
                    callback.success(data);
                } else {
                    onlineCall(callback);
                }
            }

            @Override
            public void error(Exception s) {
                onlineCall(callback);
            }
        };

        offlineRepository.getAsync(intCallback);
    }

    private void onlineCall(ResponseCallback<DivesResponse> callback) {
        if (NetworkUtils.isConnected(context)) {
            SyncChangesAndGetFreshResultsTask syncChangesTask = new SyncChangesAndGetFreshResultsTask(callback);
            syncChangesTask.execute((Void) null);
        } else {
            callback.error(new Exception(context.getString(R.string.no_internet)));
        }
    }

    public void saveDiveAsync(Dive dive, ResponseCallback<Dive> callback, boolean isNewDive) {
        if (NetworkUtils.isConnected(context)) {
            onlineRepository.saveDive(dive, new ResponseCallback<DiveResponse>() {
                @Override
                public void success(DiveResponse data) {
                    //it is important to pass old shaken id, so sync repo can find it properly
                    offlineRepository.saveDive(data.result, new ResponseCallback.Empty<>(), false, dive.shakenId, isNewDive);
                    callback.success(data.result);
                }

                @Override
                public void error(Exception s) {
                    callback.error(s);
                }
            });
        } else {
            offlineRepository.saveDive(dive, callback, true, dive.shakenId, isNewDive);
        }
    }

    public void deleteDiveAsync(Dive dive, ResponseCallback<DeleteResponse> callback) {
        //dive might be created and then deleted offline so it doesn't have id
        if (NetworkUtils.isConnected(context) && dive.existsOnline()) {
            onlineRepository.deleteDive(dive, new ResponseCallback<DeleteResponse>() {
                @Override
                public void success(DeleteResponse data) {
                    offlineRepository.deleteDive(dive, new ResponseCallback.Empty<>(), false);
                    callback.success(data);
                }

                @Override
                public void error(Exception s) {
                    callback.error(s);
                }
            });
        } else {
            offlineRepository.deleteDive(dive, callback, true);
        }
    }

    private class SyncChangesAndGetFreshResultsTask extends AsyncTask<Void, Void, Exception> {
        private ResponseCallback<DivesResponse> callback;

        public SyncChangesAndGetFreshResultsTask(ResponseCallback<DivesResponse> callback) {
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e != null) {
                callback.error(e);
                //get fresh results from server even if syncronization failed
            }

            onlineRepository.load(new ResponseCallback<DivesResponse>() {
                @Override
                public void success(DivesResponse data) {
                    offlineRepository.save(data);
                    userPreferenceService.setLastSyncTime(new Date());
                    callback.success(data);
                }

                @Override
                public void error(Exception s) {
                    callback.error(s);
                }
            });
        }

        @Override
        protected Exception doInBackground(Void... voids) {
            return syncService.syncChanges();
        }
    }
}
