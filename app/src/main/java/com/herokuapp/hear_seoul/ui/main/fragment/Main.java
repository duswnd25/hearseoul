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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.main.FetchSpot;
import com.herokuapp.hear_seoul.controller.main.SpotListAdapter;
import com.herokuapp.hear_seoul.core.otto.OttoProvider;
import com.herokuapp.hear_seoul.core.otto.PermissionEvent;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.otto.Subscribe;
import com.yalantis.taurus.PullToRefreshView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;


public class Main extends Fragment implements PermissionListener, OnMapReadyCallback {

    private String TAG = "홈 프래그먼트";
    private View view;
    private MapView mMapView;
    private GoogleMap googleMap;
    private Bundle savedInstanceState;
    private LinkedList<SpotBean> spotList = new LinkedList<>();
    private SpotListAdapter spotListAdapter;
    private PullToRefreshView pullToRefreshView;

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
            initMap();
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
        this.savedInstanceState = savedInstanceState;

        TedPermission.with(Objects.requireNonNull(getContext()))
                .setPermissionListener(Main.this)
                .setRationaleMessage(getString(R.string.location_permission_description))
                .setDeniedMessage(getString(R.string.permission_deny_description))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        ShimmerRecyclerView noticeListView = view.findViewById(R.id.fragment_recycler_common);
        noticeListView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Objects.requireNonNull(getContext()),
                mLayoutManager.getOrientation());

        noticeListView.addItemDecoration(dividerItemDecoration);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        noticeListView.setLayoutManager(mLayoutManager);

        spotListAdapter = new SpotListAdapter(getActivity(), spotList);
        noticeListView.setAdapter(spotListAdapter);

        pullToRefreshView = view.findViewById(R.id.pull_to_refresh);
        //pullToRefreshView.setOnRefreshListener(() -> fetchSpot());
    }

    // 위치 권한 있음
    @SuppressLint("MissingPermission")
    @Override
    public void onPermissionGranted() {
        OttoProvider.getInstance().post(new PermissionEvent(true));
    }

    private void initMap() {
        mMapView = view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setMyLocationEnabled(true);
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.4233438, -122.0728817))
                .title("LinkedIn")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.4629101, -122.2449094))
                .title("Facebook")
                .snippet("Facebook HQ: Menlo Park"));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.3092293, -122.1136845))
                .title("Apple"));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.4233438, -122.0728817), 10));

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
        final ShimmerRecyclerView shimmerRecyclerView = view.findViewById(R.id.fragment_recycler_common);
        shimmerRecyclerView.showShimmerAdapter();
        new FetchSpot(Objects.requireNonNull(getActivity()).getApplicationContext(), (FetchSpot.SuccessCallback) results -> {
            view.findViewById(R.id.empty_view).setVisibility(View.GONE);
            spotList.clear();
            spotList.addAll(results);
            spotListAdapter.notifyDataSetChanged();
            shimmerRecyclerView.hideShimmerAdapter();
            pullToRefreshView.setRefreshing(false);
            for (SpotBean s : results) {
                addMarker(s);
            }
        }, (FetchSpot.FailCallback) results -> {

        }).execute(location);
    }

    // 지도 마커 추가
    private void addMarker(SpotBean item) {

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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

}