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
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.baas.FetchSuggestionList;
import com.herokuapp.hear_seoul.controller.baas.FetchSuggestionSpotList;
import com.herokuapp.hear_seoul.controller.db.DBManager;
import com.herokuapp.hear_seoul.controller.main.SuggestionAdapter;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Utils;
import com.herokuapp.hear_seoul.ui.main.MainActivity;

import java.util.LinkedList;

import me.relex.circleindicator.CircleIndicator;

public class Suggestion extends Fragment {
    private LinkedList<SpotBean> dataOrigin = new LinkedList<>();
    private SuggestionAdapter adapter;
    private Context context;

    public Suggestion() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggestion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.main_pager);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(80, 60, 60, 40);
        viewPager.setPageMargin(20);

        adapter = new SuggestionAdapter(context, dataOrigin);
        viewPager.setAdapter(adapter);

        CircleIndicator indicator = view.findViewById(R.id.main_indicator);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());

        getData();
    }

    private void getData() {
        LinkedList<String> likeSpotList = new DBManager(context, Const.DB.DB_NAME, null, Const.DB.VERSION).getLikeList();

        new FetchSuggestionList(context, (FetchSuggestionList.OnFetchSuggestionListCallback) new FetchSuggestionSpotList(context, (FetchSuggestionSpotList.OnFetchSuggestionSpotListCallback) result1 -> {
            if (result1.size() == 0) {
                Utils.showStyleToast(context, getString(R.string.no_place_to_suggest));
                ((MainActivity) context).changeNavigationSelected(R.id.menu_explore);
            }
            dataOrigin.clear();
            dataOrigin.addAll(result1);
            adapter.notifyDataSetChanged();
        })::getData).getData(likeSpotList);
    }
}