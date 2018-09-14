/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.data.FetchInfoById;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Utils;

import java.util.Objects;

import me.grantland.widget.AutofitTextView;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private SpotBean spotBean;
    private boolean isNewInformation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        spotBean = getIntent().getParcelableExtra(Const.INTENT_EXTRA.SPOT);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.main_collapsing_toolbar_layout);
        AppBarLayout appBarLayout = findViewById(R.id.main_appbar_layout);
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

        ImageView mainImageView = findViewById(R.id.detail_main_image);
        TextView titleView = findViewById(R.id.detail_title);
        TextView descriptionView = findViewById(R.id.detail_description);
        AutofitTextView tagView = findViewById(R.id.detail_tag);
        AutofitTextView timeVIew = findViewById(R.id.detail_time);
        AutofitTextView priceView = findViewById(R.id.detail_price);
        AutofitTextView phoneVIew = findViewById(R.id.detail_phone);

        findViewById(R.id.detail_edit).setOnClickListener(this);

        ProgressDialog loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage(getString(R.string.loading));
        loadingProgress.show();

        new FetchInfoById(spotBean.getId(), new FetchInfoById.callback() {
            @Override
            public void onInfoFetchSuccess(boolean isExist, SpotBean result) {
                loadingProgress.dismiss();
                if (isExist) {
                    spotBean = result;
                    isNewInformation = false;
                }
                titleView.setText(spotBean.getTitle());
                descriptionView.setText(spotBean.getDescription());
                timeVIew.setText(spotBean.getTitle());

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .format(DecodeFormat.DEFAULT)
                        .error(R.drawable.placeholder);

                Glide.with(DetailActivity.this).asBitmap().load(spotBean.getImgSrc()).apply(options).thumbnail(0.4f).listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Palette.from(resource).generate(p -> {
                            Palette.Swatch vibrantSwatch = null;
                            if (p != null) {
                                vibrantSwatch = p.getVibrantSwatch();
                            }

                            if (vibrantSwatch != null) {
                                tagView.setBackgroundColor(vibrantSwatch.getRgb());
                                tagView.setTextColor(vibrantSwatch.getBodyTextColor());
                            }
                        });
                        return false;
                    }
                }).into(mainImageView);
            }

            @Override
            public void onInfoFetchFail(String message) {
                loadingProgress.dismiss();
                Utils.showStyleToast(DetailActivity.this, "오류가 발생했습니다.");
                finish();
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
