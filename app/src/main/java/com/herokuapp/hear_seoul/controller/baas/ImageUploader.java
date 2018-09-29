/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.baas;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.core.Logger;
import com.skt.baas.api.BaasFile;
import com.skt.baas.callback.BaasSaveCallback;
import com.skt.baas.exception.BaasException;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class ImageUploader {
    private int imageNum = 0;
    private ArrayList<String> urlList = new ArrayList<>();
    private ProgressDialog loadingProgress;

    public ImageUploader(Context context) {
        loadingProgress = new ProgressDialog(context);
        loadingProgress.setMessage(context.getString(R.string.loading_upload_image));
        loadingProgress.setCancelable(false);
    }

    public void uploadImage(LinkedList<Bitmap> bitmapList, uploadCallback callback) {
        loadingProgress.show();
        this.imageNum = bitmapList.size();
        try {
            for (Bitmap bmp : bitmapList) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                byte[] byteArray = stream.toByteArray();
                bmp.recycle();
                BaasFile file = new BaasFile(byteArray);
                file.serverSaveInBackground(new BaasSaveCallback() {
                    @Override
                    public void onSuccess(BaasException e) {
                        if (e == null) {
                            urlList.add(file.getUrl());
                            if (urlList.size() == imageNum) {
                                loadingProgress.dismiss();
                                callback.onImageUploadSuccess(urlList);
                            }
                        } else {
                            loadingProgress.dismiss();
                            Logger.e(e.getMessage());
                            callback.onImageUploadFail(String.valueOf(e.getCode()));
                        }
                    }
                });
            }
        } catch (Exception e) {
            loadingProgress.dismiss();
            Logger.e(e.getMessage());
        }
    }

    public interface uploadCallback extends Serializable {
        void onImageUploadSuccess(ArrayList<String> urlList);

        void onImageUploadFail(String message);
    }
}
