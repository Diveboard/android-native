package com.diveboard.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

public class Cache {
    private Context context;

    public Cache(Context context) {
        this.context = context;
    }

    private File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            // Error while creating file
        }
        return file;
    }
}
