/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.data.UpdateInfo;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Utils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailEditActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    public static final int PICK_IMAGE = 100;
    private String TAG = "큰 지도 화면 액티비티";
    private TextView openTimeView, closeTimeView;
    private boolean isNewInformation;
    private boolean isFullTime = false;
    private SpotBean spotBean;
    private CircleImageView infoImageView;
    private Bitmap bmp;
    private ProgressDialog loadingDialog;

    @SuppressLint("SetTextI18n")
    private TimePickerDialog.OnTimeSetListener startTimePickerListener = (view, hourOfDay, minute) -> {
        openTimeView.setText(hourOfDay + ":" + minute);
    };
    @SuppressLint("SetTextI18n")
    private TimePickerDialog.OnTimeSetListener endTimePickerListener = (view, hourOfDay, minute) -> {
        closeTimeView.setText(hourOfDay + ":" + minute);
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

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setMessage(getString(R.string.loading));

        initViewData();
    }

    private Bitmap imgRotate(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();

        return resizedBitmap;
    }

    // 뷰의 데이터 설정
    @SuppressLint("CheckResult")
    private void initViewData() {
        // 장소명
        EditText titleEdit = findViewById(R.id.detail_edit_title);
        titleEdit.setText(spotBean.getTitle());

        // 이미지
        infoImageView = findViewById(R.id.detail_edit_image);
        infoImageView.setOnClickListener(this);

        // 이미지
        RequestOptions options = new RequestOptions();
        options.centerCrop();

        if (!spotBean.getImgSrc().equals("NO")) {
            Glide.with(this).load(spotBean.getImgSrc()).apply(options).into(infoImageView);
        }

        // 설명
        ((EditText) findViewById(R.id.detail_edit_description)).setText(spotBean.getDescription());

        // 영업시간
        Switch isFullTimeSwitch = findViewById(R.id.detail_edit_is_full_time);
        isFullTimeSwitch.setOnCheckedChangeListener(this);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        openTimeView = findViewById(R.id.detail_edit_start);
        openTimeView.setText(getString(R.string.open_time));
        closeTimeView = findViewById(R.id.detail_edit_end);
        closeTimeView.setText(getString(R.string.close_time));

        if (isNewInformation) {
            // 새 데이터 입력
            isFullTimeSwitch.setChecked(false);
        } else if (spotBean.getTime().equals("FULL")) {
            isFullTimeSwitch.setChecked(true);
        } else {
            String[] data = spotBean.getTime().split("/");
            openTimeView.setText(data[0]);
            closeTimeView.setText(data[1]);
        }

        openTimeView.setOnClickListener(this);
        closeTimeView.setOnClickListener(this);

        findViewById(R.id.detail_edit_save).setOnClickListener(this);
        findViewById(R.id.detail_edit_cancel).setOnClickListener(this);
    }

    // 영업시간 종일 영업 여부 체크 확인
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
            case R.id.detail_edit_image:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    //Display an error
                    return;
                }
                try {
                    InputStream inputStream = getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(inputStream));
                    bmp = BitmapFactory.decodeStream(bufferedInputStream);

                    if(bmp.getHeight() < bmp.getWidth()){
                        bmp = imgRotate(bmp);
                    }
                    
                    infoImageView.setImageBitmap(bmp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 데이터 업로드
    private void saveDataToServer() {
        spotBean.setTitle(((EditText) (findViewById(R.id.detail_edit_title))).getText().toString());
        String description = ((EditText) findViewById(R.id.detail_edit_description)).getText().toString();
        spotBean.setDescription(description);

        String time;
        boolean isTimeDateModified;

        if (isFullTime) {
            isTimeDateModified = true;
            time = "FULL";
        } else {
            time = openTimeView.getText() + "/" + closeTimeView.getText();
            isTimeDateModified = time.contains(getString(R.string.open_time)) || time.contains(getString(R.string.close_time));
            isTimeDateModified = !isTimeDateModified;
        }

        spotBean.setTime(time);

        if (isTimeDateModified && bmp != null && description.length() > 10) {
            loadingDialog.show();
            new UpdateInfo(spotBean, bmp, isNewInformation, new UpdateInfo.callback() {
                @Override
                public void onUpdateSuccess() {
                    loadingDialog.dismiss();
                    Utils.showStyleToast(DetailEditActivity.this, getString(R.string.upload));
                    finish();
                }

                @Override
                public void onUpdateFail(String message) {
                    loadingDialog.dismiss();
                    Snackbar.make(findViewById(R.id.detail_edit_content_container), message, Snackbar.LENGTH_SHORT).show();
                }
            }).start();

        } else {
            Snackbar.make(findViewById(R.id.detail_edit_content_container), getString(R.string.check_data), Snackbar.LENGTH_SHORT).show();
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
