/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.baas;

import android.app.ProgressDialog;
import android.content.Context;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.skt.baas.api.BaasGeoPoint;
import com.skt.baas.api.BaasObject;
import com.skt.baas.callback.BaasSaveCallback;
import com.skt.baas.callback.BaasUpsertCallback;
import com.skt.baas.exception.BaasException;

import java.io.Serializable;
import java.util.ArrayList;

public class InfoUploader {

    private callback callback;
    private ProgressDialog loadingProgress;

    public InfoUploader(Context context, callback callback) {
        this.callback = callback;
        loadingProgress = new ProgressDialog(context);
        loadingProgress.setMessage(context.getString(R.string.loading));
        loadingProgress.setCancelable(false);
    }

    public void update(SpotBean spotBean, ArrayList<String> originalImageSrc, boolean isNew, boolean isImageUpdate) {
        loadingProgress.show();

        BaasGeoPoint location = new BaasGeoPoint(spotBean.getLocation().latitude, spotBean.getLocation().longitude);

        BaasObject baasObject = new BaasObject(Const.BAAS.SPOT.TABLE_NAME);
        baasObject.set(Const.BAAS.SPOT.ID, spotBean.getId());
        baasObject.set(Const.BAAS.SPOT.TITLE, spotBean.getTitle());
        baasObject.set(Const.BAAS.SPOT.DESCRIPTION, spotBean.getDescription());
        baasObject.set(Const.BAAS.SPOT.LOCATION, location);
        baasObject.set(Const.BAAS.SPOT.ADDRESS, spotBean.getAddress());
        baasObject.set(Const.BAAS.SPOT.TIME, spotBean.getTime());
        baasObject.set(Const.BAAS.SPOT.IMG_SRC, spotBean.getImgUrlList());
        baasObject.set(Const.BAAS.SPOT.TAG, spotBean.getTag());
        baasObject.set(Const.BAAS.SPOT.PHONE, spotBean.getPhone());
        baasObject.set(Const.BAAS.SPOT.IS_INFLUENCER, spotBean.isInfluencer());

        if (isNew) {
            // 새 장소
            baasObject.set(Const.BAAS.SPOT.LOCATION, location);
            baasObject.serverSaveInBackground(new BaasSaveCallback() {
                @Override
                public void onSuccess(BaasException e) {
                    loadingProgress.dismiss();
                    if (e == null) {
                        callback.onUpdateSuccess();
                    } else {
                        Logger.e(e.getMessage());
                        callback.onUpdateFail(String.valueOf(e.getCode()));
                    }
                }
            });
        } else {
            // 기존 장소

            baasObject.addToArrayUnique(Const.BAAS.SPOT.IMG_SRC, spotBean.getImgUrlList());
            baasObject.setObjectId(spotBean.getObjectId());
            Logger.d(baasObject.toString());
            baasObject.serverUpsertInBackground(new BaasUpsertCallback() {
                @Override
                public void onSuccess(BaasException e) {
                    if (e == null) {
                        if (isImageUpdate) {
                            baasObject.removeToArray(Const.BAAS.SPOT.IMG_SRC, originalImageSrc);
                            baasObject.serverUpsertInBackground(new BaasUpsertCallback() {
                                @Override
                                public void onSuccess(BaasException e) {
                                    loadingProgress.dismiss();
                                    callback.onUpdateSuccess();
                                }
                            });
                        } else {
                            loadingProgress.dismiss();
                            callback.onUpdateSuccess();
                        }
                    } else {
                        Logger.e(e.getMessage());
                        loadingProgress.dismiss();
                        callback.onUpdateFail(String.valueOf(e.getCode()));
                    }
                }
            });
        }
    }

    public interface callback extends Serializable {
        void onUpdateSuccess();

        void onUpdateFail(String message);
    }
}