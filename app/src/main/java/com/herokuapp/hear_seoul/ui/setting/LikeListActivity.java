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
import android.view.MenuItem;

import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.controller.baas.FetchLikeList;
import com.herokuapp.hear_seoul.controller.db.DBManager;
import com.herokuapp.hear_seoul.controller.main.ExploreAdapter;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;

import java.util.LinkedList;
import java.util.Objects;

public class LikeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.bookmark_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        LinkedList<SpotBean> dataOrigin = new LinkedList<>();
        LinkedList<String> likeList = new DBManager(this, Const.DB.DB_NAME, null, Const.DB.VERSION).getLikeList();

        RecyclerView recyclerView = findViewById(R.id.bookmark_recycler);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        ExploreAdapter adapter = new ExploreAdapter(this, dataOrigin);
        recyclerView.setAdapter(adapter);

        new FetchLikeList(this, (FetchLikeList.OnFetchLikeListCallback) result -> {
            dataOrigin.addAll(result);
            Logger.d(String.valueOf(result.size()));
            adapter.notifyDataSetChanged();
        }).getData(likeList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
