/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.detail.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.ui.detail.DetailActivity;

import java.util.Objects;


public class Info extends Fragment implements View.OnClickListener {
    private SpotBean spotBean;

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

        spotBean = Objects.requireNonNull(getActivity()).getIntent().getParcelableExtra(Const.INTENT_EXTRA.LOCATION);
        Objects.requireNonNull(((DetailActivity) getActivity()).getSupportActionBar()).setTitle(spotBean.getTitle());
        FloatingActionButton fab = view.findViewById(R.id.detail_edit);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_edit:
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(Const.INTENT_EXTRA.LOCATION, spotBean);
                startActivity(intent);
                break;
            default:
        }
    }
}