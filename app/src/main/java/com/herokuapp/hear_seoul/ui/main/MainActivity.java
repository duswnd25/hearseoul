/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.ui.TestFragment;
import com.herokuapp.hear_seoul.ui.main.fragment.Map;
import com.herokuapp.hear_seoul.ui.main.fragment.Suggestion;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        loadFragment(new Suggestion());

        BottomNavigationView navigation = findViewById(R.id.main_bottom_menu);
        navigation.setOnNavigationItemSelectedListener(this);
    }


    private void loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        switch (menuItem.getItemId()) {
            case R.id.menu_suggestion:
                loadFragment(new Suggestion());
                break;
            case R.id.menu_map:
                loadFragment(new Map());
                break;
            default:
                loadFragment(new TestFragment());
        }
        return false;
    }
}
