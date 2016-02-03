package com.example.j14014.kadai2016;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private static final String TAG = "BLUETOOTH_SAMPLE";

    private static final float ACCEL_WEIGHT = 3f;

    private static final long FPS = 60;

    int width, height;

    private Droid droid;

    private final List<BaseObject> bulletList = new ArrayList<BaseObject>();
    public final List<BaseObject> enemybulletList = new ArrayList<BaseObject>();
    private final List<BaseObject> sendbulletList = new ArrayList<BaseObject>();

    public GameView(Context context) {
        super(context);

        getHolder().addCallback(this);
    }

    private void drawObject(Canvas canvas) {

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        canvas.drawColor(Color.WHITE);

        if (droid == null) {
            Bitmap droidBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            droid = new Droid(droidBitmap, width, height);
        }

        /*
            Droidの当たり判定
         */
        if (droid.getDroidX() < 0) {
            droid.setDroidX(0);
        }
        if (droid.getDroidX() > width - droid.getDroidwidth()) {
            droid.setDroidX(width - droid.getDroidwidth());
        }
        if (droid.getDroidY() < 0) {
            droid.setDroidY(0);
        }
        if (droid.getDroidY() > height - droid.getDroidheight()) {
            droid.setDroidY(height - droid.getDroidheight());
        }

        /*
            範囲外（画面外）へ移動した弾の座標をコピー
         */
        for (int i = 0; i < bulletList.size(); i++) {
            float bullety = bulletList.get(i).yPosition;
            if (bullety < 0) {
                enemybulletList.add(bulletList.get(i));
            }

        }

        for (int i = 0; i < enemybulletList.size(); i++) {

            //BaseObject bullet = enemybulletList.remove(i);
            float bulletx = enemybulletList.get(i).xPosition;
            float bullety = enemybulletList.get(i).yPosition;

            String message = String.valueOf(bulletx);

            Log.d(message,"Heyyyyyyyyyyy!!!");
            Log.d(enemybulletList.size() + "", i + "");

            sendMessage(message);

        }

        // 1Pの弾
        drawObjectList(canvas, bulletList, width, height);

        // 2P
        drawObjectList(canvas, enemybulletList, width, height);

        droid.draw(canvas);
    }

    private void sendMessage(String message){
        byte[] send = message.getBytes();
        try {
            PairingView.connection.write(send);
            Log.d(TAG,"Paryyyyyyyyy!!!!!!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(message,"yeh!!!!!!!!!");
        }
    }

    private static void drawObjectList(Canvas canvas, List<BaseObject> objectList, int width, int height) {
        for (int i = 0; i < objectList.size(); i++) {
            BaseObject object = objectList.get(i);
            if (object.isAvailable(width, height)) {
                object.move();
                object.draw(canvas);
            } else {
                objectList.remove(object);
                i--;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fire(event.getX(), event.getY());
                break;
        }

        return super.onTouchEvent(event);
    }

    private void fire(float x, float y) {
        Log.d("fireStart","Start");
        float centerX = droid.getDroidX() + droid.getDroidwidth() / 2;
        float centerY = droid.getDroidY() + droid.getDroidheight() / 2;

        float alignX = (x - centerX) / Math.abs(y - centerY);

        Bullet bullet = new Bullet(alignX, centerX, centerY);
        bulletList.add(0, bullet);

    }

    private DrawThread drawThread;

    private class DrawThread extends Thread {
        private boolean isFinished;

        @Override
        public void run() {
            super.run();

            SurfaceHolder holder = getHolder();
            while (!isFinished) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    drawObject(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }

                try {
                    sleep(1000 / FPS);
                } catch (InterruptedException e) {
                }
            }
        }
    }



    public void startDrawThread() {
        stopDrawThread();

        drawThread = new DrawThread();
        drawThread.start();

    }

    public boolean stopDrawThread() {
        if (drawThread == null) {
            return false;
        }

        drawThread.isFinished = true;
        drawThread = null;
        return true;
    }

    public void startSensor() {
        sensorValues = null;

        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stopSensor() {
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDrawThread();

        startSensor();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawThread();
    }

    private static final float ALPHA = 0.8f;
    private float[] sensorValues;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (sensorValues == null) {
            sensorValues = new float[3];
            sensorValues[0] = event.values[0];
            sensorValues[1] = event.values[1];
            sensorValues[2] = event.values[2];
            return;
        }

        sensorValues[0] = sensorValues[0] * ALPHA + event.values[0] * (1f - ALPHA);
        sensorValues[1] = sensorValues[1] * ALPHA + event.values[1] * (1f - ALPHA);
        sensorValues[2] = sensorValues[2] * ALPHA + event.values[2] * (1f - ALPHA);

        droid.move(sensorValues[1] * ACCEL_WEIGHT, sensorValues[0] * ACCEL_WEIGHT);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
