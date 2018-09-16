/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.data.FetchSpotList;
import com.herokuapp.hear_seoul.controller.location.LocationByIP;
import com.herokuapp.hear_seoul.controller.main.SuggestionAdapter;
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
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator;


public class Suggestion extends Fragment implements LocationByIP.callback, FetchSpotList.callback {


    private LinkedList<SpotBean> result = new LinkedList<>();
    private SuggestionAdapter adapter;
    private AlertDialog locationAlert;
    private ProgressDialog locationLoading;

    public Suggestion() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggestion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.main_pager);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(80, 60, 60, 40);
        viewPager.setPageMargin(20);

        adapter = new SuggestionAdapter(getContext(), result);
        viewPager.setAdapter(adapter);

        CircleIndicator indicator = view.findViewById(R.id.main_indicator);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());

        locationAlert = new AlertDialog.Builder(getContext())
                .setMessage("현재 위치가 한국이 아닌 것 같습니다.")
                .setPositiveButton("다시 확인", (dialog, which) -> {
                    locationLoading.show();
                    new LocationByIP(getContext(), Suggestion.this).execute();
                })
                .setNegativeButton("종료", (dialog12, which) -> Objects.requireNonNull(getActivity()).finish())
                .create();

        locationLoading = new ProgressDialog(getContext());
        locationLoading.setMessage(getString(R.string.loading));
        locationLoading.setCancelable(false);

        locationLoading.show();
        new LocationByIP(getContext(), this).execute();
    }

    @Override
    public void onLocationFetchSuccess(String country, String countryCode, double latitude, double longitude) {
        locationLoading.dismiss();
        Handler mHandler = new Handler(Looper.getMainLooper());
        if (countryCode.equals("KR")) {
            Utils.saveLocation(getContext(), new LatLng(latitude, longitude));
            test(latitude, longitude);
        } else {
            mHandler.postDelayed(() -> locationAlert.show(), 0);
        }
    }

    private void test(double latitude, double longitude) {
        // 서버에 저장된 정보
        int max = 4;
        BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
        baasQuery.setLimit(4);
        baasQuery.orderByDescending(Const.BAAS.SPOT.TITLE);
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
                    adapter.notifyDataSetChanged();
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onLocationFetchFail(String message) {
        new LocationByIP(getContext(), this).execute();
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