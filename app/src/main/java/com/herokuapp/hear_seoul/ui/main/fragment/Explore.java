/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.baas.FetchSpotList;
import com.herokuapp.hear_seoul.controller.main.ExploreAdapter;

import java.util.LinkedList;


public class Explore extends Fragment implements FetchSpotList.OnFetchSpotListCallback {

    private LinkedList<SpotBean> dataOrigin = new LinkedList<>();
    private ExploreAdapter adapter;
    private Context context;

    public Explore() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.explore_recycler);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new ExploreAdapter(getContext(), dataOrigin);
        recyclerView.setAdapter(adapter);

        new FetchSpotList(context, this).getData();
    }

    @Override
    public void onDataFetchSuccess(LinkedList<SpotBean> result) {
        if (result.size() == 0) {
            new FetchSpotList(context, this).getData();
        }
        dataOrigin.clear();
        dataOrigin.addAll(result);
        adapter.notifyDataSetChanged();
    }
}