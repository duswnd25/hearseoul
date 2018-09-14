/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.location;

import com.herokuapp.hear_seoul.core.Logger;

import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationByIP extends Thread {
    private callback callback;

    public LocationByIP(callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        super.run();
        String countryCode = "", country = "";
        double latitude = 0, longitude = 0;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://ip-api.com/json").build();

        try (Response response = client.newCall(request).execute()) {
            JSONObject responseJson = new JSONObject(response.body().string());
            latitude = (responseJson.getDouble("lat"));
            longitude = (responseJson.getDouble("lon"));
            countryCode = (responseJson.getString("countryCode"));
            country = (responseJson.getString("country"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage());
            callback.onLocationFetchFail(e.getMessage());
        }
        callback.onLocationFetchSuccess(country, countryCode, latitude, longitude);
    }

    public interface callback extends Serializable {
        void onLocationFetchSuccess(String country, String city, double latitude, double longitude);

        void onLocationFetchFail(String message);
    }
}
