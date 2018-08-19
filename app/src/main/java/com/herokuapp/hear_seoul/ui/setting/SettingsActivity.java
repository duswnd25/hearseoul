/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.setting;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.herokuapp.hear_seoul.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_general);

        /*
        Preference autoLogIn = findPreference("launchAutoLogin");
        Preference questionToAdmin = findPreference("question_to_admin");


        autoLogIn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Setting.this, AutoLog.class);
                startActivity(intent);
                return true;
            }
        });

        questionToAdmin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"duswnd25@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "[쿠콜릭] - 문의");
                intent.putExtra(Intent.EXTRA_TEXT, "제목은 수정하지 말아주세요");
                startActivity(Intent.createChooser(intent, "메일 보내기"));
                return true;
            }
        });
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
