package com.diveboard.dataaccess;

import android.content.Context;

import com.diveboard.dataaccess.datamodel.User2;

public class UserOfflineRepository extends FileRepository<User2> {

    public UserOfflineRepository(Context context) {
        super(context);
    }

    @Override
    protected String getFileName() {
        return "user.json";
    }

    @Override
    protected Class getClazz() {
        return User2.class;
    }
}
