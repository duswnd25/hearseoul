/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.herokuapp.hear_seoul.core.Utils;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
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


public class Map extends Fragment implements PermissionListener, OnMapReadyCallback, View.OnClickListener {

    private View rootView;
    private Context context;
    private MapView mapView;
    private GoogleMap googleMap;
    private Bundle savedInstanceState;
    private LinkedList<SpotBean> spotList = new LinkedList<>();
    private LatLng currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private ProgressDialog loadingProgress;

    // 위치 콜백
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Utils.saveLocation(context, currentLocation);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(16).build();
                if (googleMap != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                fetchSpot(currentLocation);
            }
        }
    };

    public Map() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;
        this.savedInstanceState = savedInstanceState;
        this.currentLocation = Utils.getSavedLocation(context);

        //view.findViewById(R.id.map_find).setOnClickListener(this);

        loadingProgress = new ProgressDialog(context);
        loadingProgress.setCancelable(false);
        loadingProgress.setMessage(getString(R.string.loading));

        // 권한 체크
        TedPermission.with(Objects.requireNonNull(context))
                .setPermissionListener(Map.this)
                .setRationaleMessage(getString(R.string.location_permission_description))
                .setDeniedMessage(getString(R.string.permission_deny_description))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    // 위치 권한 있음
    @Override
    public void onPermissionGranted() {
        initLocation();
        initMap();
    }

    private void initLocation() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(context), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(context));
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
    }

    // 위치 권한 없음
    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        new StyleableToast
                .Builder(Objects.requireNonNull(context))
                .text(getString(R.string.location_permission_description))
                .textColor(Color.WHITE)
                .backgroundColor(context.getColor(R.color.colorAccent))
                .show();

        Snackbar.make(Objects.requireNonNull(getView()), getString(R.string.permission_deny_description), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.open), view -> Utils.startAppInformationActivity(context)).show();
    }

    private void initMap() {
        loadingProgress.show();
        mapView = rootView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // 즉시 지도를 가져오기 위해 필요함
        try {
            MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        mapView.getMapAsync(this);
        loadingProgress.dismiss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(context), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.getUiSettings().setAllGesturesEnabled(true);
        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(16).build();
        this.googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        this.googleMap.setOnMapClickListener(latLng -> {

        });
    }

    // 주변 정보 가져오기
    private void fetchSpot(LatLng location) {
        int max = 1000;

        BaasQuery<BaasObject> baasQuery = BaasQuery.makeQuery(Const.BAAS.SPOT.TABLE_NAME);
        baasQuery.setLimit(max);
        baasQuery.orderByDescending(Const.BAAS.SPOT.TITLE);
        baasQuery.whereNearWithinKilometers(Const.BAAS.SPOT.LOCATION, new BaasGeoPoint(location.latitude, location.longitude), max);
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
                        spotBean.setTag(fetchResult.get(index).getInt(Const.BAAS.SPOT.TAG));
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

                        spotList.add(spotBean);
                    }
                    addMarker();
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }

    //TODO 지도 좌표에 따라 로드하게 변경해야함
    // 지도 마커 추가
    private void addMarker() {
        for (SpotBean spotBean : spotList) {
            int icon_resource;
            switch (spotBean.getTag()) {
                case 1:
                    icon_resource = R.drawable.ic_food_black;
                    break;
                case 2:
                    icon_resource = R.drawable.ic_cafe_black;
                    break;
                case 3:
                    icon_resource = R.drawable.ic_landmark_black;
                    break;
                case 4:
                    icon_resource = R.drawable.ic_show_black;
                    break;
                case 5:
                    icon_resource = R.drawable.ic_camera_black;
                    break;
                default:
                    icon_resource = R.drawable.ic_empty_black;

            }
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(icon_resource);

            googleMap.addMarker(new MarkerOptions()
                    .position(spotBean.getLocation())
                    .title(spotBean.getTitle())
                    .snippet(spotBean.getDescription())
                    .icon(icon));
        }
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
            if (googleMap != null && currentLocation != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(16).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            initLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
        }
    }
}