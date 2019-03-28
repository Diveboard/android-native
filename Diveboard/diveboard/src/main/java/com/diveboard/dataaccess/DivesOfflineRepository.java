package com.diveboard.dataaccess;

import android.content.Context;

import com.diveboard.dataaccess.datamodel.DivesResponse;

public class DivesOfflineRepository extends FileRepository<DivesResponse> {

    public DivesOfflineRepository(Context context) {
        super(context);
    }

    @Override
    protected String getFileName() {
        return "dives.json";
    }

    @Override
    protected Class getClazz() {
        return DivesResponse.class;
    }
}
