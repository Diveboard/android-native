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
    public static Bitmap getRoundedCornerBitmap(int size, int contentheight, int pixels) {
        Bitmap output = Bitmap.createBitmap(size, size, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color_circle = 0xFFFFFFFF;
        final int color_rect = 0xffffffff;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);
        //final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        //paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        //canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color_rect);
        //canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawRect(rect, paint);
        //canvas.drawOval(rectF, paint);
        //paint.setColor(color_circle);
        //paint.setAlpha(125);
        //Xfermode fermode = paint.getXfermode();
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        //paint.setARGB(125, 255, 255, 255);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        paint.setColor(0xff1C1C1C);
        paint.setAlpha(30);
        paint.setXfermode(new PorterDuffXfermode(Mode.ADD));
        paint.setStyle(Paint.Style.STROKE);
        int strokeWidth = contentheight * 32 / 1000;
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(size / 2, size / 2, (size / 2) - (strokeWidth / 2), paint);
        //canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}