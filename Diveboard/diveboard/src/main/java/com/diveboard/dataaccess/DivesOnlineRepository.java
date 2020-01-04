package com.diveboard.dataaccess;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.dataaccess.datamodel.DeleteResponse;
import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DiveResponse;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.model.AuthenticationService;
import com.diveboard.util.DiveboardRequest;
import com.diveboard.util.ResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static com.diveboard.mobile.ApplicationController.getGson;
import static com.diveboard.mobile.ApplicationController.getGsonWithExclude;

public class DivesOnlineRepository {

    private Context context;
    private AuthenticationService authenticationService;
    private UserOnlineRepository userOnlineRepository;
    private UserOfflineRepository userOfflineRepository;

    public DivesOnlineRepository(Context context,
                                 AuthenticationService authenticationService,
                                 UserOnlineRepository userOnlineRepository,
                                 UserOfflineRepository userOfflineRepository) {
        this.context = context;
        this.authenticationService = authenticationService;
        //TODO: minor, this service should be rather dependent upon UserService, not underlying repos?
        this.userOnlineRepository = userOnlineRepository;
        this.userOfflineRepository = userOfflineRepository;
    }

    public void load(final ResponseCallback<DivesResponse> callback) {
        ResponseCallback<User> intCallback = new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                if (data != null) {
                    //update offline user data
                    userOfflineRepository.save(data);
                    if (data.dives.size() == 0) {
                        callback.success(new DivesResponse());
                    } else {
                        RequestQueue queue = Volley.newRequestQueue(context);
                        DiveboardRequest stringRequest = getGetDivesRequest(callback, data.dives);
                        queue.add(stringRequest);
                    }
                }
            }

            @Override
            public void error(Exception s) {
                callback.error(s);
            }
        };
        userOnlineRepository.getAsync(intCallback);
        Log.d(DivesOnlineRepository.class.toString(), "Get dives online");
    }

    private DiveboardRequest getGetDivesRequest(final ResponseCallback<DivesResponse> callback, List<Integer> dives) {
        String url = AppConfig.SERVER_URL + "/api/V2/dive";
        return new DiveboardRequest<DivesResponse>(Request.Method.POST, url, DivesResponse.class, callback) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> args = RequestHelper.getCommonRequestArgs(authenticationService);
                args.put("arg", getDives(dives).toString());
                return args;
            }
        };
    }

    private JSONArray getDives(List<Integer> dives) {
        JSONArray result = new JSONArray();
        try {
            for (Integer diveId : dives) {
                JSONObject value = new JSONObject();
                value.put("id", diveId);
                result.put(value);
            }
        } catch (JSONException e) {
            throw new RuntimeException();
        }
        return result;
    }

    public void saveDive(Dive dive, ResponseCallback<DiveResponse> callback) {
        Volley.newRequestQueue(context).add(getSaveDiveRequest(callback, dive));
    }

    private DiveboardRequest getSaveDiveRequest(final ResponseCallback<DiveResponse> callback, Dive dive) {
        String url = AppConfig.SERVER_URL + "/api/V2/dive";
        return new DiveboardRequest<DiveResponse>(Request.Method.POST, url, DiveResponse.class, callback) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> args = RequestHelper.getCommonRequestArgs(authenticationService);
                args.put("arg", getGsonWithExclude().toJson(dive));
                return args;
            }
        };
    }

    public void deleteDive(Dive dive, ResponseCallback<DeleteResponse> callback) {
        Volley.newRequestQueue(context).add(getDeleteDiveRequest(callback, dive));
    }

    private DiveboardRequest getDeleteDiveRequest(ResponseCallback<DeleteResponse> callback, Dive dive) {
        String url = RequestHelper.addCommonRequestArgs(AppConfig.SERVER_URL + "/api/V2/dive/" + dive.id + "?", authenticationService);
        return new DiveboardRequest<>(Request.Method.DELETE, url, DeleteResponse.class, callback);
    }
}
