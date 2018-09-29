/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.baas;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.R;
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
import java.util.LinkedList;
import java.util.List;

public class FetchMapMarkerList {
    private OnFetchMapPoiCallback callback;
    private ProgressDialog loadingProgress;

    public FetchMapMarkerList(Context context, OnFetchMapPoiCallback callback) {
        this.callback = callback;
        loadingProgress = new ProgressDialog(context);
        loadingProgress.setMessage(context.getString(R.string.loading_marker));
        loadingProgress.setCancelable(true);
    }


    public void getData(LatLng location) {
        loadingProgress.show();
        try {
            BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
            baasQuery.whereNearWithinKilometers(Const.BAAS.SPOT.LOCATION, new BaasGeoPoint(location.latitude, location.longitude), 5);
            baasQuery.findInBackground(new BaasListCallback<BaasObject>() {
                @Override
                public void onSuccess(List<BaasObject> fetchResult, BaasException e) {
                    if (e == null) {
                        LinkedList<SpotBean> result = new LinkedList<>();
                        Logger.d(String.valueOf(fetchResult.size()));
                        for (int index = 0; index < fetchResult.size(); index++) {
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
                            spotBean.setInfluencer(fetchResult.get(index).getBoolean(Const.BAAS.SPOT.IS_INFLUENCER));

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
                        loadingProgress.dismiss();
                        callback.onPoiFetch(result);
                    } else {
                        loadingProgress.dismiss();
                        Logger.e(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            loadingProgress.dismiss();
            Logger.e(e.getMessage());
        }
    }

    public interface OnFetchMapPoiCallback extends Serializable {
        void onPoiFetch(LinkedList<SpotBean> result);
    }
}
