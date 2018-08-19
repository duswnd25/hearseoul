/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.main;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.bean.ZonePoiBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchLandMark extends Thread {
    private FetchLandMark.callback callback;
    private String skZonePoiKey;
    private LatLng location;

    public interface callback extends Serializable {
        void onPlaceSearchSuccess(LinkedList<ZonePoiBean> result);

        void onPlaceSearchFail(String message);
    }

    public FetchLandMark(String skZonePoiKey, LatLng location, FetchLandMark.callback callback) {
        this.skZonePoiKey = skZonePoiKey;
        this.location = location;
        this.callback = callback;
    }

    @Override
    public void run() {
        super.run();
        LinkedList<ZonePoiBean> result = new LinkedList<>();
        String requestUrl = String.format("https://apis.sktelecom.com/v1/zonepoi/pois?latitude=%s&longitude=%s&radiusCode=5&resultSize=20", location.latitude, location.longitude);

        try {
            Request request = new Request.Builder()
                    .header("TDCProjectKey", skZonePoiKey)
                    .url(requestUrl)
                    .build();

            Response response = new OkHttpClient().newCall(request).execute();

            JSONArray responseArray = new JSONArray(
                    new JSONObject(Objects.requireNonNull(response.body()).string()).getString("results")
            );

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject jsonObject = responseArray.getJSONObject(i);
                ZonePoiBean temp = new ZonePoiBean();
                temp.setId(jsonObject.getString("poiId"));
                temp.setName(jsonObject.getString("name"));
                temp.setClassNm(jsonObject.getString("classNm"));
                temp.setLocation(new LatLng(jsonObject.getInt("latitude"), jsonObject.getInt("longitude")));
                result.add(temp);
            }
            callback.onPlaceSearchSuccess(result);
        } catch (Exception e) {
            callback.onPlaceSearchFail(e.getMessage());
        }
    }
}
