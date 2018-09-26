/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.baas.query;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.skt.baas.api.BaasGeoPoint;
import com.skt.baas.api.BaasObject;
import com.skt.baas.api.BaasQuery;
import com.skt.baas.callback.BaasListCallback;
import com.skt.baas.exception.BaasException;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FetchSpotList extends Thread {
    private FetchSpotList.callback callback;
    private LatLng location;
    private int distance, max;

    public FetchSpotList(LatLng location, int distance, int max, FetchSpotList.callback callback) {
        this.location = location;
        this.distance = distance;
        this.max = max;
        this.callback = callback;
    }

    @Override
    public void run() {
        super.run();
        LinkedList<SpotBean> result = new LinkedList<>();
        // 서버에 저장된 정보
        BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
        baasQuery.setLimit(max);
        baasQuery.orderByDescending(Const.BAAS.SPOT.TITLE);
        baasQuery.whereNearWithinKilometers(Const.BAAS.SPOT.LOCATION, new BaasGeoPoint(location.latitude, location.longitude), distance);
        baasQuery.findInBackground(new BaasListCallback<BaasObject>() {
            @Override
            public void onSuccess(List<BaasObject> fetchResult, BaasException e) {
                if (e == null) {
                    Collections.sort(fetchResult, (o1, o2) -> o2.getUpdatedAt().compareTo(o1.getUpdatedAt()));

                    int maxIndex = max < fetchResult.size() ? max : fetchResult.size();
                    for (int index = 0; index < maxIndex; index++) {
                        SpotBean spotBean = new SpotBean();
                        spotBean.setId(fetchResult.get(index).getString(Const.BAAS.SPOT.ID));
                        spotBean.setTitle(fetchResult.get(index).getString(Const.BAAS.SPOT.TITLE));
                        spotBean.setDescription(fetchResult.get(index).getString(Const.BAAS.SPOT.DESCRIPTION));
                        spotBean.setAddress(fetchResult.get(index).getString(Const.BAAS.SPOT.ADDRESS));
                        spotBean.setAddress(fetchResult.get(index).getString(Const.BAAS.SPOT.ADDRESS));
                        spotBean.setTime(fetchResult.get(index).getString(Const.BAAS.SPOT.TIME));
                        spotBean.setTag(fetchResult.get(index).getInt(Const.BAAS.SPOT.TAG));
                        spotBean.setPhone(fetchResult.get(index).getString(Const.BAAS.SPOT.PHONE));
                        spotBean.setUpdatedAt(fetchResult.get(index).getUpdatedAt());

                        try {
                            ArrayList<String> urlList = new ArrayList<>();
                            JSONArray jArray = fetchResult.get(index).getJSONArray(Const.BAAS.SPOT.IMG_SRC);
                            if (jArray != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    urlList.add(jArray.getString(i));
                                }
                            }
                            spotBean.setImgUrlList(urlList);
                        } catch (Exception error) {
                            Logger.e(error.getMessage());
                        }

                        BaasGeoPoint temp = (BaasGeoPoint) fetchResult.get(index).get(Const.BAAS.SPOT.LOCATION);
                        spotBean.setLocation(new LatLng(temp.getLatitude(), temp.getLongitude()));

                        result.add(spotBean);
                    }

                    callback.onDataFetchSuccess(result);
                } else {
                    Logger.e(e.getMessage());
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
