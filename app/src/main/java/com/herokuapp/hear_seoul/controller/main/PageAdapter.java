package com.herokuapp.hear_seoul.controller.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.herokuapp.hear_seoul.ui.TestFragment;
import com.herokuapp.hear_seoul.ui.main.fragment.Event;
import com.herokuapp.hear_seoul.ui.main.fragment.List;
import com.herokuapp.hear_seoul.ui.main.fragment.Main;

public class PageAdapter extends FragmentPagerAdapter {

    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment temp;
        switch (position) {
            case 0:
                temp = new Main();
                break;
            case 1:
                temp = new List();
                break;
            case 2:
                temp = new Event();
                break;
            default:
                temp = new TestFragment();
        }
        return temp;
    }

    @Override
    public int getCount() {
        return 3;
    }
}