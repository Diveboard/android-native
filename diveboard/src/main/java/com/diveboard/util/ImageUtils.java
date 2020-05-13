package com.diveboard.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.Map;

import androidx.core.content.ContextCompat;

public class ImageUtils {

    static Map<Integer, BitmapDescriptor> cache = new HashMap<>();

    public synchronized static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        if (cache.containsKey(vectorResId)) {
            return cache.get(vectorResId);
        }
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        BitmapDescriptor result = BitmapDescriptorFactory.fromBitmap(bitmap);
        cache.put(vectorResId, result);
        return result;
    }
}
