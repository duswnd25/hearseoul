/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;

import java.util.ArrayList;

public class InfoImageAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> itemList;

    public InfoImageAdapter(Context context, ArrayList<String> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_detail_image, container, false);

        ImageView image = view.findViewById(R.id.item_detail_image_slider);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(true)
                .format(DecodeFormat.DEFAULT)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);

        Glide.with(context).load(itemList.get(position)).apply(options).thumbnail(0.4f).into(image);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }
}