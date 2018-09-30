/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.ui.event_detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.EventBean;
import com.herokuapp.hear_seoul.core.Const;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private String detailUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Toolbar toolbar = findViewById(R.id.event_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        EventBean eventBean = intent.getParcelableExtra(Const.INTENT_EXTRA.EVENT);

        ImageView mainImage = findViewById(R.id.event_detail_image);
        AutofitTextView title = findViewById(R.id.event_detail_title);
        AutofitTextView code = findViewById(R.id.event_detail_code_name);
        AutofitTextView date = findViewById(R.id.event_detail_date);
        AutofitTextView time = findViewById(R.id.event_detail_time);
        AutofitTextView place = findViewById(R.id.event_detail_place);
        AutofitTextView target = findViewById(R.id.event_detail_target);
        AutofitTextView fee = findViewById(R.id.event_detail_use_fee);
        AutofitTextView sponsor = findViewById(R.id.event_detail_sponsor);
        AutofitTextView inquiry = findViewById(R.id.event_detail__inquiry);
        TextView program = findViewById(R.id.event_detail_program);

        title.setText(eventBean.getTitle());
        code.setText(eventBean.getCodeName());
        String dateString = eventBean.getStartDate() + " ~ " + eventBean.getEndDate();
        date.setText(dateString);
        time.setText(eventBean.getTime());
        place.setText(eventBean.getPlace());
        target.setText(eventBean.getUseTarget());
        fee.setText(eventBean.getUseFee());
        inquiry.setText(eventBean.getInquery());
        sponsor.setText(eventBean.getSponsor());
        program.setText(eventBean.getProgram());
        this.detailUrl = eventBean.getOrgLink();

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(true)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.placeholder);

        Glide.with(this).load(makeCorrectImageUrl(eventBean.getMainImg())).apply(options).into(mainImage);

        findViewById(R.id.event_detail_go_web).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.event_detail_go_web:
                Intent intent = new Intent("android.intent.action.VIEW");
                Uri uri = Uri.parse(this.detailUrl);
                intent.setData(uri);
                startActivity(intent);
                break;
            default:
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
