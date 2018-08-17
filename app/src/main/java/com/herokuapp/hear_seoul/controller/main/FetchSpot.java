package com.herokuapp.hear_seoul.controller.main;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.skt.baas.api.BaasGeoPoint;
import com.skt.baas.api.BaasObject;
import com.skt.baas.api.BaasQuery;
import com.skt.baas.callback.BaasListCallback;
import com.skt.baas.exception.BaasException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FetchSpot extends AsyncTask<LatLng, Void, LinkedList<SpotBean>> {

    public interface SuccessCallback extends Serializable {
        void successCallback(LinkedList<SpotBean> results);
    }

    public interface FailCallback extends Serializable {
        void failCallback(LinkedList<SpotBean> results);
    }

    private SuccessCallback successCallback;
    private FailCallback failCallback;
    private String skZonePoiKey;

    public FetchSpot(Context context, SuccessCallback successCallback, FailCallback failCallback) {
        this.successCallback = successCallback;
        this.failCallback = failCallback;
        this.skZonePoiKey = context.getString(R.string.tdc_project_key);
    }

    @Override
    protected LinkedList<SpotBean> doInBackground(LatLng... locations) {
        final LinkedList<SpotBean> results = new LinkedList<>();

        // Landmark 정보
        String requestUrl = "https://apis.sktelecom.com/v1/zonepoi/pois?latitude=" +
                locations[0].latitude +
                "&longitude=" + locations[0].longitude +
                "&radiusCode=5&resultSize=20";

        try {
            Request request = new Request.Builder()
                    .header("TDCProjectKey", skZonePoiKey)
                    .url(requestUrl)
                    .build();

            skZonePoiKey = null;

            Response response = new OkHttpClient().newCall(request).execute();

            JSONArray responseArray = new JSONArray(
                    new JSONObject(Objects.requireNonNull(response.body()).string()).getString("results")
            );

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject jsonObject = responseArray.getJSONObject(i);

                SpotBean temp = new SpotBean();
                temp.setId(jsonObject.getString("poiId"));
                temp.setTitle(jsonObject.getString("name"));
                temp.setDescription(jsonObject.getString("classNm"));
                temp.setImgSrc("NO");

                temp.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
                temp.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));
                temp.setType("SK");

                temp.setVisit(false);
                results.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 서버에 저장된 정보
        BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
        baasQuery.whereNearWithinKilometers(Const.BAAS.SPOT.LOCATION, new BaasGeoPoint(locations[0].latitude, locations[0].longitude), 10);
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

                        boolean isVisit = false;
                        for (Object o : ((ArrayList) item.get(Const.BAAS.SPOT.VISIT))) {
                            isVisit = (o).toString().contains("");// TODO 사용자 닉네임으로 교체해야함
                        }
                        temp.setVisit(isVisit);

                        results.add(temp);
                    }
                }
            }
        });
        return results;
    }


    @Override
    protected void onPostExecute(LinkedList<SpotBean> results) {
        if (results.size() > 0) {
            successCallback.successCallback(results);
        } else {
            failCallback.failCallback(results);
        }
    }
}