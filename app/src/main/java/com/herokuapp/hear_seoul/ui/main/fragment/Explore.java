/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.data.FetchSpotList;
import com.herokuapp.hear_seoul.controller.main.ExploreAdapter;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.herokuapp.hear_seoul.core.Utils;
import com.skt.baas.api.BaasGeoPoint;
import com.skt.baas.api.BaasObject;
import com.skt.baas.api.BaasQuery;
import com.skt.baas.callback.BaasListCallback;
import com.skt.baas.exception.BaasException;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Explore extends Fragment implements FetchSpotList.callback {

    private LinkedList<SpotBean> result = new LinkedList<>();
    private ExploreAdapter adapter;
    private ProgressDialog locationLoading;

    public Explore() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.explore_recycler);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new ExploreAdapter(getContext(), result);
        recyclerView.setAdapter(adapter);

        locationLoading = new ProgressDialog(getContext());
        locationLoading.setMessage(getString(R.string.loading));
        locationLoading.setCancelable(false);

        LatLng currentLocation = Utils.getSavedLocation(getContext());
        getSpotData(currentLocation.latitude, currentLocation.longitude);
    }

    private void getSpotData(double latitude, double longitude) {
        // 서버에 저장된 정보
        locationLoading.show();
        int max = 1000;
        BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
        baasQuery.setLimit(max);
        baasQuery.whereNearWithinKilometers(Const.BAAS.SPOT.LOCATION, new BaasGeoPoint(latitude, longitude), 100);
        baasQuery.findInBackground(new BaasListCallback<BaasObject>() {
            @Override
            public void onSuccess(List<BaasObject> fetchResult, BaasException e) {
                if (e == null) {
                    Collections.sort(fetchResult, (o1, o2) -> o2.getUpdatedAt().compareTo(o1.getUpdatedAt()));

                    int maxIndex = max < fetchResult.size() ? max : fetchResult.size();
                    for (int index = 0; index < maxIndex; index++) {
                        SpotBean spotBean = new SpotBean();
                        spotBean.setId(fetchResult.get(index).getString(Const.BAAS.SPOT.ID));
                        spotBean.setTitle(fetchResult.get(index).getString(Const.BAAS.SPOT.TITLE));
                        spotBean.setDescription(fetchResult.get(index).getString(Const.BAAS.SPOT.DESCRIPTION));
                        spotBean.setAddress(fetchResult.get(index).getString(Const.BAAS.SPOT.ADDRESS));
                        spotBean.setAddress(fetchResult.get(index).getString(Const.BAAS.SPOT.ADDRESS));
                        spotBean.setTime(fetchResult.get(index).getString(Const.BAAS.SPOT.TIME));
                        spotBean.setTag(fetchResult.get(index).getString(Const.BAAS.SPOT.TAG));
                        spotBean.setPhone(fetchResult.get(index).getString(Const.BAAS.SPOT.PHONE));
                        spotBean.setUpdatedAt(fetchResult.get(index).getUpdatedAt());

                        try {
                            ArrayList<String> urlList = new ArrayList<>();
                            JSONArray jArray = fetchResult.get(index).getJSONArray(Const.BAAS.SPOT.IMG_SRC);
                            if (jArray != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    urlList.add(jArray.getString(i));
                                }
                            }
                            spotBean.setImgUrlList(urlList);
                        } catch (Exception error) {
                            Logger.e(error.getMessage());
                        }

                        BaasGeoPoint temp = (BaasGeoPoint) fetchResult.get(index).get(Const.BAAS.SPOT.LOCATION);
                        spotBean.setLocation(new LatLng(temp.getLatitude(), temp.getLongitude()));

                        result.add(spotBean);
                    }
                    locationLoading.dismiss();
                    adapter.notifyDataSetChanged();
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDataFetchSuccess(LinkedList<SpotBean> result) {
        this.result.addAll(result);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDataFetchFail(String message) {
        new FetchSpotList(Utils.getSavedLocation(getContext()), 100, 4, this).start();
    }
}