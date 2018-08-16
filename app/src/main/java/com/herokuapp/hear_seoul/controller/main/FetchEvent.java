package com.herokuapp.hear_seoul.controller.main;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.EventBean;
import com.herokuapp.hear_seoul.bean.EventBean;
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


public class FetchEvent extends AsyncTask<Context, Void, LinkedList<EventBean>> {

    public interface SuccessCallback extends Serializable {
        void successCallback(LinkedList<EventBean> results);
    }

    public interface FailCallback extends Serializable {
        void failCallback(LinkedList<EventBean> results);
    }

    private SuccessCallback successCallback;
    private FailCallback failCallback;

    public FetchEvent(SuccessCallback successCallback, FailCallback failCallback) {
        this.successCallback = successCallback;
        this.failCallback = failCallback;
    }

    @Override
    protected LinkedList<EventBean> doInBackground(Context... params) {
        final LinkedList<EventBean> results = new LinkedList<>();

        String eventRequestUrl = "http://openapi.seoul.go.kr:8088/" + params[0].getString(R.string.seoul_event_key) + "/json/SearchConcertDetailService/1/1000";

        try {
            Request request = new Request.Builder().url(eventRequestUrl).build();
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
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
        return results;
    }


    @Override
    protected void onPostExecute(LinkedList<EventBean> results) {
        if (results.size() > 0) {
            successCallback.successCallback(results);
        } else {
            failCallback.failCallback(results);
        }
    }
}