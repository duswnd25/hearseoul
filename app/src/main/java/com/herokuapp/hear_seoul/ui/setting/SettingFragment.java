/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.herokuapp.hear_seoul.R;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingFragment() {

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.pref_general);

        Preference reset = findPreference(getString(R.string.pref_like_list));
        Preference license = findPreference("pref_license");

        reset.setOnPreferenceClickListener(pref -> {
            startActivity(new Intent(getActivity(), LikeListActivity.class));
            return true;
        });

        license.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent("android.intent.action.VIEW");
            Uri uri = Uri.parse("https://hear-seoul.herokuapp.com/app/license");
            intent.setData(uri);
            startActivity(intent);
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}