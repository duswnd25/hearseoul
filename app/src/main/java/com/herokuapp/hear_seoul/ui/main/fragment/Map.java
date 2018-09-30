/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.baas.FetchMapMarkerList;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.herokuapp.hear_seoul.core.Utils;
import com.herokuapp.hear_seoul.ui.spot_detail.DetailActivity;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class Map extends Fragment implements PermissionListener, OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private View rootView;
    private Context context;
    private MapView mapView;
    private GoogleMap googleMap;
    private Bundle savedInstanceState;
    private LatLng currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LinkedList<SpotBean> spotList = new LinkedList<>();

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
        mLocationRequest.setInterval(30000); // 30 seconds interval
        mLocationRequest.setFastestInterval(30000);
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
        mapView = rootView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // 즉시 지도를 가져오기 위해 필요함
        try {
            MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        mapView.getMapAsync(this);
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
        this.googleMap.setOnMarkerClickListener(this);
        this.googleMap.setOnCameraIdleListener(() -> new FetchMapMarkerList(context, (FetchMapMarkerList.OnFetchMapPoiCallback) this::addMarker)
                .getData(googleMap.getCameraPosition().target));
    }

    //TODO 지도 좌표에 따라 로드하게 변경해야함
    // 지도 마커 추가
    private void addMarker(LinkedList<SpotBean> spotList) {
        this.googleMap.clear();
        this.spotList.clear();
        this.spotList.addAll(spotList);
        for (SpotBean spotBean : spotList) {
            int icon_resource;
            switch (spotBean.getTag()) {
                case 1:
                    icon_resource = R.drawable.ic_tag_food;
                    break;
                case 2:
                    icon_resource = R.drawable.ic_tag_coffee;
                    break;
                case 3:
                    icon_resource = R.drawable.ic_landmark_black;
                    break;
                case 4:
                    icon_resource = R.drawable.ic_tag_show;
                    break;
                case 5:
                    icon_resource = R.drawable.ic_tag_camera;
                    break;
                default:
                    icon_resource = R.drawable.ic_empty_black;

            }

            int size = 60;
            BitmapDrawable resource = (BitmapDrawable) getResources().getDrawable(icon_resource);
            Bitmap b = resource.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, size, size, false);
            googleMap.addMarker(new MarkerOptions()
                    .position(spotBean.getLocation())
                    .title(spotBean.getTitle())
                    .snippet(spotBean.getDescription())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        SpotBean spot = null;
        for (SpotBean s : spotList) {
            if (marker.getTitle().equals(s.getTitle())) {
                spot = s;
            }
        }
        if (spot == null) {
            return false;
        }
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Const.INTENT_EXTRA.SPOT, spot);
        context.startActivity(intent);
        return false;
    }
}