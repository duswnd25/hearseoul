/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.data.FetchInfoById;
import com.herokuapp.hear_seoul.controller.detail.InfoImageAdapter;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Utils;

import java.util.Objects;

import me.grantland.widget.AutofitTextView;
import me.relex.circleindicator.CircleIndicator;

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

        Drawable navIcon = toolbar.getNavigationIcon();

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
                if (scrollRange + verticalOffset <= 145) {
                    Objects.requireNonNull(navIcon).setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                    collapsingToolbarLayout.setTitle(spotBean.getTitle());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    Objects.requireNonNull(navIcon).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    isShow = false;
                }
            }
        });

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

        AutofitTextView titleView = findViewById(R.id.detail_title);
        AutofitTextView tagView = findViewById(R.id.detail_tag);
        AutofitTextView timeView = findViewById(R.id.detail_time);
        AutofitTextView phoneView = findViewById(R.id.detail_phone);
        AutofitTextView addressView = findViewById(R.id.detail_address);

        titleView.setText(spotBean.getTitle());
        tagView.setText(spotBean.getTag());
        timeView.setText(spotBean.getTime());
        phoneView.setText(spotBean.getPhone());
        addressView.setText(spotBean.getAddress());

        // 이미지 뷰 페이저
        ViewPager viewPager = findViewById(R.id.detail_image_pager);
        InfoImageAdapter adapter = new InfoImageAdapter(this, spotBean.getImgUrlList());
        viewPager.setAdapter(adapter);

        // 이미지 인디케이터
        CircleIndicator indicator = findViewById(R.id.detail_indicator);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());

        if (isExist) {

        }

        findViewById(R.id.detail_correction).setOnClickListener(this);
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
            case R.id.detail_correction:
                Intent intent = new Intent(this, DetailEditActivity.class);
                intent.putExtra(Const.INTENT_EXTRA.SPOT, spotBean);
                intent.putExtra(Const.INTENT_EXTRA.IS_NEW_INFORMATION, isNewInformation);
                startActivity(intent);
                finish();
                break;
        }
    }
}
