package com.herokuapp.hear_seoul.ui.main.fragment;

import android.annotation.SuppressLint;
import android.location.Location;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.main.FetchSpot;
import com.herokuapp.hear_seoul.controller.main.SpotListAdapter;
import com.herokuapp.hear_seoul.core.otto.LocationChangeEvent;
import com.squareup.otto.Subscribe;
import com.yalantis.taurus.PullToRefreshView;

import java.util.LinkedList;
import java.util.Objects;


public class List extends Fragment implements OnSuccessListener<Location> {

    private LinkedList<SpotBean> spotList = new LinkedList<>();
    private SpotListAdapter spotListAdapter;
    private View view;
    private Location location;
    private PullToRefreshView pullToRefreshView;

    public List() {
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

        ShimmerRecyclerView noticeListView = view.findViewById(R.id.fragment_recycler_common);
        noticeListView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Objects.requireNonNull(getContext()),
                mLayoutManager.getOrientation());

        noticeListView.addItemDecoration(dividerItemDecoration);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        noticeListView.setLayoutManager(mLayoutManager);

        spotListAdapter = new SpotListAdapter(getActivity(), spotList);
        noticeListView.setAdapter(spotListAdapter);
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        mFusedLocationClient.getLastLocation().addOnSuccessListener(Objects.requireNonNull(getActivity()), this);

        pullToRefreshView = view.findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchLandmark();
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
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void LocationChangeEvent(LocationChangeEvent event) {
        location = event.getLocation();
        fetchLandmark();
    }

    private void fetchLandmark() {
        if (location != null) {
            final ShimmerRecyclerView shimmerRecyclerView = view.findViewById(R.id.fragment_recycler_common);
            shimmerRecyclerView.showShimmerAdapter();

            new FetchSpot(Objects.requireNonNull(getActivity()).getApplicationContext(), new FetchSpot.SuccessCallback() {
                @Override
                public void successCallback(LinkedList<SpotBean> results) {
                    view.findViewById(R.id.empty_view).setVisibility(View.GONE);
                    spotList.clear();
                    spotList.addAll(results);
                    spotListAdapter.notifyDataSetChanged();
                    shimmerRecyclerView.hideShimmerAdapter();
                    pullToRefreshView.setRefreshing(false);
                }
            }, new FetchSpot.FailCallback() {
                @Override
                public void failCallback(LinkedList<SpotBean> results) {

                }
            }).execute(location);
        } else {
            pullToRefreshView.setRefreshing(false);
        }
    }

    // GPS 좌표 가져오기
    @Override
    public void onSuccess(Location location) {
        this.location = location;
        if (location != null) {
            fetchLandmark();
        }
    }
}