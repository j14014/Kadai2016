package com.example.j14014.kadai2016;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bullet extends BaseObject {

    private final Paint paint = new Paint();

    private static final float SIZE = 15f;

    //public final float alignX;

    Bullet( float x, float y, boolean flag) {
        //this.alignX = alignX;
        yPosition = y;
        xPosition = x;

        bulletFlag = flag;

        paint.setColor(Color.RED);
    }

    Bullet (float x) {

        xPosition = x;
    }

    @Override
    public void move() {
        if (bulletFlag == true) {
            yPosition -= 5 * MOVE_WEIGHT;
        } else if (bulletFlag == false) {
            yPosition += 5 * MOVE_WEIGHT;
        }

        //xPosition += alignX * MOVE_WEIGHT;
    }



    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(xPosition, yPosition, SIZE, paint);
    }

}


