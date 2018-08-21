/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.data;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.skt.baas.api.BaasGeoPoint;
import com.skt.baas.api.BaasObject;
import com.skt.baas.api.BaasQuery;
import com.skt.baas.callback.BaasQueryCallback;
import com.skt.baas.exception.BaasException;

import java.io.Serializable;

public class FetchInfoById extends Thread {
    private FetchInfoById.callback callback;
    private String id;

    public FetchInfoById(String id, FetchInfoById.callback callback) {
        this.id = id;
        this.callback = callback;
    }

    @Override
    public void run() {
        super.run();
        // 서버에 저장된 정보
        BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
        baasQuery.whereEqual(Const.BAAS.SPOT.ID, id);
        baasQuery.findFirstInBackground(new BaasQueryCallback<BaasObject>() {
            @Override
            public void onSuccess(BaasObject baasObject, BaasException e) {
                if (e == null) {
                    if (baasObject != null) {
                        SpotBean spotBean = new SpotBean();
                        spotBean.setObjectId(baasObject.getObjectId());
                        spotBean.setId(baasObject.getString(Const.BAAS.SPOT.ID));
                        spotBean.setTitle(baasObject.getString(Const.BAAS.SPOT.TITLE));
                        spotBean.setDescription(baasObject.getString(Const.BAAS.SPOT.DESCRIPTION));
                        spotBean.setImgSrc(baasObject.getString(Const.BAAS.SPOT.IMG_SRC));
                        spotBean.setAddress(baasObject.getString(Const.BAAS.SPOT.ADDRESS));
                        spotBean.setAddress(baasObject.getString(Const.BAAS.SPOT.ADDRESS));
                        spotBean.setTime(baasObject.getString(Const.BAAS.SPOT.TIME));

                        BaasGeoPoint temp = (BaasGeoPoint) baasObject.get(Const.BAAS.SPOT.LOCATION);
                        spotBean.setLocation(new LatLng(temp.getLatitude(), temp.getLongitude()));
                        callback.onInfoFetchSuccess(true, spotBean);
                    } else {
                        callback.onInfoFetchSuccess(false, null);
                    }

                } else {
                    Logger.e(e.getMessage());
                    callback.onInfoFetchFail(e.getMessage());
                }
            }
        });
    }

    public interface callback extends Serializable {
        void onInfoFetchSuccess(boolean isExist, SpotBean result);

        void onInfoFetchFail(String message);
    }
}
