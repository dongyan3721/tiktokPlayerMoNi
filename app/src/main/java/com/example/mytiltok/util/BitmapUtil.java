package com.example.mytiltok.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BitmapUtil {

    public static Bitmap changeShape(Bitmap bitmap) {

        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

        int x = (bitmap.getWidth() - size) / 2;
        int y = (bitmap.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(bitmap, x, y, size, size);
        if (squaredBitmap != bitmap) {
            bitmap.recycle();
        }

        bitmap = Bitmap.createBitmap(size, size, bitmap.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size/2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();

        return  bitmap;
    }
}
