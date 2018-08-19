/*
 * Copyright 2018 YeonJung Kim
 * GitHub : @duswnd25
 * Site   : https://yeonjung.herokuapp.com/
 *
 * Adapter for Detail view's tab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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