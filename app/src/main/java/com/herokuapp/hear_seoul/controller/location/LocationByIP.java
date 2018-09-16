/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.core.Logger;

import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationByIP extends AsyncTask<Void, Integer, Void> {
    private callback callback;
    private String countryCode = "", country = "";
    private double latitude = 0, longitude = 0;
    private String errorMessage = "";
    private ProgressDialog locationLoading;

    public LocationByIP(Context context, callback callback) {
        this.callback = callback;
        locationLoading = new ProgressDialog(context);
        locationLoading.setMessage(context.getString(R.string.loading));
        locationLoading.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        locationLoading.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        locationLoading.dismiss();
        if (errorMessage.equals("")) {
            callback.onLocationFetchSuccess(country, countryCode, latitude, longitude);
        } else {
            callback.onLocationFetchFail(errorMessage);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://ip-api.com/json").build();

        try (Response response = client.newCall(request).execute()) {
            JSONObject responseJson = new JSONObject(response.body().string());
            latitude = (responseJson.getDouble("lat"));
            longitude = (responseJson.getDouble("lon"));
            countryCode = (responseJson.getString("countryCode"));
            country = (responseJson.getString("country"));
        } catch (Exception e) {
            errorMessage = e.getMessage();
            Logger.e(errorMessage);
        }
        return null;
    }

    public interface callback extends Serializable {
        void onLocationFetchSuccess(String country, String city, double latitude, double longitude);

        void onLocationFetchFail(String message);
    }
}
