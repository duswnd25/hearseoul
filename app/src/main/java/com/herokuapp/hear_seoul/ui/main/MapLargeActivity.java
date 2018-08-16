package com.herokuapp.hear_seoul.ui.main;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.main.FetchSpot;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.LinkedList;
import java.util.Objects;

public class MapLargeActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private int markerTag;
    private static MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_large);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        ViewGroup mapViewContainer = findViewById(R.id.map_large_map_view);
        mapView = new MapView(this);
        mapView.setClickable(false);
        mapView.setMapType(MapView.MapType.Standard);
        mapView.setCurrentLocationEventListener(this);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);

        fetchSpot((Location) getIntent().getParcelableExtra("location"));
    }

    @Override
    public void onPause() {
        ((ViewGroup) findViewById(R.id.map_large_map_view)).removeAllViews();
        super.onPause();
    }

    private void addMarker(SpotBean item) {
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.getLatitude(), item.getLongitude());
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(markerTag + " " + item.getTitle());
        marker.setTag(markerTag++);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(item.isVisit() ? MapPOIItem.MarkerType.BluePin : MapPOIItem.MarkerType.RedPin); // 기본으로 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.YellowPin); // 마커를 클릭했을때
        mapView.addPOIItem(marker);
    }

    private void fetchSpot(Location location) {
        new FetchSpot(this, new FetchSpot.SuccessCallback() {
            @Override
            public void successCallback(LinkedList<SpotBean> results) {
                mapView.removeAllPOIItems();
                for (SpotBean s : results) {
                    addMarker(s);
                }
            }
        }, new FetchSpot.FailCallback() {
            @Override
            public void failCallback(LinkedList<SpotBean> results) {

            }
        }).execute(location);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Location location = new Location("");
        location.setLatitude(mapPointGeo.latitude);
        location.setLongitude(mapPointGeo.longitude);
        fetchSpot(location);
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate temp = mapView.getMapCenterPoint().getMapPointGeoCoord();
        Location location = new Location("");
        location.setLatitude(temp.latitude);
        location.setLongitude(temp.longitude);
        fetchSpot(location);
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

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
}
