package com.diveboard.model;

import com.diveboard.dataaccess.UserOfflineRepository;
import com.diveboard.dataaccess.datamodel.User2;
import com.diveboard.util.ResponseCallback;

public class UserService {
    private UserOfflineRepository offlineRepository;

    public UserService(UserOfflineRepository offlineRepository) {
        this.offlineRepository = offlineRepository;
    }

    public void getUserAsync(ResponseCallback<User2, Exception> callback) {
        //TODO: implement online repo
        offlineRepository.getAsync(callback);
    }
}
