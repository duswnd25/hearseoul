/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.ui.spot_detail.DetailActivity;
import com.lid.lib.LabelImageView;

import java.util.LinkedList;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {

    private LinkedList<SpotBean> itemList;
    private Context context;

    public ExploreAdapter(Context context, LinkedList<SpotBean> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint({"CheckResult"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.title.setText(itemList.get(position).getTitle());
        holder.description.setText(itemList.get(position).getDescription());

        if (itemList.get(position).isInfluencer()) {
            holder.image.setLabelVisual(true);
            holder.image.setLabelText("VERIFIED");
            holder.image.setLabelBackgroundColor(context.getResources().getColor(R.color.colorHighlight, null));
        } else {
            holder.image.setLabelVisual(false);
        }

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(true)
                .format(DecodeFormat.DEFAULT)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);

        Glide.with(context).load(itemList.get(position).getImgUrlList().get(0)).apply(options).thumbnail(0.4f).into(holder.image);

        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(Const.INTENT_EXTRA.SPOT, itemList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, description;
        private LabelImageView image;
        private View container;

        ViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.item_explore_container);
            title = view.findViewById(R.id.item_explore_title);
            description = view.findViewById(R.id.item_explore_description);
            image = view.findViewById(R.id.item_explore_image);
        }
    }
}