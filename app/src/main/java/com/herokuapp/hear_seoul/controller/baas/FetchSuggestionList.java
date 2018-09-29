/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.baas;

import android.app.ProgressDialog;
import android.content.Context;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.skt.baas.api.BaasObject;
import com.skt.baas.api.BaasQuery;
import com.skt.baas.callback.BaasListCallback;
import com.skt.baas.exception.BaasException;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FetchSuggestionList {
    private OnFetchSuggestionListCallback callback;
    private ProgressDialog loadingProgress;

    public FetchSuggestionList(Context context, OnFetchSuggestionListCallback callback) {
        this.callback = callback;
        loadingProgress = new ProgressDialog(context);
        loadingProgress.setMessage(context.getString(R.string.loading_suggestion_list));
        loadingProgress.setCancelable(false);
    }


    public void getData(LinkedList<String> param) {
        loadingProgress.show();
        try {
            BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SUGGESTION.TABLE_NAME);
            baasQuery.whereContainedIn(Const.BAAS.SUGGESTION.ID, param);
            baasQuery.findInBackground(new BaasListCallback<BaasObject>() {
                @Override
                public void onSuccess(List<BaasObject> fetchResult, BaasException e) {
                    if (e == null) {
                        LinkedList<String> result = new LinkedList<>();
                        for (int index = 0; index < fetchResult.size(); index++) {
                            result.add(fetchResult.get(index).getString(Const.BAAS.SUGGESTION.SUGGEST));
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

    public interface OnFetchSuggestionListCallback extends Serializable {
        void onDataFetchSuccess(LinkedList<String> result);
    }
}
