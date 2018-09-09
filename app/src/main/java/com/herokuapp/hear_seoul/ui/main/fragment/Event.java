/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.EventBean;
import com.herokuapp.hear_seoul.controller.data.FetchEvent;
import com.herokuapp.hear_seoul.controller.main.EventListAdapter;
import com.yalantis.taurus.PullToRefreshView;

import java.util.LinkedList;
import java.util.Objects;


public class Event extends Fragment implements FetchEvent.callback {

    private LinkedList<EventBean> eventList = new LinkedList<>();
    private EventListAdapter eventListAdapter;
    private PullToRefreshView pullToRefreshView;

    public Event() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.template_recycler_with_refresher, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ShimmerRecyclerView eventListView = view.findViewById(R.id.template_recycler_common);
        eventListView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Objects.requireNonNull(getContext()),
                mLayoutManager.getOrientation());

        eventListView.addItemDecoration(dividerItemDecoration);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        eventListView.setLayoutManager(mLayoutManager);

        eventListAdapter = new EventListAdapter(getActivity(), eventList);
        eventListView.setAdapter(eventListAdapter);

        pullToRefreshView = view.findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setOnRefreshListener(this::fetchEvent);

        fetchEvent();
    }

    private void fetchEvent() {
        new FetchEvent(getString(R.string.seoul_event_key), this).start();
    }

    @Override
    public void onEventFetchSuccess(LinkedList<EventBean> result) {
        eventList.clear();
        eventList.addAll(result);
        eventListAdapter.notifyDataSetChanged();
        pullToRefreshView.setRefreshing(false);
    }

    @Override
    public void onEventFetchFail(String message) {
        fetchEvent();
    }
}