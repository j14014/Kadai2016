package com.example.j14014.kadai2016;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Droid extends BaseObject {

    private final Paint paint = new Paint();

    public final Bitmap bitmap;

    private float droidX, droidY;

    private int droidwidth, droidheight;

    public Droid(Bitmap bitmap, int width, int height) {
        this.bitmap = bitmap;

        droidwidth = bitmap.getWidth();
        droidheight = bitmap.getHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, droidX, droidY, paint);
    }

    @Override
    public boolean isAvailable(int width, int height) {
        return true;
    }

    @Override
    public void move() {

    }

    public void move(float x, float y) {
        droidX += x;
        droidY += y;
    }

    public void setDroidX(float x) {
        droidX = x;
    }

    public void setDroidY(float y) {
        droidY = y;
    }

    public float getDroidX() {
        return droidX;
    }

    public float getDroidY() {
        return droidY;
    }

    public int getDroidwidth() {
        return droidwidth;
    }

    public int getDroidheight() {
        return droidheight;
    }
}
