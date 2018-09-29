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
import com.herokuapp.hear_seoul.bean.LocationBean;
import com.herokuapp.hear_seoul.core.Logger;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationByIP extends AsyncTask<Void, Integer, LocationBean> {
    private OnLocationFetchFinishCallback callback;
    private ProgressDialog loadingProgress;
    private Exception error;

    public LocationByIP(Context context, OnLocationFetchFinishCallback callback) {
        this.callback = callback;
        loadingProgress = new ProgressDialog(context);
        loadingProgress.setMessage(context.getString(R.string.loading));
        loadingProgress.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadingProgress.show();
    }

    @Override
    protected void onPostExecute(LocationBean param) {
        super.onPostExecute(param);
        loadingProgress.dismiss();
        callback.onLocationFetchFinish(param, error);
    }

    @Override
    protected LocationBean doInBackground(Void... params) {

        LocationBean result = new LocationBean();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://ip-api.com/json").build();

        try (Response response = client.newCall(request).execute()) {
            JSONObject responseJson = new JSONObject(Objects.requireNonNull(response.body()).string());
            result.setCountry(responseJson.getString("country"));
            result.setCountryCode(responseJson.getString("countryCode"));
            result.setLatitude(responseJson.getDouble("lat"));
            result.setLongitude(responseJson.getDouble("lon"));
        } catch (Exception e) {
            error = e;
            Logger.e(e.getMessage());
        }

        return result;
    }

    public interface OnLocationFetchFinishCallback extends Serializable {
        void onLocationFetchFinish(LocationBean result, Exception error);
    }
}
