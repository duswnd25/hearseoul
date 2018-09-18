/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.herokuapp.hear_seoul.bean.SpotBean;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageDownloader extends AsyncTask<Void, Void, Void> {
    private String src;
    private OnDownloadFinishCallback callback;
    private Bitmap bitmap;
    private Exception exception;

    public ImageDownloader(String src, OnDownloadFinishCallback callback) {
        this.callback = callback;
        this.src = src;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        callback.onFinish(bitmap, exception);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(src).build();
        try (Response response = client.newCall(request).execute()) {
            InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
        } catch (Exception e) {
            exception = e;
            Logger.e(e.getMessage());
        }
        return null;
    }

    public interface OnDownloadFinishCallback extends Serializable {
        void onFinish(Bitmap resource, Exception error);
    }
}
