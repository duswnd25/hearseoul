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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Utils;
import com.herokuapp.hear_seoul.ui.TestFragment;
import com.herokuapp.hear_seoul.ui.detail.DetailActivity;
import com.herokuapp.hear_seoul.ui.main.fragment.Event;
import com.herokuapp.hear_seoul.ui.main.fragment.Explore;
import com.herokuapp.hear_seoul.ui.main.fragment.Map;
import com.herokuapp.hear_seoul.ui.main.fragment.Suggestion;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private int PLACE_PICKER_REQUEST = 1;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        loadFragment(new Suggestion());

        navigation = findViewById(R.id.main_bottom_menu);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
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
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {

            Place place = PlacePicker.getPlace(this, data);
            // if (Utils.calcDistance(currentLocation, place.getLatLng()) < 200) {
            // }
            SpotBean spotBean = new SpotBean();
            spotBean.setId(place.getId());
            spotBean.setTitle(place.getName().toString());
            spotBean.setLocation(place.getLatLng());
            spotBean.setTime("NO");
            spotBean.setAddress(Objects.requireNonNull(place.getAddress()).toString());
            String phone = String.valueOf(place.getPhoneNumber()).replace("+82", "0").replaceAll(" ", "");
            spotBean.setPhone(phone);

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(Const.INTENT_EXTRA.SPOT, spotBean);
            intent.putExtra(Const.INTENT_EXTRA.IS_NEW_INFORMATION, true);

            startActivity(intent);

        } else {
            Utils.showStyleToast(this, getString(R.string.select_nearby_place));
        }
    }

    public void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .commit();
        }
    }

    public void changeNavigationSelected(int menuId) {
        navigation.setSelectedItemId(menuId);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        switch (menuItem.getItemId()) {
            case R.id.menu_suggestion:
                loadFragment(new Suggestion());
                break;
            case R.id.menu_explore:
                loadFragment(new Explore());
                break;
            case R.id.menu_map:
                loadFragment(new Map());
                break;
            case R.id.menu_event:
                loadFragment(new Event());
                break;
            default:
                loadFragment(new TestFragment());
        }
        return false;
    }
}
