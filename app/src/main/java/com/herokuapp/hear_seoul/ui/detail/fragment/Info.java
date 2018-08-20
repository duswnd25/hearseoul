/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.detail.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.ui.detail.DetailActivity;
import com.herokuapp.hear_seoul.ui.detail.DetailEditActivity;

import java.util.Objects;

import me.grantland.widget.AutofitTextView;


public class Info extends Fragment implements View.OnClickListener {
    private SpotBean spotBean;

    public Info() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_view, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spotBean = Objects.requireNonNull(getActivity()).getIntent().getParcelableExtra(Const.INTENT_EXTRA.SPOT);
        Objects.requireNonNull(((DetailActivity) getActivity()).getSupportActionBar()).setTitle(spotBean.getTitle());

        // 뷰 데이터 설정
        ImageView infoImageVIew = view.findViewById(R.id.detail_image);

        // 이미지
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(getActivity()).load(spotBean.getImgSrc().equals("NO") ? R.drawable.placeholder : spotBean.getImgSrc()).apply(options).into(infoImageVIew);

        // 영업시간
        AutofitTextView infoTimeView = view.findViewById(R.id.detail_time);
        infoTimeView.setText((spotBean.getTime()).equals("NO") ? getString(R.string.no_data) : spotBean.getTime());

        // FAB
        FloatingActionButton fab = view.findViewById(R.id.detail_edit);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_edit:
                Intent intent = new Intent(getContext(), DetailEditActivity.class);
                intent.putExtra(Const.INTENT_EXTRA.LOCATION, spotBean);
                startActivity(intent);
                break;
            default:
        }
    }
}