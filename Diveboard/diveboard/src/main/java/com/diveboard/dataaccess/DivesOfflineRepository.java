package com.diveboard.dataaccess;

import android.content.Context;

import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DiveResponse;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.util.ResponseCallback;

public class DivesOfflineRepository extends FileRepository<DivesResponse> {

    public DivesOfflineRepository(Context context) {
        super(context);
    }

    public void saveDive(Dive dive, ResponseCallback<DiveResponse, Exception> callback){
//TODO: implement
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
