package com.diveboard.model;

import android.content.Context;

import com.diveboard.dataaccess.UserOfflineRepository;
import com.diveboard.dataaccess.UserOnlineRepository;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.util.NetworkUtils;
import com.diveboard.util.ResponseCallback;

public class UserService {
    private Context context;
    private UserOfflineRepository offlineRepository;
    private UserOnlineRepository onlineRepository;

    public UserService(Context context, UserOfflineRepository offlineRepository, UserOnlineRepository onlineRepository) {
        this.context = context;
        this.offlineRepository = offlineRepository;
        this.onlineRepository = onlineRepository;
    }

    public void getUserAsync(ResponseCallback<User> callback) {
        offlineRepository.getAsync(getFallbackToOnlineCallback(callback));
    }

    private ResponseCallback<User> getFallbackToOnlineCallback(ResponseCallback<User> callback) {
        return new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                callback.success(data);
            }

            @Override
            public void error(Exception e) {
                if (NetworkUtils.isConnected(context)) {
                    onlineRepository.getAsync(getSaveOfflineCallback(callback));
                } else {
                    callback.error(e);
                }
            }
        };
    }

    private ResponseCallback<User> getSaveOfflineCallback(ResponseCallback<User> callback) {
        return new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                offlineRepository.save(data);
                callback.success(data);
            }

            @Override
            public void error(Exception e) {
                callback.error(e);
            }
        };
    }
}
