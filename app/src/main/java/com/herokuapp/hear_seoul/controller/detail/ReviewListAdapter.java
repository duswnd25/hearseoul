package com.herokuapp.hear_seoul.controller.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.ReviewBean;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.ui.detail.DetailActivity;
import com.herokuapp.hear_seoul.ui.detail.fragment.Review;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private LinkedList<ReviewBean> itemList;

    public ReviewListAdapter(Context context, LinkedList<ReviewBean> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spot_list, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);

        }
    }
}