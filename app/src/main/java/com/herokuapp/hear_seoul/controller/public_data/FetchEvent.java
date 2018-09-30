/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.public_data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.EventBean;
import com.herokuapp.hear_seoul.core.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FetchEvent extends AsyncTask<Void, Integer, LinkedList<EventBean>> {

    private callback callback;
    private String apiKey;
    private ProgressDialog locationLoading;

    public FetchEvent(Context context, callback callback) {
        this.callback = callback;
        this.apiKey = context.getString(R.string.seoul_event_key);
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
    protected LinkedList<EventBean> doInBackground(Void... voids) {
        final LinkedList<EventBean> results = new LinkedList<>();

        String requestUrl = String.format("http://openapi.seoul.go.kr:8088/%s/json/SearchConcertDetailService/1/1000", apiKey);

        try {
            Request request = new Request.Builder().url(requestUrl).build();
            Response response = new OkHttpClient().newCall(request).execute();
            JSONObject requestResponse = new JSONObject(Objects.requireNonNull(response.body()).string()).getJSONObject("SearchConcertDetailService");
            JSONArray responseArray = requestResponse.getJSONArray("row");

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject jsonObject = responseArray.getJSONObject(i);
                EventBean temp = new EventBean();
                temp.setCodeName(jsonObject.getString("CODENAME"));
                temp.setTitle(jsonObject.getString("TITLE"));
                temp.setStartDate(jsonObject.getString("STRTDATE"));
                temp.setEndDate(jsonObject.getString("END_DATE"));
                temp.setTime(jsonObject.getString("TIME"));
                temp.setPlace(jsonObject.getString("PLACE"));
                temp.setOrgLink(jsonObject.getString("ORG_LINK"));
                temp.setMainImg(jsonObject.getString("MAIN_IMG").trim());
                temp.setHomepage(jsonObject.getString("HOMEPAGE"));
                temp.setUseTarget(jsonObject.getString("USE_TRGT"));
                temp.setUseFee(jsonObject.getString("USE_FEE"));
                temp.setInquery(jsonObject.getString("INQUIRY"));
                temp.setProgram(jsonObject.getString("PROGRAM"));
                temp.setContent(jsonObject.getString("CONTENTS"));
                temp.setGcode(jsonObject.getString("GCODE"));
                temp.setSponsor(jsonObject.getString("SPONSOR"));
                results.add(temp);
            }

            response.close();
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        Collections.sort(results, (o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
        return results;
    }

    @Override
    protected void onPostExecute(LinkedList<EventBean> eventBeans) {
        super.onPostExecute(eventBeans);
        locationLoading.dismiss();
        callback.onEventFetchSuccess(eventBeans);
    }

    public interface callback extends Serializable {
        void onEventFetchSuccess(LinkedList<EventBean> result);
    }
}