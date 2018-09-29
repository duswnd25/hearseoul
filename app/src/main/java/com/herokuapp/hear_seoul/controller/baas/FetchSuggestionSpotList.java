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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FetchSuggestionSpotList {
    private OnFetchSuggestionSpotListCallback callback;
    private ProgressDialog loadingProgress;

    public FetchSuggestionSpotList(Context context, OnFetchSuggestionSpotListCallback callback) {
        this.callback = callback;
        loadingProgress = new ProgressDialog(context);
        loadingProgress.setMessage(context.getString(R.string.loading_suggestion_spot));
        loadingProgress.setCancelable(false);
    }


    public void getData(LinkedList<String> param) {
        loadingProgress.show();
        try {
            BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
            baasQuery.whereContainedIn(Const.BAAS.SPOT.ID, param);
            baasQuery.findInBackground(new BaasListCallback<BaasObject>() {
                @Override
                public void onSuccess(List<BaasObject> fetchResult, BaasException e) {
                    if (e == null) {
                        Collections.sort(fetchResult, (o1, o2) -> o2.getUpdatedAt().compareTo(o1.getUpdatedAt()));
                        LinkedList<SpotBean> result = new LinkedList<>();
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
                        callback.onDataFetchSuccess(result);
                    } else {
                        Logger.e(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            loadingProgress.dismiss();
            Logger.e(e.getMessage());
        }
    }

    public interface OnFetchSuggestionSpotListCallback extends Serializable {
        void onDataFetchSuccess(LinkedList<SpotBean> result);
    }
}
