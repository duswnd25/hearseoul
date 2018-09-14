/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.core;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.Serializable;


public class BitmapRotate extends Thread {

    private callback callback;
    private Bitmap bitmap;

    public BitmapRotate(Bitmap bitmap, callback callback) {
        this.callback = callback;
        this.bitmap = bitmap;
    }


    @Override
    public void run() {
        super.run();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(-90);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();

        callback.onFinish(resizedBitmap);
    }

    public interface callback extends Serializable {
        void onFinish(Bitmap bitmap);
    }
}