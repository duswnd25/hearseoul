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
import com.herokuapp.hear_seoul.core.Utils;

import java.util.LinkedList;
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
                    new LocationByIP(Suggestion.this).start();
                })
                .setNegativeButton("종료", (dialog12, which) -> Objects.requireNonNull(getActivity()).finish())
                .create();

        locationLoading = new ProgressDialog(getContext());
        locationLoading.setMessage(getString(R.string.loading));
        locationLoading.setCancelable(false);

        locationLoading.show();
        new LocationByIP(this).start();
    }

    @Override
    public void onLocationFetchSuccess(String country, String countryCode, double latitude, double longitude) {
        locationLoading.dismiss();
        Handler mHandler = new Handler(Looper.getMainLooper());
        if (countryCode.equals("KR")) {
            Utils.saveLocation(getContext(), new LatLng(latitude, longitude));
            new FetchSpotList(new LatLng(latitude, longitude), 100, 4, this).start();
        } else {
            mHandler.postDelayed(() -> locationAlert.show(), 0);
        }
    }

    @Override
    public void onLocationFetchFail(String message) {
        new LocationByIP(this).start();
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