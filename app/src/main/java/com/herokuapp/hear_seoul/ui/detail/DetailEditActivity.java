/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.detail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.data.UpdateInfo;
import com.herokuapp.hear_seoul.core.BitmapRotate;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.herokuapp.hear_seoul.core.Utils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class DetailEditActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PICK_IMAGE = 100;
    private boolean isNewInformation;
    private SpotBean spotBean;
    private Bitmap bmp;
    private static ProgressDialog loadingDialog;

    private ImageView mainImage;
    private EditText titleEdit, timeEdit, tagEdit, phoneEdit, descriptionEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_edit);

        Intent intent = getIntent();
        isNewInformation = intent.getBooleanExtra(Const.INTENT_EXTRA.IS_NEW_INFORMATION, true);
        spotBean = intent.getParcelableExtra(Const.INTENT_EXTRA.SPOT);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.detail_edit_collapsing_toolbar_layout);
        AppBarLayout appBarLayout = findViewById(R.id.detail_edit_appbar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(titleEdit.getText());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setMessage(getString(R.string.loading));

        initView();
    }

    private void initView() {
        mainImage = findViewById(R.id.detail_edit_main_image);
        titleEdit = findViewById(R.id.detail_edit_title);
        timeEdit = findViewById(R.id.detail_edit_time);
        tagEdit = findViewById(R.id.detail_edit_tag);
        phoneEdit = findViewById(R.id.detail_edit_phone);
        descriptionEdit = findViewById(R.id.detail_edit_description);

        titleEdit.setText(spotBean.getTitle());
        timeEdit.setText(spotBean.getTime());
        tagEdit.setText(spotBean.getTag());
        phoneEdit.setText(spotBean.getPhone());
        descriptionEdit.setText(spotBean.getDescription());

        findViewById(R.id.detail_edit_rotate_image).setOnClickListener(this);
        mainImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_edit_main_image:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                break;
            case R.id.detail_edit_rotate_image:
                rotateInfoImage();
                break;
            case R.id.detail_edit_save:
                saveDataToServer();
                break;
            default:
        }
    }

    private void rotateInfoImage() {
        if (bmp == null) {
            return;
        }

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .format(DecodeFormat.DEFAULT)
                .error(R.drawable.placeholder);

        loadingDialog.show();
        new BitmapRotate(bmp, (BitmapRotate.callback) bitmap -> {
            runOnUiThread(() -> {
                bmp.recycle();
                bmp = bitmap;
                Glide.with(DetailEditActivity.this).load(bmp).apply(options).into(mainImage);
                loadingDialog.dismiss();
            });
        }).start();
        loadingDialog.dismiss();
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

                    Logger.d(String.valueOf(bmp.getHeight()));
                    Logger.d(String.valueOf(bmp.getWidth()));
                    mainImage.setImageBitmap(bmp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 데이터 업로드
    private void saveDataToServer() {
        loadingDialog.show();

        spotBean.setTitle(titleEdit.getText().toString());
        spotBean.setTime(timeEdit.getText().toString());
        spotBean.setPhone(phoneEdit.getText().toString());
        spotBean.setTag(tagEdit.getText().toString());
        spotBean.setDescription(descriptionEdit.getText().toString());

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
                Snackbar.make(findViewById(R.id.detail_edit_container), message, Snackbar.LENGTH_SHORT).show();
            }
        }).start();
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
