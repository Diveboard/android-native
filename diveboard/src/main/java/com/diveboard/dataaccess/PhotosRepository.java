package com.diveboard.dataaccess;

import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.diveboard.config.AppConfig;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.model.AuthenticationService;
import com.diveboard.util.DiveboardRequest;
import com.diveboard.util.ResponseCallback;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class PhotosRepository {
    private ApplicationController context;
    private AuthenticationService authenticationService;

    public PhotosRepository(ApplicationController context, AuthenticationService authenticationService) {
        this.context = context;
        this.authenticationService = authenticationService;
    }

    public void uploadPhoto(final ResponseCallback<PhotoUploadResponse> callback, Bitmap bitmap) {
        String url = AppConfig.SERVER_URL + "/api/picture/upload";
        DiveboardRequest request = new DiveboardRequest<PhotoUploadResponse>(Request.Method.POST, url, PhotoUploadResponse.class, callback) {
            @Override
            protected Map<String, String> getParams() {
                return RequestHelper.getCommonRequestArgs(authenticationService);
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                Map<String, DataPart> params = new HashMap<>();
                params.put("qqfile", new DataPart("file.jpg", baos.toByteArray(), "image/jpeg"));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
