package com.herokuapp.hear_seoul.ui.detail;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.core.Const;
import com.skt.baas.api.BaasGeoPoint;
import com.skt.baas.api.BaasObject;
import com.skt.baas.callback.BaasSaveCallback;
import com.skt.baas.callback.BaasUpsertCallback;
import com.skt.baas.exception.BaasException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

public class DetailEditActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private TextView startView, endView;
    private boolean isNewInformation;
    private String recentObjectId;
    private double latitude = 0, longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        isNewInformation = intent.getBooleanExtra("is_new_information", true);
        recentObjectId = intent.getStringExtra("objectId");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("정보 수정");
            actionBar.setHomeButtonEnabled(true);
        }

        Switch isFullTimeSwitch = findViewById(R.id.detail_edit_is_full_time);
        isFullTimeSwitch.setChecked(false);
        isFullTimeSwitch.setOnCheckedChangeListener(this);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        startView = findViewById(R.id.detail_edit_start);
        startView.setText("개점 시간");
        startView.setOnClickListener(this);

        endView = findViewById(R.id.detail_edit_end);
        endView.setText("폐점 시간");
        endView.setOnClickListener(this);

        NestedScrollView contentScrollView = findViewById(R.id.detail_edit_content_container);
        contentScrollView.setSmoothScrollingEnabled(true);

        findViewById(R.id.detail_edit_save).setOnClickListener(this);
        findViewById(R.id.detail_edit_cancel).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private TimePickerDialog.OnTimeSetListener startTimePickerListener = (view, hourOfDay, minute) -> {
        startView.setText(hourOfDay + ":" + minute);
    };

    @SuppressLint("SetTextI18n")
    private TimePickerDialog.OnTimeSetListener endTimePickerListener = (view, hourOfDay, minute) -> {
        endView.setText(hourOfDay + ":" + minute);
    };

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        findViewById(R.id.detail_edit_time_container).setVisibility(b ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_edit_start:
                new TimePickerDialog(this, startTimePickerListener, 15, 24, false).show();
                break;
            case R.id.detail_edit_end:
                new TimePickerDialog(this, endTimePickerListener, 15, 24, false).show();
                break;
            case R.id.detail_edit_save:
                saveInformation();
                break;
            case R.id.detail_edit_cancel:
                finish();
                break;
            default:
        }
    }

    private void saveInformation() {
        String time = startView.getText() + "/" + endView.getText();
        String title = ((EditText) findViewById(R.id.detail_edit_title)).getText().toString();
        String description = ((EditText) findViewById(R.id.detail_edit_description)).getText().toString();
        BaasGeoPoint location = new BaasGeoPoint(latitude, longitude);

        BaasObject baasObject = new BaasObject(Const.BAAS.SPOT.TABLE_NAME);
        baasObject.set(Const.BAAS.SPOT.TITLE, title);
        baasObject.set(Const.BAAS.SPOT.DESCRIPTION, description);
        baasObject.set(Const.BAAS.SPOT.TIME, time);

        if (isNewInformation) {
            baasObject.set(Const.BAAS.SPOT.LOCATION, location);
            // 새 장소
            baasObject.serverSaveInBackground(new BaasSaveCallback() {
                @Override
                public void onSuccess(BaasException e) {
                    if (e != null) {
                        // 에러 처리
                    }
                }
            });
        } else {
            // 기존 장소
            baasObject.setObjectId(recentObjectId);
            baasObject.serverUpsertInBackground(new BaasUpsertCallback() {
                @Override
                public void onSuccess(BaasException e) {
                    if (e != null) {
                        // 에러 처리
                    }
                }
            });
        }
    }
}
