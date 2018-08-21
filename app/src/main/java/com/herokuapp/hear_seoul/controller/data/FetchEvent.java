/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.data;

import com.herokuapp.hear_seoul.bean.EventBean;
import com.herokuapp.hear_seoul.core.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FetchEvent extends Thread {

    private callback callback;
    private String apiKey;

    public FetchEvent(String apiKey, callback callback) {
        this.callback = callback;
        this.apiKey = apiKey;
    }


    @Override
    public void run() {
        super.run();
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
                results.add(temp);
            }
            callback.onEventFetchSuccess(results);
        } catch (Exception e) {
            Logger.e(e.getMessage());
            callback.onEventFetchFail(e.getMessage());
        }
    }

    public interface callback extends Serializable {
        void onEventFetchSuccess(LinkedList<EventBean> result);

        void onEventFetchFail(String message);
    }
}