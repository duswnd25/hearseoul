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
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.main.SpotListAdapter;

import java.util.LinkedList;


public class List extends Fragment {

    public List() {
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

        // 더미 데이터
        LinkedList<SpotBean> result = new LinkedList<>();
        SpotBean temp = new SpotBean();
        temp.setTitle("장소");
        temp.setDescription("설명");
        result.add(temp);
        temp.setTitle("장소 2");
        temp.setDescription("설명 2");
        result.add(temp);

        SpotListAdapter contactAdapter = new SpotListAdapter(getActivity(), result);
        noticeListView.setAdapter(contactAdapter);

    }
}