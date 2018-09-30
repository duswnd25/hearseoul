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
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.EventBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.ui.event_detail.EventDetailActivity;
import com.lid.lib.LabelImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private LinkedList<EventBean> itemList;
    private Context context;

    public EventListAdapter(Context context, LinkedList<EventBean> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.title.setText(itemList.get(position).getTitle());
        holder.dDay.setText(getDday(itemList.get(position).getStartDate()));
        String dateText = itemList.get(position).getStartDate() + " ~ " + itemList.get(position).getEndDate();
        holder.date.setText(dateText);
        holder.gCode.setText(itemList.get(position).getGcode());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(true)
                .format(DecodeFormat.DEFAULT)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);

        String imageUrl = itemList.get(position).getMainImg();

        if (imageUrl.length() > 10) {
            Glide.with(context).load(makeCorrectImageUrl(imageUrl)).apply(options).into(holder.mainImage);
        } else {
            Glide.with(context).load(R.drawable.placeholder).apply(options).into(holder.mainImage);
        }

        if (itemList.get(position).getUseFee().equals("무료")) {
            holder.mainImage.setLabelText(context.getString(R.string.fee_no));
            holder.mainImage.setLabelBackgroundColor(Color.parseColor("#03A9F4"));
        } else {
            holder.mainImage.setLabelText(context.getString(R.string.fee_yes));
            holder.mainImage.setLabelBackgroundColor(Color.parseColor("#C2185B"));
        }

        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra(Const.INTENT_EXTRA.EVENT, itemList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private String makeCorrectImageUrl(String imageUrl) {
        // 이 부분은 서울시 API 라이브러리를 디컴파일 해서 가져온 로직이다.
        // 이렇게 별도 가공을 하지 않으면 No content provider 오류가 발생한다.
        StringBuilder url = new StringBuilder();
        String[] mainImg = imageUrl.split("\\/");
        for (int i = 0; i < mainImg.length; i++) {
            if ((i == 0) || (i == 1) || (i == 2)) {
                url.append(mainImg[i].toLowerCase()).append("/");
            } else if (i == mainImg.length - 1) {
                url.append(mainImg[i]);
            } else {
                url.append(mainImg[i]).append("/");
            }
        }
        return url.toString();
    }

    private String getDday(String startDate) {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = formatter.parse(startDate);
            Date currentDay = formatter.parse(formatter.format(new Date()));

            long diff = beginDate.getTime() - currentDay.getTime();
            long diffDays = diff / 86400000L;
            if (diffDays <= 0L) {
                return context.getString(R.string.in_progress);
            }
            return "D-" + diffDays;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dDay, title, date, gCode;
        private LabelImageView mainImage;
        private LinearLayout container;

        ViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.item_event_list_container);
            title = view.findViewById(R.id.item_event_list_cultural_title);
            dDay = view.findViewById(R.id.item_event_list_d_day);
            date = view.findViewById(R.id.item_event_list_cultural_date);
            gCode = view.findViewById(R.id.item_event_list_cultural_g_code);
            mainImage = view.findViewById(R.id.item_event_list_main_image);
        }
    }
}