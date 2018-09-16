/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.detail;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.data.UpdateInfo;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import gun0912.tedbottompicker.TedBottomPicker;

public class DetailEditActivity extends AppCompatActivity implements View.OnClickListener, TedBottomPicker.OnMultiImageSelectedListener {

    private LinkedList<ImageView> imageViewList = new LinkedList<>();
    private ProgressDialog loadingDialog;
    private boolean isNewInformation, isImageChange = false;
    private SpotBean spotBean;
    private ImageView mainImage;
    private EditText titleEdit, timeEdit, tagEdit, phoneEdit, descriptionEdit;
    private RequestOptions glideOptions = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .format(DecodeFormat.DEFAULT)
            .error(R.drawable.placeholder);

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

        Glide.with(this).asBitmap().load(spotBean.getImgSrc()).apply(glideOptions).thumbnail(0.4f).into(mainImage);

        findViewById(R.id.detail_edit_rotate_image).setOnClickListener(this);
        findViewById(R.id.detail_edit_save).setOnClickListener(this);
        mainImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_edit_main_image:
                isImageChange = true;
                selectImage();
                break;
            case R.id.detail_edit_rotate_image:
                break;
            case R.id.detail_edit_save:
                loadingDialog.show();
                spotBean.setTitle(titleEdit.getText().toString());
                spotBean.setTime(timeEdit.getText().toString());
                spotBean.setPhone(phoneEdit.getText().toString());
                spotBean.setTag(tagEdit.getText().toString());
                spotBean.setDescription(descriptionEdit.getText().toString());

                /*
                if (isImageChange) {
                    new BaasImageManager().uploadImage(spotBean.getId(), bitmap, new BaasImageManager.uploadCallback() {
                        @Override
                        public void onImageUploadSuccess(String url) {
                            spotBean.setImgSrc(url);
                            saveDataToServer();
                        }

                        @Override
                        public void onImageUploadFail(String message) {
                            Utils.showStyleToast(DetailEditActivity.this, "실패");
                        }
                    });
                } else {
                    saveDataToServer();
                }
                */
                break;
            default:
        }
    }

    private void selectImage() {
        // 권한 체크
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(DetailEditActivity.this)
                                .setOnMultiImageSelectedListener(DetailEditActivity.this)
                                .setPeekHeight(1600)
                                .showTitle(false)
                                .setCompleteButtonText("Done")
                                .setSelectMaxCount(5)
                                .setEmptySelectionText("No Select")
                                .create();
                        bottomSheetDialogFragment.show(getSupportFragmentManager());
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Utils.showStyleToast(DetailEditActivity.this, getString(R.string.permission_deny_description));
                    }
                })
                .setRationaleMessage(getString(R.string.storage_permission_description))
                .setDeniedMessage(getString(R.string.permission_deny_description))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    // 데이터 업로드
    private void saveDataToServer() {
        new UpdateInfo(spotBean, isNewInformation, new UpdateInfo.callback() {
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

    // 이미지 선택
    @Override
    public void onImagesSelected(ArrayList<Uri> uriList) {
        imageViewList.clear();
        try {
            for (Uri current : uriList) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), current);
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(this).asBitmap().load(bitmap).apply(glideOptions).thumbnail(0.4f).into(imageView);
                imageViewList.add(imageView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinearLayout imageContainer = findViewById(R.id.detail_edit_more_image_container);
        imageContainer.removeAllViews();
        for (ImageView imageView : imageViewList) {
            imageContainer.addView(imageView);
        }
    }
}
