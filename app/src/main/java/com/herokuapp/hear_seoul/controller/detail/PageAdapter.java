/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.detail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.herokuapp.hear_seoul.ui.TestFragment;
import com.herokuapp.hear_seoul.ui.detail.fragment.Main;
import com.herokuapp.hear_seoul.ui.detail.fragment.Review;

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
                temp = new Review();
                break;
            default:
                temp = new TestFragment();
        }
        return temp;
    }

    @Override
    public int getCount() {
        return 2;
    }
}