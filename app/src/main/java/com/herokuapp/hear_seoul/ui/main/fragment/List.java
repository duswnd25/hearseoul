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


    }

    @Subscribe
    @SuppressWarnings("unused")
    public void LocationChangeEvent(LocationChangeEvent event) {
        location = event.getLocation();
        fetchLandmark();
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