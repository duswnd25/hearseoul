/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.data;

import android.graphics.Bitmap;

import com.herokuapp.hear_seoul.core.Logger;
import com.skt.baas.api.BaasFile;
import com.skt.baas.callback.BaasSaveCallback;
import com.skt.baas.exception.BaasException;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class BaasImageManager {

    public void uploadImage(String fileName, Bitmap bmp, uploadCallback callback) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        BaasFile file = new BaasFile(fileName + ".png", byteArray);
        file.serverSaveInBackground(new BaasSaveCallback() {
            @Override
            public void onSuccess(BaasException e) {
                if (e == null) {
                    callback.onImageUploadSuccess(file.getUrl());
                } else {
                    Logger.e(e.getMessage());
                    callback.onImageUploadFail(String.valueOf(e.getCode()));
                }
            }
        });
    }

    public interface downloadCallback extends Serializable {
        void onImageDownloadSuccess(String url);

        void onImageDownloadFail(String message);
    }

    public interface uploadCallback extends Serializable {
        void onImageUploadSuccess(String url);

        void onImageUploadFail(String message);
    }
}
