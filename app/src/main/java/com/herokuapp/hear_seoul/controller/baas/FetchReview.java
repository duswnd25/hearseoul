/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.baas;

import android.os.AsyncTask;

import com.herokuapp.hear_seoul.bean.ReviewBean;
import com.herokuapp.hear_seoul.core.Const;
import com.skt.baas.api.BaasObject;
import com.skt.baas.api.BaasQuery;
import com.skt.baas.callback.BaasListCallback;
import com.skt.baas.exception.BaasException;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class FetchReview extends AsyncTask<String, Void, LinkedList<ReviewBean>> {

    private SuccessCallback successCallback;
    private FailCallback failCallback;

    public FetchReview(SuccessCallback successCallback, FailCallback failCallback) {
        this.successCallback = successCallback;
        this.failCallback = failCallback;
    }

    @Override
    protected LinkedList<ReviewBean> doInBackground(String... query) {
        final LinkedList<ReviewBean> results = new LinkedList<>();

        // 서버에 저장된 정보
        BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.REVIEW.TABLE_NAME);
        baasQuery.whereEqual(Const.BAAS.REVIEW.SPOT_ID, query);
        baasQuery.findInBackground(new BaasListCallback<BaasObject>() {
            @Override
            public void onSuccess(List<BaasObject> fetchResult, BaasException e) {
                if (e == null || fetchResult != null) {
                    for (BaasObject item : fetchResult) {
                        ReviewBean temp = new ReviewBean();
                        temp.setTitle(item.getString(Const.BAAS.REVIEW.TITLE));
                        temp.setContent(item.getString(Const.BAAS.REVIEW.CONTENT));
                        temp.setRate(item.getDouble(Const.BAAS.REVIEW.RATE));
                        temp.setSpotId(item.getString(Const.BAAS.REVIEW.SPOT_ID));
                        temp.setUploader(item.getString(Const.BAAS.REVIEW.UPLOADER));

                        results.add(temp);
                    }
                }
            }
        });
        return results;
    }

    @Override
    protected void onPostExecute(LinkedList<ReviewBean> results) {
        if (results.size() > 0) {
            successCallback.successCallback(results);
        } else {
            failCallback.failCallback(results);
        }
    }

    public interface SuccessCallback extends Serializable {
        void successCallback(LinkedList<ReviewBean> results);
    }


    public interface FailCallback extends Serializable {
        void failCallback(LinkedList<ReviewBean> results);
    }
}