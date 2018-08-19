/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.controller.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.EventBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

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

    @SuppressLint({"CheckResult"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.title.setText(itemList.get(position).getTitle());
        holder.dDay.setText(getDday(itemList.get(position).getStartDate()));
        String dateText = itemList.get(position).getStartDate() + " ~ " + itemList.get(position).getEndDate();
        holder.date.setText(dateText);
        holder.gCode.setText(itemList.get(position).getGcode());

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.skipMemoryCache(true);
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        options.error(R.drawable.placeholder);
        String imageUrl = itemList.get(position).getMainImg();

        if (imageUrl.length() > 10) {
            Glide.with(context).load(makeCorrectImageUrl(imageUrl)).apply(options).into(holder.mainImage);
        } else {
            Glide.with(context).load(R.drawable.placeholder).apply(options).into(holder.mainImage);
        }
        int feeType = itemList.get(position).getUseFee().equals("무료") ? R.drawable.ic_free : R.drawable.ic_no_free;
        Glide.with(context).load(feeType).apply(options).into(holder.subImage);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dDay, title, date, gCode;
        private ImageView mainImage;
        private CircleImageView subImage;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.item_event_list_cultural_title);
            dDay = view.findViewById(R.id.item_event_list_d_day);
            date = view.findViewById(R.id.item_event_list_cultural_date);
            gCode = view.findViewById(R.id.item_event_list_cultural_g_code);
            mainImage = view.findViewById(R.id.item_event_list_main_image);
            subImage = view.findViewById(R.id.item_event_list_sub_image);
        }
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
                return "진행중";
            }
            return "D-" + diffDays;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}