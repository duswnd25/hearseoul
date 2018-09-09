package com.herokuapp.hear_seoul.controller.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Logger;

import java.util.LinkedList;

public class SuggestionAdapter extends PagerAdapter {
    private Context context;
    private LinkedList<SpotBean> itemList;

    public SuggestionAdapter(Context context, LinkedList<SpotBean> itemList) {
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

    @SuppressLint("CheckResult")
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_main_suggestion, container, false);

        ImageView image = v.findViewById(R.id.item_main_suggestion_image);
        TextView title = v.findViewById(R.id.item_main_suggestion_title);
        TextView description = v.findViewById(R.id.item_main_suggestion_description);
        TextView tag = v.findViewById(R.id.item_main_suggestion_tag);

        title.setText(itemList.get(position).getTitle());
        description.setText(itemList.get(position).getDescription());

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        try {
            Glide.with(context).load(itemList.get(position).getImgSrc().equals("NO") ? R.drawable.placeholder : itemList.get(position).getImgSrc()).apply(options).into(image);
        } catch (Exception e) {
            Logger.e(e.getMessage());
            Glide.with(context).load(R.drawable.placeholder).apply(options).into(image);
        }

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }
}