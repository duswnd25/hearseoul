/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.detail.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.data.FetchInfoById;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.ui.detail.DetailActivity;
import com.herokuapp.hear_seoul.ui.detail.DetailEditActivity;

import java.util.Objects;

import me.grantland.widget.AutofitTextView;


public class Info extends Fragment implements View.OnClickListener, FetchInfoById.callback {
    private SpotBean spotBean;
    private View view;
    private ActionBar actionBar;
    private Context context;
    private boolean isNew;

    public Info() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        this.actionBar = ((DetailActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        this.context = getContext();

        spotBean = Objects.requireNonNull(getActivity()).getIntent().getParcelableExtra(Const.INTENT_EXTRA.SPOT);

        // FAB
        FloatingActionButton fab = view.findViewById(R.id.detail_edit);
        fab.setOnClickListener(this);

        new FetchInfoById(spotBean.getId(), this).start();
    }

    @SuppressLint("CheckResult")
    private void initViewData() {
        actionBar.setTitle(spotBean.getTitle());
        actionBar.setSubtitle(spotBean.getAddress());

        // 뷰 데이터 설정
        ImageView infoImageVIew = view.findViewById(R.id.detail_image);

        // 이미지
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(context).load(spotBean.getImgSrc().equals("NO") ? R.drawable.placeholder : spotBean.getImgSrc()).apply(options).into(infoImageVIew);

        // 영업시간
        AutofitTextView infoTimeView = view.findViewById(R.id.detail_time);
        infoTimeView.setText((spotBean.getTime()).equals("NO") ? getString(R.string.no_data) : spotBean.getTime());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_edit:
                Intent intent = new Intent(getContext(), DetailEditActivity.class);
                intent.putExtra(Const.INTENT_EXTRA.SPOT, spotBean);
                intent.putExtra(Const.INTENT_EXTRA.IS_NEW_INFORMATION, isNew);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public void onInfoFetchSuccess(boolean isExist, SpotBean result) {
        this.isNew = !isExist;
        if (isExist && result != null) {
            Info.this.spotBean = result;
        }
        initViewData();
    }

    @Override
    public void onInfoFetchFail(String message) {

    }
}