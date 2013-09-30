package com.diveboard.mobile;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Xfermode;

public class ImageHelper {
    public static Bitmap getRoundedLayer(int size, int contentheight, int pixels) {
        Bitmap output = Bitmap.createBitmap(size, size, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color_circle = 0xFFFFFFFF;
        final int color_rect = 0xffffffff;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        paint.setColor(color_rect);
        canvas.drawRect(rect, paint);

        Xfermode fermode = paint.getXfermode();
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        paint.setColor(0xff1C1C1C);
        paint.setAlpha(125);
        paint.setXfermode(fermode);
        paint.setStyle(Paint.Style.STROKE);
        int strokeWidth = contentheight * 32 / 1000;
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(size / 2, size / 2, (size / 2) - (strokeWidth / 2), paint);
        return output;
    }
}