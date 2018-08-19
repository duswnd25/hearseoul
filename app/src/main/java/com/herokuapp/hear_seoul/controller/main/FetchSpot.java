/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.main;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.skt.baas.api.BaasGeoPoint;
import com.skt.baas.api.BaasObject;
import com.skt.baas.api.BaasQuery;
import com.skt.baas.callback.BaasListCallback;
import com.skt.baas.exception.BaasException;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FetchSpot extends Thread {
    private FetchSpot.callback callback;
    private LatLng location;
    private int distance;

    public interface callback extends Serializable {
        void onDataFetchSuccess(LinkedList<SpotBean> result);

        void onDataFetchFail(String message);
    }

    public FetchSpot(LatLng location, int distance, FetchSpot.callback callback) {
        this.location = location;
        this.distance = distance;
        this.callback = callback;
    }

    @Override
    public void run() {
        super.run();
        LinkedList<SpotBean> result = new LinkedList<>();
        // 서버에 저장된 정보
        BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
        baasQuery.whereNearWithinKilometers(Const.BAAS.SPOT.LOCATION, new BaasGeoPoint(location.latitude, location.longitude), distance);
        baasQuery.findInBackground(new BaasListCallback<BaasObject>() {
            @Override
            public void onSuccess(List<BaasObject> fetchResult, BaasException e) {
                if (e == null) {
                    for (BaasObject item : fetchResult) {
                        SpotBean temp = new SpotBean();
                        temp.setId(item.getObjectId());
                        temp.setTitle(item.getString(Const.BAAS.SPOT.TITLE));
                        temp.setDescription(item.getString(Const.BAAS.SPOT.DESCRIPTION));

                        String tempSrc = item.getString(Const.BAAS.SPOT.IMG_SRC);
                        if (tempSrc != null && tempSrc.length() > 10) {
                            temp.setImgSrc(tempSrc);
                        } else {
                            temp.setImgSrc("NO");
                        }

                        BaasGeoPoint geoPoint = (BaasGeoPoint) item.get(Const.BAAS.SPOT.LOCATION);
                        temp.setLocation(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                        temp.setVisit(false);

                        Log.e("TEST", temp.getTitle());
                        result.add(temp);
                    }
                    callback.onDataFetchSuccess(result);
                } else {
                    callback.onDataFetchFail(e.getMessage());
                }
            }
        });
    }
}
