/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.LocationBean;
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


public class Suggestion extends Fragment implements LocationByIP.OnLocationFetchFinishCallback, FetchSpotList.callback, PermissionListener {
    private FusedLocationProviderClient mFusedLocationClient;
    private LinkedList<SpotBean> result = new LinkedList<>();
    private SuggestionAdapter adapter;
    private Context context;

    public Suggestion() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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

        adapter = new SuggestionAdapter(context, result);
        viewPager.setAdapter(adapter);

        CircleIndicator indicator = view.findViewById(R.id.main_indicator);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // 권한 체크
        TedPermission.with(Objects.requireNonNull(getContext()))
                .setPermissionListener(Suggestion.this)
                .setRationaleMessage(getString(R.string.location_permission_description))
                .setDeniedMessage(getString(R.string.permission_deny_description))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void getSpotData(double latitude, double longitude) {
        Utils.saveLocation(context, new LatLng(latitude, longitude));
        // 서버에 저장된 정보
        ProgressDialog loadingProgress = new ProgressDialog(context);
        loadingProgress.setMessage(getString(R.string.loading));
        loadingProgress.setCancelable(false);
        loadingProgress.show();

        int max = 10;

        BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
        baasQuery.setLimit(max);
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
                    loadingProgress.dismiss();
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
        new FetchSpotList(Utils.getSavedLocation(context), 100, 4, this).start();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onPermissionGranted() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(Objects.requireNonNull(getActivity()), location -> {
            if (location == null) {
                new LocationByIP(context, Suggestion.this).execute();
            } else {
                getSpotData(location.getLatitude(), location.getLongitude());
            }
        });
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        new LocationByIP(context, this).execute();
    }

    @Override
    public void onLocationFetchFinish(LocationBean result, Exception error) {
        if (error == null) {
            if (result.getCountryCode().equals("KR")) {
                getSpotData(result.getLatitude(), result.getLongitude());
            } else {
                new AlertDialog.Builder(context)
                        .setMessage("현재 위치가 한국이 아닌 것 같습니다.\n현재위치 : " + result.getCountry())
                        .setPositiveButton("다시 확인", (dialog, which) -> new LocationByIP(context, Suggestion.this).execute())
                        .setNegativeButton("종료", (dialog12, which) -> Objects.requireNonNull(getActivity()).finish())
                        .create().show();
            }
        } else {
            new LocationByIP(context, this).execute();
        }
    }
}