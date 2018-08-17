package com.herokuapp.hear_seoul.ui.main.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.main.FetchSpot;
import com.herokuapp.hear_seoul.core.otto.OttoProvider;
import com.herokuapp.hear_seoul.core.otto.PermissionEvent;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Objects;


public class Main extends Fragment implements PermissionListener {
    private View view;
    private int markerTag = 0;

    public Main() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        OttoProvider.getInstance().register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void PermissionEvent(PermissionEvent event) {
        if (event.isPermissionGranted()) {
            view.findViewById(R.id.main_map_hider).setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        TedPermission.with(Objects.requireNonNull(getContext()))
                .setPermissionListener(Main.this)
                .setRationaleMessage(getString(R.string.location_permission_description))
                .setDeniedMessage(getString(R.string.permission_deny_description))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    // 위치 권한 있음
    @SuppressLint("MissingPermission")
    @Override
    public void onPermissionGranted() {
        OttoProvider.getInstance().post(new PermissionEvent(true));
    }


    // 위치 권한 없음
    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        new StyleableToast
                .Builder(Objects.requireNonNull(getContext()))
                .text(getString(R.string.location_permission_description))
                .textColor(Color.WHITE)
                .backgroundColor(getContext().getColor(R.color.colorAccent))
                .show();

        Snackbar.make(view, getString(R.string.permission_deny_description), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.open), view -> startAppInformationActivity()).show();
    }


    // 앱 정보 화면
    private void startAppInformationActivity() {
        String packageName = Objects.requireNonNull(getContext()).getPackageName();
        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);

        }
    }

    // 주변 정보 가져오기
    private void fetchSpot(Location location) {
        new FetchSpot(Objects.requireNonNull(getActivity()).getApplicationContext(), (FetchSpot.SuccessCallback) results -> {
            if (markerTag != 0) {
                markerTag = 0;
            }
            for (SpotBean s : results) {
                addMarker(s);
            }
        }, (FetchSpot.FailCallback) results -> {
            if (markerTag != 0) {
            }
        }).execute(location);
    }

    // 지도 마커 추가
    private void addMarker(SpotBean item) {

    }

    @Override
    public void onDestroy() {
        OttoProvider.getInstance().unregister(this);
        super.onDestroy();
    }
}