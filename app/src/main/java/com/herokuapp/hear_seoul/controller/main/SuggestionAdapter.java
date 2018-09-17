package com.herokuapp.hear_seoul.controller.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Logger;
import com.herokuapp.hear_seoul.core.Utils;
import com.herokuapp.hear_seoul.ui.detail.DetailActivity;

import java.util.LinkedList;

import me.grantland.widget.AutofitTextView;

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

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_main_suggestion, container, false);

        ImageView image = view.findViewById(R.id.item_main_suggestion_image);
        AutofitTextView title = view.findViewById(R.id.item_main_suggestion_title);
        AutofitTextView description = view.findViewById(R.id.item_main_suggestion_description);
        AutofitTextView distance = view.findViewById(R.id.item_main_suggestion_distance);
        //TextView tag = view.findViewById(R.id.item_main_suggestion_tag);

        title.setText(itemList.get(position).getTitle());
        description.setText(itemList.get(position).getDescription());
        distance.setText(Utils.getDistanceFromeCurrentLocation(context, itemList.get(position).getLocation()) + "km");

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .format(DecodeFormat.DEFAULT)
                .error(R.drawable.placeholder);

        Glide.with(context).load(itemList.get(position).getImgUrlList().get(0)).apply(options).thumbnail(0.4f).into(image);

        view.findViewById(R.id.item_main_suggestion_container).setOnClickListener(view1 -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(Const.INTENT_EXTRA.SPOT, itemList.get(position));
            context.startActivity(intent);
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }
}