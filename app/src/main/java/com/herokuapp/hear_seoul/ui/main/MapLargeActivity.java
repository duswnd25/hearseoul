package com.herokuapp.hear_seoul.ui.main;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.main.FetchSpot;

import java.util.LinkedList;
import java.util.Objects;

public class MapLargeActivity extends AppCompatActivity {
    private int markerTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_large);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        ViewGroup mapViewContainer = findViewById(R.id.map_large_map_view);

    }

    @Override
    public void onPause() {
        ((ViewGroup) findViewById(R.id.map_large_map_view)).removeAllViews();
        super.onPause();
    }

    private void addMarker(SpotBean item) {

    }

    private void fetchSpot(LatLng location) {
        new FetchSpot(this, new FetchSpot.SuccessCallback() {
            @Override
            public void successCallback(LinkedList<SpotBean> results) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
