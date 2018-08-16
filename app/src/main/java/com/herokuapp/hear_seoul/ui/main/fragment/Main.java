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
import com.herokuapp.hear_seoul.core.otto.LocationChangeEvent;
import com.herokuapp.hear_seoul.core.otto.OttoProvider;
import com.herokuapp.hear_seoul.core.otto.PermissionEvent;
import com.herokuapp.hear_seoul.ui.main.MapLargeActivity;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.otto.Subscribe;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;


public class Main extends Fragment implements MapView.CurrentLocationEventListener, PermissionListener {
    private MapView mapView;
    private View view;
    private int markerTag = 0;
    private Location location;
    private boolean isMapDestroy = false;

    public Main() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMapDestroy) {
            initMap();
            fetchSpot(location);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        OttoProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        OttoProvider.getInstance().unregister(this);
        super.onDestroy();
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

        view.findViewById(R.id.main_map_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View notThisView) {
                isMapDestroy = true;
                ((ViewGroup) view.findViewById(R.id.main_map_view)).removeAllViews();
                Intent intent = new Intent(getContext(), MapLargeActivity.class);
                intent.putExtra("location", location);
                startActivity(intent);
            }
        });
    }

    // 위치 권한 있음
    @SuppressLint("MissingPermission")
    @Override
    public void onPermissionGranted() {
        OttoProvider.getInstance().post(new PermissionEvent(true));
        initMap();
    }

    private void initMap() {
        isMapDestroy = false;
        ViewGroup mapViewContainer = view.findViewById(R.id.main_map_view);
        mapView = new MapView(Objects.requireNonNull(getContext()));
        mapView.setClickable(false);
        mapView.setMapType(MapView.MapType.Standard);
        mapView.setCurrentLocationEventListener(Main.this);
        mapViewContainer.addView(mapView);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
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
                .setAction(getString(R.string.open), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startAppInformationActivity();
                    }
                }).show();
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

    // 지도 현재 위치 업데이트
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Location location = new Location("");
        location.setLatitude(mapPointGeo.latitude);
        location.setLongitude(mapPointGeo.longitude);
        fetchSpot(location);
        OttoProvider.getInstance().post(new LocationChangeEvent(location));
    }

    // 지도 현재 위치 업데이트
    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    // 주변 정보 가져오기
    private void fetchSpot(Location location) {
        this.location = location;
        new FetchSpot(Objects.requireNonNull(getActivity()).getApplicationContext(), new FetchSpot.SuccessCallback() {
            @Override
            public void successCallback(LinkedList<SpotBean> results) {
                if (markerTag != 0) {
                    markerTag = 0;
                    mapView.removeAllPOIItems();
                }
                for (SpotBean s : results) {
                    addMarker(s);
                }
            }
        }, new FetchSpot.FailCallback() {
            @Override
            public void failCallback(LinkedList<SpotBean> results) {
                if (markerTag != 0) {
                    mapView.removeAllPOIItems();
                }
            }
        }).execute(location);
    }

    // 지도 마커 추가
    private void addMarker(SpotBean item) {
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.getLatitude(), item.getLongitude());
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(markerTag + " " + item.getTitle());
        marker.setTag(++markerTag);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(item.isVisit() ? MapPOIItem.MarkerType.BluePin : MapPOIItem.MarkerType.RedPin); // 기본으로 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.YellowPin); // 마커를 클릭했을때
        mapView.addPOIItem(marker);
    }
}