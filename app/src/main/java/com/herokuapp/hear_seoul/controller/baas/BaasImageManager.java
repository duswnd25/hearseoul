/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.baas;

import android.graphics.Bitmap;

import com.herokuapp.hear_seoul.core.Logger;
import com.skt.baas.api.BaasFile;
import com.skt.baas.callback.BaasSaveCallback;
import com.skt.baas.exception.BaasException;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class BaasImageManager {
    private int imageNum = 0;
    private ArrayList<String> urlList = new ArrayList<>();

    public void uploadImage(String fileName, LinkedList<Bitmap> bitmapList, uploadCallback callback) {
        this.imageNum = bitmapList.size();

        for (Bitmap bmp : bitmapList) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteArray = stream.toByteArray();
            bmp.recycle();
            BaasFile file = new BaasFile(fileName + ".jpg", byteArray);
            file.serverSaveInBackground(new BaasSaveCallback() {
                @Override
                public void onSuccess(BaasException e) {
                    if (e == null) {
                        urlList.add(file.getUrl());
                        if (urlList.size() == imageNum) {
                            callback.onImageUploadSuccess(urlList);
                        }
                    } else {
                        Logger.e(e.getMessage());
                        callback.onImageUploadFail(String.valueOf(e.getCode()));
                    }
                }
            });
        }
    }

    public interface uploadCallback extends Serializable {
        void onImageUploadSuccess(ArrayList<String> urlList);

        void onImageUploadFail(String message);
    }
}
