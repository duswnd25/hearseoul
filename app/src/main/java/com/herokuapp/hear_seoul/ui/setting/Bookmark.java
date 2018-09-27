/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.baas.query.FetchLikeList;
import com.herokuapp.hear_seoul.controller.main.ExploreAdapter;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.DBManager;

import java.util.LinkedList;

public class Bookmark extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        LinkedList<SpotBean> dataOrigin = new LinkedList<>();
        LinkedList<String> likeList = new DBManager(this, Const.DB.DB_NAME, null, Const.DB.VERSION).getLikeList();

        RecyclerView recyclerView = findViewById(R.id.explore_recycler);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        ExploreAdapter adapter = new ExploreAdapter(this, dataOrigin);
        recyclerView.setAdapter(adapter);

        new FetchLikeList(this, (FetchLikeList.OnFetchLikeListCallback) result -> adapter.notifyDataSetChanged()).getData(likeList);
    }
}
