package com.herokuapp.hear_seoul.controller.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.ui.detail.DetailActivity;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpotListAdapter extends RecyclerView.Adapter<SpotListAdapter.ViewHolder> {

    private LinkedList<SpotBean> itemList;
    private Context context;

    public SpotListAdapter(Context context, LinkedList<SpotBean> itemList) {
        this.itemList = itemList;
        this.context = context;
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
        holder.title.setText(itemList.get(position).getTitle());
        holder.description.setText(itemList.get(position).getDescription());

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        String imageUrl = itemList.get(position).getImgSrc();

        if (imageUrl != null && imageUrl.length() < 10) {
            Glide.with(context).load(imageUrl).apply(options).into(holder.image);
        } else {
            Glide.with(context).load(R.drawable.placeholder).apply(options).into(holder.image);
        }

        int typeImageId;
        switch (itemList.get(position).getType()) {
            case "SK":
                typeImageId = R.drawable.ic_landmark;
                break;
            default:
                typeImageId = R.drawable.ic_user_place;
        }
        Glide.with(context).load(typeImageId).apply(options).into(holder.sub);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, description;
        private CircleImageView image, sub;
        private RelativeLayout container;

        ViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.item_spot_list_container);
            title = view.findViewById(R.id.item_spot_list_title);
            description = view.findViewById(R.id.item_spot_list_description);
            image = view.findViewById(R.id.item_spot_list_image_main);
            sub = view.findViewById(R.id.item_spot_list_image_sub);
        }
    }
}