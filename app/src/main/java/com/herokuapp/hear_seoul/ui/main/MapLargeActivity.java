/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.main.FetchSpot;
import com.herokuapp.hear_seoul.core.Utils;
import com.herokuapp.hear_seoul.core.otto.OttoProvider;
import com.herokuapp.hear_seoul.core.otto.PermissionEvent;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@SuppressLint("MissingPermission")
public class MapLargeActivity extends AppCompatActivity implements PermissionListener, OnMapReadyCallback, FetchSpot.callback {

    private String TAG = "큰 지도 액티비티";
    private MapView mMapView;
    private Bundle savedInstanceState;
    private GoogleMap googleMap;
    private LatLng currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        setContentView(R.layout.activity_map_large);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // 권한 체크
        TedPermission.with(Objects.requireNonNull(this))
                .setPermissionListener(this)
                .setRationaleMessage(getString(R.string.location_permission_description))
                .setDeniedMessage(getString(R.string.permission_deny_description))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        currentLocation = getIntent().getParcelableExtra("location");
    }

    // 위치 권한 있음
    @Override
    public void onPermissionGranted() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(this));
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        OttoProvider.getInstance().post(new PermissionEvent(true));
        initMap();
    }

    // 위치 권한 없음
    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        new StyleableToast
                .Builder(Objects.requireNonNull(this))
                .text(getString(R.string.location_permission_description))
                .textColor(Color.WHITE)
                .backgroundColor(getColor(R.color.colorAccent))
                .show();

        Snackbar.make(mMapView, getString(R.string.permission_deny_description), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.open), view -> Utils.startAppInformationActivity(this)).show();
    }

    @SuppressLint("MissingPermission")
    private void initMap() {
        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // 즉시 지도를 가져오기 위해 필요함
        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        mMapView.getMapAsync(this);
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(16).build();
                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                fetchSpot(currentLocation);
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        if (currentLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    // 주변 정보 가져오기
    private void fetchSpot(LatLng location) {
        new FetchSpot(location, 10, this).start();
    }

    // 지도 마커 추가
    private void addMarker(SpotBean item) {
        googleMap.addMarker(new MarkerOptions().position(item.getLocation()).title(item.getTitle()).snippet(item.getDescription()));
    }

    @Override
    public void onDestroy() {
        OttoProvider.getInstance().unregister(this);
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataFetchSuccess(LinkedList<SpotBean> result) {
        for (SpotBean s : result) {
            addMarker(s);
        }
    }

    @Override
    public void onDataFetchFail(String message) {

    }
}
