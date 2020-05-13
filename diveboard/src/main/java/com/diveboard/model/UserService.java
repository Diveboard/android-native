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

    public void getUserAsync(ResponseCallback<User> callback, boolean forceOnline) {
        if (forceOnline) {
            executeOnline(callback);
            return;
        }
        offlineRepository.getAsync(getFallbackToOnlineCallback(callback));
    }

    public void saveUserAsync(ResponseCallback<User> callback, User user){
        //online only for now as long as it is used in wallet only so network should be available already to upload a picture
        onlineRepository.saveAsync(callback, user);
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
                    executeOnline(callback);
                } else {
                    callback.error(e);
                }
            }
        };
    }

    private void executeOnline(ResponseCallback<User> callback) {
        onlineRepository.getAsync(getSaveOfflineCallback(callback));
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
