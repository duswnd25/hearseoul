/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.main.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.EventBean;
import com.herokuapp.hear_seoul.controller.main.EventListAdapter;
import com.herokuapp.hear_seoul.controller.public_data.FetchEvent;
import com.herokuapp.hear_seoul.core.Logger;

import java.util.LinkedList;
import java.util.Objects;


public class Event extends Fragment implements FetchEvent.callback {

    private LinkedList<EventBean> eventList = new LinkedList<>();
    private EventListAdapter eventListAdapter;
    private Context context;

    public Event() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView eventListView = view.findViewById(R.id.event_recycler);
        eventListView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Objects.requireNonNull(context),
                mLayoutManager.getOrientation());

        eventListView.addItemDecoration(dividerItemDecoration);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        eventListView.setLayoutManager(mLayoutManager);

        eventListAdapter = new EventListAdapter(getActivity(), eventList);
        eventListView.setAdapter(eventListAdapter);

        fetchEvent();
    }

    private void fetchEvent() {
        new FetchEvent(context, this).execute();
    }

    @Override
    public void onEventFetchSuccess(LinkedList<EventBean> result) {
        eventList.clear();
        eventList.addAll(result);
        Logger.d(String.valueOf(eventList.size()));
        eventListAdapter.notifyDataSetChanged();
    }
}