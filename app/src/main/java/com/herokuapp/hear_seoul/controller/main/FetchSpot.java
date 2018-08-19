/*
 * Copyright 2018 YeonJung Kim
 * GitHub : @duswnd25
 * Site   : https://yeonjung.herokuapp.com/
 *
 * get data from backend service
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.herokuapp.hear_seoul.controller.main;

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

public class FetchSpot {
    private FetchSpot.callback callback;

    public interface callback extends Serializable {
        void onDataFetchSuccess(LinkedList<SpotBean> result);

        void onDataFetchFail(String message);
    }

    public FetchSpot() {

    }

    public void get(LatLng location, int distance, FetchSpot.callback newCallback) {
        LinkedList<SpotBean> result = new LinkedList<>();
        this.callback = newCallback;
        // 서버에 저장된 정보
        BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
        baasQuery.whereNearWithinKilometers(Const.BAAS.SPOT.LOCATION, new BaasGeoPoint(location.latitude, location.longitude), distance);
        baasQuery.findInBackground(new BaasListCallback<BaasObject>() {
            @Override
            public void onSuccess(List<BaasObject> fetchResult, BaasException e) {
                if (e == null || fetchResult != null) {
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
                        temp.setLatitude(geoPoint.getLatitude());
                        temp.setLongitude(geoPoint.getLongitude());
                        temp.setVisit(false);

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
