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
import com.skt.baas.api.BaasGeoPoint;
import com.skt.baas.api.BaasObject;
import com.skt.baas.api.BaasQuery;
import com.skt.baas.callback.BaasListCallback;
import com.skt.baas.exception.BaasException;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FetchSpotList extends Thread {
    private FetchSpotList.callback callback;
    private LatLng location;
    private int distance;

    public FetchSpotList(LatLng location, int distance, FetchSpotList.callback callback) {
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
                        SpotBean spotBean = new SpotBean();
                        spotBean.setId(item.getString(Const.BAAS.SPOT.ID));
                        spotBean.setTitle(item.getString(Const.BAAS.SPOT.TITLE));
                        spotBean.setDescription(item.getString(Const.BAAS.SPOT.DESCRIPTION));
                        spotBean.setImgSrc(item.getString(Const.BAAS.SPOT.IMG_SRC));
                        spotBean.setAddress(item.getString(Const.BAAS.SPOT.ADDRESS));
                        spotBean.setAddress(item.getString(Const.BAAS.SPOT.ADDRESS));
                        spotBean.setTime(item.getString(Const.BAAS.SPOT.TIME));

                        BaasGeoPoint temp = (BaasGeoPoint) item.get(Const.BAAS.SPOT.LOCATION);
                        spotBean.setLocation(new LatLng(temp.getLatitude(), temp.getLongitude()));

                        result.add(spotBean);
                    }
                    callback.onDataFetchSuccess(result);
                } else {
                    callback.onDataFetchFail(e.getMessage());
                }
            }
        });
    }

    public interface callback extends Serializable {
        void onDataFetchSuccess(LinkedList<SpotBean> result);

        void onDataFetchFail(String message);
    }
}
