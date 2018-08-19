package com.herokuapp.hear_seoul.ui.detail.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.ui.detail.DetailEditActivity;

import java.util.Objects;


public class Main extends Fragment {

    public Main() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SpotBean spotBean = (SpotBean) Objects.requireNonNull(getActivity()).getIntent().getSerializableExtra(Const.INTENT_EXTRA.LOCATION);
        Log.e("TEST", spotBean.getTitle());
        FloatingActionButton fab = view.findViewById(R.id.detail_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DetailEditActivity.class));
            }
        });
    }
}