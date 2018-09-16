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
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
import com.herokuapp.hear_seoul.controller.data.BaasImageManager;
import com.herokuapp.hear_seoul.controller.data.UpdateInfo;
import com.herokuapp.hear_seoul.controller.detail.EditImageAdapter;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.herokuapp.hear_seoul.core.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import gun0912.tedbottompicker.TedBottomPicker;
import me.relex.circleindicator.CircleIndicator;

public class DetailEditActivity extends AppCompatActivity implements View.OnClickListener, TedBottomPicker.OnMultiImageSelectedListener {

    private ProgressDialog loadingDialog;
    private CircleIndicator indicator;
    private SpotBean spotBean;
    private LinkedList<Bitmap> imageList = new LinkedList<>();
    private boolean isNewInformation, isImageChange = false;
    private ViewPager viewPager;
    private EditImageAdapter adapter;
    private EditText titleEdit, timeEdit, tagEdit, phoneEdit, descriptionEdit, addressEdit;
    private ArrayList<String> originalImageSrc = new ArrayList<>();
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
        // 이미지 뷰 페이저
        viewPager = findViewById(R.id.detail_edit_image_pager);
        adapter = new EditImageAdapter(this, imageList);
        viewPager.setAdapter(adapter);

        // 이미지 인디케이터
        indicator = findViewById(R.id.detail_edit_indicator);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());

        // content editor
        titleEdit = findViewById(R.id.detail_edit_title);
        timeEdit = findViewById(R.id.detail_edit_time);
        tagEdit = findViewById(R.id.detail_edit_tag);
        phoneEdit = findViewById(R.id.detail_edit_phone);
        descriptionEdit = findViewById(R.id.detail_edit_description);
        addressEdit = findViewById(R.id.detail_edit_address);

        // 값 초기화
        titleEdit.setText(spotBean.getTitle());
        timeEdit.setText(spotBean.getTime());
        tagEdit.setText(spotBean.getTag());
        phoneEdit.setText(spotBean.getPhone());
        descriptionEdit.setText(spotBean.getDescription());
        addressEdit.setText(spotBean.getAddress());

        if (!isNewInformation) {
            originalImageSrc.addAll(spotBean.getImgUrlList());
            for (String url : spotBean.getImgUrlList()) {
                Glide.with(this).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        imageList.add(resource);
                        adapter.notifyDataSetChanged();
                        return false;
                    }
                }).apply(glideOptions).submit();
            }
        }

        // Click 이벤트
        findViewById(R.id.detail_edit_add_image).setOnClickListener(this);
        findViewById(R.id.detail_edit_save).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_edit_add_image:
                isImageChange = true;
                selectImage();
                break;
            case R.id.detail_edit_save:
                loadingDialog.show();
                spotBean.setTitle(titleEdit.getText().toString());
                spotBean.setTime(timeEdit.getText().toString());
                spotBean.setPhone(phoneEdit.getText().toString());
                spotBean.setTag(tagEdit.getText().toString());
                spotBean.setDescription(descriptionEdit.getText().toString());
                spotBean.setAddress(addressEdit.getText().toString());

                if (isImageChange) {
                    new BaasImageManager().uploadImage(spotBean.getId(), imageList, new BaasImageManager.uploadCallback() {
                        @Override
                        public void onImageUploadSuccess(ArrayList<String> url) {
                            spotBean.setImgUrlList(url);
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
                                .showTitle(true)
                                .showCameraTile(false)
                                .setCompleteButtonText("Done")
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
        new UpdateInfo(spotBean, originalImageSrc, isNewInformation, isImageChange, new UpdateInfo.callback() {
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
        loadingDialog.show();
        try {
            imageList.clear();
            for (Uri current : uriList) {
                Bitmap tempBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), current);

                int rotateImage = getCameraPhotoOrientation(current);
                Logger.d(String.valueOf(rotateImage));
                if (rotateImage != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotateImage);
                    tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);
                }

                imageList.add(tempBitmap);
            }
        } catch (IOException e) {
            Logger.e(e.getMessage());
        }
        loadingDialog.dismiss();
        reInitAdapter();
    }

    private void reInitAdapter() {
        adapter = null;
        adapter = new EditImageAdapter(this, imageList);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());
    }

    public int getCameraPhotoOrientation(Uri imageUri) {
        int rotate = 0;
        try {
            File imageFile = new File(imageUri.getPath());

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
}
