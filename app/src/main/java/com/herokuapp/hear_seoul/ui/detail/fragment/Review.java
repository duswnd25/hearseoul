package com.herokuapp.hear_seoul.ui.detail.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.ReviewBean;
import com.herokuapp.hear_seoul.controller.detail.FetchReview;
import com.herokuapp.hear_seoul.controller.detail.ReviewListAdapter;

import java.util.LinkedList;


public class Review extends Fragment {

    // 더미 데이터
    private LinkedList<ReviewBean> result = new LinkedList<>();

    public Review() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.template_recycler_with_refresher, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView noticeListView = view.findViewById(R.id.fragment_recycler_common);
        noticeListView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                mLayoutManager.getOrientation());

        noticeListView.addItemDecoration(dividerItemDecoration);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        noticeListView.setLayoutManager(mLayoutManager);


        new FetchReview(new FetchReview.SuccessCallback() {
            @Override
            public void successCallback(LinkedList<ReviewBean> results) {
                result.addAll(results);
            }
        }, new FetchReview.FailCallback() {
            @Override
            public void failCallback(LinkedList<ReviewBean> results) {

            }
        });

        ReviewListAdapter contactAdapter = new ReviewListAdapter(getActivity(), result);
        noticeListView.setAdapter(contactAdapter);
    }
}