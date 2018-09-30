/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.location.LocationByIP;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.herokuapp.hear_seoul.core.Utils;
import com.herokuapp.hear_seoul.ui.EmptyFragment;
import com.herokuapp.hear_seoul.ui.main.fragment.Event;
import com.herokuapp.hear_seoul.ui.main.fragment.Explore;
import com.herokuapp.hear_seoul.ui.main.fragment.Map;
import com.herokuapp.hear_seoul.ui.main.fragment.Suggestion;
import com.herokuapp.hear_seoul.ui.setting.SettingFragment;
import com.herokuapp.hear_seoul.ui.spot_detail.DetailActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    int currentIndex = 0;
    private int PLACE_PICKER_REQUEST = 1;
    private BottomNavigationView navigation;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        new LocationByIP(this, (LocationByIP.OnLocationFetchFinishCallback) (result, error) -> {
            if (error != null) {
                currentLocation = new LatLng(result.getLatitude(), result.getLongitude());
                Utils.saveLocation(MainActivity.this, currentLocation);
            }
        }).execute();

        currentLocation = Utils.getSavedLocation(this);

        navigation = findViewById(R.id.main_bottom_menu);
        navigation.setOnNavigationItemSelectedListener(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, new Suggestion());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                placePickerStart();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    // Place Picker 호출
    private void placePickerStart() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(Objects.requireNonNull(this)), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Place Picker 결과
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String message = getString(R.string.select_nearby_place);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            Place place = PlacePicker.getPlace(this, data);
            if (Utils.calcDistance(currentLocation, place.getLatLng()) < 3000) {
                SpotBean spotBean = new SpotBean();
                spotBean.setId(place.getId());
                spotBean.setTitle(place.getName().toString());
                spotBean.setLocation(place.getLatLng());
                spotBean.setTime("NO");
                spotBean.setAddress(Objects.requireNonNull(place.getAddress()).toString());
                String phone = String.valueOf(place.getPhoneNumber())
                        .replace("+82", "0")
                        .replaceAll(" ", "");
                spotBean.setPhone(phone);

                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(Const.INTENT_EXTRA.SPOT, spotBean);
                intent.putExtra(Const.INTENT_EXTRA.IS_NEW_INFORMATION, true);

                startActivity(intent);
                return;
            } else {
                message = getString(R.string.select_nearby_place);
            }
        }
        Utils.showStyleToast(this, message);
    }

    public void changeNavigationSelected(int menuId) {
        try {
            navigation.setSelectedItemId(menuId);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        int index;
        switch (menuItem.getItemId()) {
            case R.id.menu_suggestion:
                index = 0;
                break;
            case R.id.menu_explore:
                index = 1;
                break;
            case R.id.menu_event:
                index = 2;
                break;
            case R.id.menu_map:
                index = 3;
                break;
            case R.id.menu_setting:
                index = 4;
                break;
            default:
                index = -1;
        }

        if (index == currentIndex) {
            return false;
        } else {
            currentIndex = index;
        }

        Fragment temp;
        switch (currentIndex) {
            case 0:
                temp = new Suggestion();
                break;
            case 1:
                temp = new Explore();
                break;
            case 2:
                temp = new Event();
                break;
            case 3:
                temp = new Map();
                break;
            case 4:
                temp = new SettingFragment();
                break;
            default:
                temp = new EmptyFragment();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, temp);
        transaction.commit();
        return false;
    }
}
