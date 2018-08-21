/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.detail;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.skt.baas.api.BaasGeoPoint;
import com.skt.baas.api.BaasObject;
import com.skt.baas.callback.BaasSaveCallback;
import com.skt.baas.callback.BaasUpsertCallback;
import com.skt.baas.exception.BaasException;

import java.util.Calendar;
import java.util.Objects;

public class DetailEditActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private String TAG = "큰 지도 화면 액티비티";
    private TextView startView, endView;
    private boolean isNewInformation;
    private boolean isFullTime = false;
    private SpotBean spotBean;

    @SuppressLint("SetTextI18n")
    private TimePickerDialog.OnTimeSetListener startTimePickerListener = (view, hourOfDay, minute) -> {
        startView.setText(hourOfDay + ":" + minute);
    };
    @SuppressLint("SetTextI18n")
    private TimePickerDialog.OnTimeSetListener endTimePickerListener = (view, hourOfDay, minute) -> {
        endView.setText(hourOfDay + ":" + minute);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        Intent intent = getIntent();
        isNewInformation = intent.getBooleanExtra(Const.INTENT_EXTRA.IS_NEW_INFORMATION, true);
        spotBean = intent.getParcelableExtra(Const.INTENT_EXTRA.SPOT);

        if (actionBar != null) {
            actionBar.setTitle(isNewInformation ? getString(R.string.upload_new_data) : getString(R.string.edit_data));
            Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        }

        initViewData();
    }

    private void initViewData() {
        // 장소명
        EditText titleEdit = findViewById(R.id.detail_edit_title);
        titleEdit.setEnabled(false);
        titleEdit.setFocusable(false);
        titleEdit.setText(spotBean.getTitle());

        // 설명
        ((EditText) findViewById(R.id.detail_edit_description)).setText(spotBean.getDescription());

        // 영업시간
        Switch isFullTimeSwitch = findViewById(R.id.detail_edit_is_full_time);
        isFullTimeSwitch.setOnCheckedChangeListener(this);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        startView = findViewById(R.id.detail_edit_start);
        startView.setText("개점 시간");
        endView = findViewById(R.id.detail_edit_end);
        endView.setText("폐점 시간");

        if (isNewInformation) {
            // 새 데이터 입력
            isFullTimeSwitch.setChecked(false);
        } else if (spotBean.getTime().equals("FULL")) {
            isFullTimeSwitch.setChecked(true);
        } else {
            String[] data = spotBean.getTime().split("/");
            startView.setText(data[0]);
            endView.setText(data[1]);
        }

        startView.setOnClickListener(this);
        endView.setOnClickListener(this);

        findViewById(R.id.detail_edit_save).setOnClickListener(this);
        findViewById(R.id.detail_edit_cancel).setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        findViewById(R.id.detail_edit_time_container).setVisibility(b ? View.GONE : View.VISIBLE);
        isFullTime = b;
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
                saveDataToServer();
                break;
            case R.id.detail_edit_cancel:
                finish();
                break;
            default:
        }
    }

    private void saveJobFinish(BaasException e) {
        StyleableToast.Builder styleableToast = new StyleableToast.Builder(getApplicationContext());
        styleableToast.textColor(Color.WHITE);
        styleableToast.backgroundColor(getColor(R.color.colorAccent));
        styleableToast.text(e == null ? getString(R.string.upload) : getString(R.string.error) + e.getCode());
        styleableToast.show();
        if (e != null) Log.e(TAG, e.getLocalizedMessage());
    }

    private void saveDataToServer() {
        String time = isFullTime ? "FULL" : startView.getText() + "/" + endView.getText();
        if (time.contains("시간")) {
            return;
        }
        String title = ((EditText) findViewById(R.id.detail_edit_title)).getText().toString();
        String description = ((EditText) findViewById(R.id.detail_edit_description)).getText().toString();

        BaasGeoPoint location = new BaasGeoPoint(spotBean.getLocation().latitude, spotBean.getLocation().longitude);

        BaasObject baasObject = new BaasObject(Const.BAAS.SPOT.TABLE_NAME);
        baasObject.set(Const.BAAS.SPOT.ID, spotBean.getId());
        baasObject.set(Const.BAAS.SPOT.TITLE, title);
        baasObject.set(Const.BAAS.SPOT.DESCRIPTION, description);
        baasObject.set(Const.BAAS.SPOT.LOCATION, location);
        baasObject.set(Const.BAAS.SPOT.ADDRESS, spotBean.getAddress());
        baasObject.set(Const.BAAS.SPOT.TIME, time);

        if (isNewInformation) {
            // 새 장소
            baasObject.set(Const.BAAS.SPOT.LOCATION, location);
            baasObject.serverSaveInBackground(new BaasSaveCallback() {
                @Override
                public void onSuccess(BaasException e) {
                    saveJobFinish(e);
                }
            });
        } else {
            // 기존 장소
            baasObject.setObjectId(spotBean.getObjectId());
            baasObject.serverUpsertInBackground(new BaasUpsertCallback() {
                @Override
                public void onSuccess(BaasException e) {
                    saveJobFinish(e);
                }
            });
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
