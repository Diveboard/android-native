package com.diveboard.model;

import android.content.Context;

import com.diveboard.dataaccess.DivesOfflineRepository;
import com.diveboard.dataaccess.DivesOnlineRepository;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.mobile.R;
import com.diveboard.util.NetworkUtils;
import com.diveboard.util.ResponseCallback;

public class DivesService {

    private Context context;
    private DivesOfflineRepository offlineRepository;
    private DivesOnlineRepository onlineRepository;

    public DivesService(Context context, DivesOfflineRepository offlineRepository, DivesOnlineRepository onlineRepository) {
        this.context = context;
        this.offlineRepository = offlineRepository;
        this.onlineRepository = onlineRepository;
    }

    public void getDivesAsync(final ResponseCallback<DivesResponse, String> callback) {
        final ResponseCallback<DivesResponse, Exception> intCallback = new ResponseCallback<DivesResponse, Exception>() {
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

    private void onlineCall(ResponseCallback<DivesResponse, String> callback) {
        if (NetworkUtils.isConnected(context)) {
            onlineRepository.load(new ResponseCallback<DivesResponse, String>() {
                @Override
                public void success(DivesResponse data) {
                    offlineRepository.save(data);
                    callback.success(data);
                }

                @Override
                public void error(String s) {
                    callback.error(s);
                }
            });
        } else {
            callback.error(context.getString(R.string.no_internet));
        }
    }
}
