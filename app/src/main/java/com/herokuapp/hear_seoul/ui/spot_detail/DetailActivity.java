/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.spot_detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.baas.FetchInfoById;
import com.herokuapp.hear_seoul.controller.db.DBManager;
import com.herokuapp.hear_seoul.controller.detail.InfoImageAdapter;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Utils;

import java.util.Objects;

import me.grantland.widget.AutofitTextView;
import me.relex.circleindicator.CircleIndicator;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, FetchInfoById.callback {
    private SpotBean spotBean;
    private boolean isNewInformation = true, isUserLikeSpot = false;
    private ProgressDialog loadingProgress;
    private FloatingActionButton likeView;

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

        AutofitTextView titleView = findViewById(R.id.detail_title);
        AutofitTextView timeView = findViewById(R.id.detail_time);
        AutofitTextView phoneView = findViewById(R.id.detail_phone);
        AutofitTextView addressView = findViewById(R.id.detail_address);
        AutofitTextView distanceView = findViewById(R.id.detail_distance);
        TextView descriptionView = findViewById(R.id.detail_description);
        likeView = findViewById(R.id.detail_like);

        if (isExist) {
            spotBean = result;
            isNewInformation = false;

            DBManager dbManager = new DBManager(this, Const.DB.DB_NAME, null, Const.DB.VERSION);
            isUserLikeSpot = dbManager.isUserLikeThisSpot(spotBean.getId());

            // 이미지 뷰 페이저
            ViewPager viewPager = findViewById(R.id.detail_image_pager);
            InfoImageAdapter adapter = new InfoImageAdapter(this, spotBean.getImgUrlList());
            viewPager.setAdapter(adapter);

            // 이미지 인디케이터
            CircleIndicator indicator = findViewById(R.id.detail_indicator);
            indicator.setViewPager(viewPager);
            adapter.registerDataSetObserver(indicator.getDataSetObserver());
            descriptionView.setText(spotBean.getDescription());
        }

        titleView.setText(spotBean.getTitle());
        timeView.setText(spotBean.getTime());
        phoneView.setText(spotBean.getPhone());
        addressView.setText(spotBean.getAddress());
        String distanceValue = Utils.getDistanceFromCurrentLocation(this, spotBean.getLocation()) + "km";
        distanceView.setText(distanceValue);

        likeView.setImageResource(isUserLikeSpot ? R.drawable.ic_like_fill : R.drawable.ic_like_blank);

        String tagText;

        switch (spotBean.getTag()) {
            case 1:
                tagText = getString(R.string.tag_food);
                break;
            case 2:
                tagText = getString(R.string.tag_cafe);
                break;
            case 3:
                tagText = getString(R.string.tag_landmark);
                break;
            case 4:
                tagText = getString(R.string.tag_show);
                break;
            case 5:
                tagText = getString(R.string.tag_photo);
                break;
            default:
                tagText = getString(R.string.tag_no);
        }
        ((TextView) findViewById(R.id.detail_tag_text)).setText(tagText);

        likeView.setOnClickListener(this);
        phoneView.setOnClickListener(this);
        addressView.setOnClickListener(this);
        findViewById(R.id.detail_correction).setOnClickListener(this);
        findViewById(R.id.detail_share).setOnClickListener(this);
    }

    @Override
    public void onInfoFetchFail(String message) {
        loadingProgress.dismiss();
        Utils.showStyleToast(DetailActivity.this, getString(R.string.error));
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
            case R.id.detail_phone:
                String phoneNumber = spotBean.getPhone();
                if (phoneNumber.contains("+82")) {
                    phoneNumber = phoneNumber.replace("+82", "0");
                }
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
                break;
            case R.id.detail_like:
                if (isNewInformation) {
                    Utils.showStyleToast(DetailActivity.this, getString(R.string.please_insert_information_first));
                } else {
                    isUserLikeSpot = !isUserLikeSpot;
                    DBManager dbManager = new DBManager(DetailActivity.this, Const.DB.DB_NAME, null, Const.DB.VERSION);
                    dbManager.updateSpotLikeState(spotBean.getId(), isUserLikeSpot);
                    likeView.setImageResource(isUserLikeSpot ? R.drawable.ic_like_fill : R.drawable.ic_like_blank);
                }
                break;
            case R.id.detail_share:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(spotBean.getTitle());
                stringBuilder.append("\n");
                stringBuilder.append("tel : ");
                stringBuilder.append(spotBean.getPhone());
                stringBuilder.append("\n");
                stringBuilder.append("address : ");
                stringBuilder.append(spotBean.getAddress());

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[" + getString(R.string.app_name) + "]");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, stringBuilder.toString());
                startActivity(Intent.createChooser(sharingIntent, "select"));
                break;
            case R.id.detail_address:
                String uri = "geo:" + spotBean.getLocation().latitude + ","
                        + spotBean.getLocation().longitude + "?q=" + spotBean.getTitle() + " " + spotBean.getAddress();
                startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri)));
                break;
            default:
        }
    }
}
