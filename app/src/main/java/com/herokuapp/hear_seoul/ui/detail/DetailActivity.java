/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.data.FetchInfoById;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Utils;

import java.util.Objects;

import me.grantland.widget.AutofitTextView;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, FetchInfoById.callback {
    private SpotBean spotBean;
    private boolean isNewInformation = true;
    private ProgressDialog loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        spotBean = getIntent().getParcelableExtra(Const.INTENT_EXTRA.SPOT);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.detail_collapsing_toolbar_layout);
        AppBarLayout appBarLayout = findViewById(R.id.detail_appbar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(spotBean.getTitle());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        findViewById(R.id.detail_edit).setOnClickListener(this);

        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage(getString(R.string.loading));
        loadingProgress.show();

        new FetchInfoById(spotBean.getId(), this).start();
    }

    @Override
    public void onInfoFetchSuccess(boolean isExist, SpotBean result) {
        loadingProgress.dismiss();
        if (isExist) {
            spotBean = result;
            isNewInformation = false;
        }

        ImageView mainImageView = findViewById(R.id.detail_main_image);
        AutofitTextView titleView = findViewById(R.id.detail_title);
        AutofitTextView tagView = findViewById(R.id.detail_tag);
        AutofitTextView timeView = findViewById(R.id.detail_time);
        AutofitTextView phoneView = findViewById(R.id.detail_phone);

        titleView.setText(spotBean.getTitle());
        tagView.setText(spotBean.getTag());
        timeView.setText(spotBean.getTime());
        phoneView.setText(spotBean.getPhone());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .format(DecodeFormat.DEFAULT)
                .error(R.drawable.placeholder);

        Glide.with(DetailActivity.this).asBitmap().load(spotBean.getImgSrc()).apply(options).thumbnail(0.4f).into(mainImageView);
    }

    @Override
    public void onInfoFetchFail(String message) {
        loadingProgress.dismiss();
        Utils.showStyleToast(DetailActivity.this, "오류가 발생했습니다.");
        finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_edit:
                Intent intent = new Intent(this, DetailEditActivity.class);
                intent.putExtra(Const.INTENT_EXTRA.SPOT, spotBean);
                intent.putExtra(Const.INTENT_EXTRA.IS_NEW_INFORMATION, isNewInformation);
                startActivity(intent);
                break;
        }
    }
}
