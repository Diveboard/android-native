package com.diveboard.dataaccess;

import android.content.Context;

import com.diveboard.dataaccess.datamodel.User;

public class UserOfflineRepository extends FileRepository<User> {

    public UserOfflineRepository(Context context) {
        super(context);
    }

    @Override
    protected String getFileName() {
        return "user.json";
    }

    @Override
    protected Class getClazz() {
        return User.class;
    }
}
