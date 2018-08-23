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
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import com.herokuapp.hear_seoul.controller.data.FetchSpotList;
import com.herokuapp.hear_seoul.controller.main.SpotListAdapter;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Utils;
import com.herokuapp.hear_seoul.core.otto.OttoProvider;
import com.herokuapp.hear_seoul.core.otto.PermissionEvent;
import com.herokuapp.hear_seoul.ui.detail.DetailActivity;
import com.herokuapp.hear_seoul.ui.main.MapLargeActivity;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.otto.Subscribe;
import com.yalantis.taurus.PullToRefreshView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class Home extends Fragment implements PermissionListener, OnMapReadyCallback, PullToRefreshView.OnRefreshListener, View.OnClickListener {

    private final String TAG = "메인 (홈)";
    private int PLACE_PICKER_REQUEST = 1;
    private View rootView;
    private MapView mapView;
    private GoogleMap googleMap;
    private Bundle savedInstanceState;
    private LinkedList<SpotBean> spotList = new LinkedList<>();
    private SpotListAdapter spotListAdapter;
    private PullToRefreshView pullToRefreshView;
    private LatLng currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private ShimmerRecyclerView spotListView;

    // 위치 콜백
    private LocationCallback locationCallback = new LocationCallback() {
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

    public Home() {
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
            rootView.findViewById(R.id.home_map_replace_text).setVisibility(View.GONE);
            initMap();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;
        this.savedInstanceState = savedInstanceState;

        // 권한 체크
        TedPermission.with(Objects.requireNonNull(getContext()))
                .setPermissionListener(Home.this)
                .setRationaleMessage(getString(R.string.location_permission_description))
                .setDeniedMessage(getString(R.string.permission_deny_description))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        // Shimmer rootView 설정
        spotListView = view.findViewById(R.id.template_recycler_common);
        spotListView.setHasFixedSize(true);

        // Recycler 뷰의 레이아웃 타입과 구분선 설정
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Objects.requireNonNull(getContext()),
                mLayoutManager.getOrientation());

        spotListView.addItemDecoration(dividerItemDecoration);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        spotListView.setLayoutManager(mLayoutManager);

        spotListAdapter = new SpotListAdapter(getActivity(), spotList);
        spotListView.setAdapter(spotListAdapter);

        // 검색 버튼 세팅
        FloatingActionButton searchButton = view.findViewById(R.id.home_search);
        searchButton.setOnClickListener(this);

        // 리프레시뷰의 설정
        pullToRefreshView = view.findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setOnRefreshListener(this);

        spotListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    searchButton.show();
                } else {
                    searchButton.hide();
                }
            }
        });
    }

    // 위치 권한 있음
    @Override
    public void onPermissionGranted() {
        initLocation();
        OttoProvider.getInstance().post(new PermissionEvent(true));
    }

    private void initLocation() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
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
                .Builder(Objects.requireNonNull(getContext()))
                .text(getString(R.string.location_permission_description))
                .textColor(Color.WHITE)
                .backgroundColor(getContext().getColor(R.color.colorAccent))
                .show();

        Snackbar.make(Objects.requireNonNull(getView()), getString(R.string.permission_deny_description), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.open), view -> Utils.startAppInformationActivity(getContext())).show();
    }

    private void initMap() {
        mapView = rootView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // 즉시 지도를 가져오기 위해 필요함
        try {
            MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.getUiSettings().setAllGesturesEnabled(false);
        this.googleMap.getUiSettings().setCompassEnabled(false);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.setOnMapClickListener(latLng -> {
            Intent intent = new Intent(getActivity(), MapLargeActivity.class);
            intent.putExtra(Const.INTENT_EXTRA.LOCATION, currentLocation);
            startActivity(intent);
        });
    }

    // 주변 정보 가져오기
    private void fetchSpot(LatLng location) {
        spotListView.showShimmerAdapter();
        new FetchSpotList(location, 10, new FetchSpotList.callback() {
            @Override
            public void onDataFetchSuccess(LinkedList<SpotBean> result) {
                spotListView.hideShimmerAdapter();
                pullToRefreshView.setRefreshing(false);
                // 데이터 교체
                spotList.clear();
                spotList.addAll(result);
                spotListAdapter.notifyDataSetChanged();

                if (spotList.size() == 0) {
                    rootView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                } else {
                    rootView.findViewById(R.id.empty_view).setVisibility(View.GONE);
                    for (SpotBean s : result) {
                        addMarker(s);
                    }
                }
            }

            @Override
            public void onDataFetchFail(String message) {
                Log.e(TAG, message);
            }
        }).start();
    }

    // Place Picker 호출
    private void placePickerStart() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(Objects.requireNonNull(getActivity())), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Place Picker 결과
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, Objects.requireNonNull(getContext()));
                if (Utils.calcDistance(currentLocation, place.getLatLng()) < 200) {
                    SpotBean spotBean = new SpotBean();
                    spotBean.setId(place.getId());
                    spotBean.setTitle(place.getName().toString());
                    spotBean.setLocation(place.getLatLng());
                    spotBean.setTime("NO");
                    spotBean.setAddress(Objects.requireNonNull(place.getAddress()).toString());

                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra(Const.INTENT_EXTRA.SPOT, spotBean);
                    getContext().startActivity(intent);
                } else {
                    Utils.showStyleToast(getContext(), "근처의 위치를 선택하세요");
                }
            } else {
                Utils.showStyleToast(getContext(), "근처의 위치를 선택하세요");
            }
        }
    }

    // 지도 마커 추가
    private void addMarker(SpotBean item) {
        googleMap.addMarker(new MarkerOptions().position(item.getLocation()).title(item.getTitle()).snippet(item.getDescription()));
    }

    @Override
    public void onDestroy() {
        OttoProvider.getInstance().unregister(this);
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
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    // Refresh View 의 refresh 컨트롤
    @Override
    public void onRefresh() {
        if (currentLocation != null) {
            fetchSpot(currentLocation);
        } else {
            pullToRefreshView.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_search:
                placePickerStart();
                break;
            default:
        }
    }
}