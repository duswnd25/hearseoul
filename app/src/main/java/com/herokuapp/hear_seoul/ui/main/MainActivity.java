/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.data.FetchSpotList;
import com.herokuapp.hear_seoul.controller.location.LocationByIP;
import com.herokuapp.hear_seoul.controller.main.SuggestionAdapter;
import com.herokuapp.hear_seoul.ui.setting.SettingsActivity;

import java.util.LinkedList;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements LocationByIP.callback, FetchSpotList.callback {

    ViewPager viewPager;
    private LinkedList<SpotBean> result = new LinkedList<>();
    private SuggestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.main_pager);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(80, 60, 60, 40);
        viewPager.setPageMargin(20);

        adapter = new SuggestionAdapter(this, result);
        viewPager.setAdapter(adapter);

        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());

        new LocationByIP(this).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationFetchSuccess(String city, double latitude, double longitude) {
        new FetchSpotList(new LatLng(latitude, longitude), 100, this).start();
    }

    @Override
    public void onLocationFetchFail(String message) {

    }

    @Override
    public void onDataFetchSuccess(LinkedList<SpotBean> result) {
        this.result.addAll(result);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDataFetchFail(String message) {

    }
}
