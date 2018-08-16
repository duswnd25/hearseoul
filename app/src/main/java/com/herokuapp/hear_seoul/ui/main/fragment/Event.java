package com.herokuapp.hear_seoul.ui.main.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.EventBean;
import com.herokuapp.hear_seoul.controller.main.FetchEvent;
import com.herokuapp.hear_seoul.controller.main.EventListAdapter;
import com.yalantis.taurus.PullToRefreshView;

import java.util.LinkedList;
import java.util.Objects;


public class Event extends Fragment {

    private LinkedList<EventBean> eventList = new LinkedList<>();
    private EventListAdapter eventListAdapter;
    private PullToRefreshView pullToRefreshView;
    private View view;

    public Event() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        ShimmerRecyclerView eventListView = view.findViewById(R.id.fragment_recycler_common);
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
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchEvent();
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fetchEvent();
    }

    private void fetchEvent() {
        final ShimmerRecyclerView shimmerRecyclerView = view.findViewById(R.id.fragment_recycler_common);
        shimmerRecyclerView.showShimmerAdapter();
        new FetchEvent(new FetchEvent.SuccessCallback() {
            @Override
            public void successCallback(LinkedList<EventBean> results) {
                eventList.clear();
                eventList.addAll(results);
                eventListAdapter.notifyDataSetChanged();
                pullToRefreshView.setRefreshing(false);
                shimmerRecyclerView.hideShimmerAdapter();
            }
        }, new FetchEvent.FailCallback() {
            @Override
            public void failCallback(LinkedList<EventBean> results) {

            }
        }).execute(Objects.requireNonNull(getActivity()).getApplicationContext());
    }
}