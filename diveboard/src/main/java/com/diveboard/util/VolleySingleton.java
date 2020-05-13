package com.diveboard.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.diveboard.mobile.ApplicationController;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    MessageDigest digest;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleySingleton() {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        ApplicationController ac = ApplicationController.getInstance();
        mRequestQueue = Volley.newRequestQueue(ac);
        try {
            mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
                private final DiskLruCache mCache = DiskLruCache.open(ac.getCacheDir(), 1, 1, 100 * 1024 * 1024);

                public void putBitmap(String url, Bitmap bitmap) {
                    try {
                        DiskLruCache.Editor editor = mCache.edit(MD5(url));
                        try (OutputStream output = editor.newOutputStream(0)) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                            editor.commit();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                public Bitmap getBitmap(String url) {
                    try {
                        DiskLruCache.Snapshot snapshot = mCache.get(MD5(url));
                        if (snapshot == null) {
                            return null;
                        }
                        InputStream inputStream = snapshot.getInputStream(0);
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inMutable = true;
                        return BitmapFactory.decodeStream(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static VolleySingleton getInstance() {
        if (mInstance == null) {
            mInstance = new VolleySingleton();
        }
        return mInstance;
    }

    private String MD5(String s) {
        digest.update(s.getBytes(), 0, s.length());
        return new BigInteger(1, digest.digest()).toString(16);
    }

    public RequestQueue getRequestQueue() {
        return this.mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return this.mImageLoader;
    }
}