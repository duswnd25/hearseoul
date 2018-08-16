package com.herokuapp.hear_seoul.ui.detail;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.herokuapp.hear_seoul.R;

import java.util.Calendar;

public class DetailEditActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private boolean isFullTime = false;
    private TextView startView, endView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_edit);

        Switch isFullTimeSwitch = findViewById(R.id.detail_edit_is_full_time);
        isFullTimeSwitch.setChecked(isFullTime);
        isFullTimeSwitch.setOnCheckedChangeListener(this);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        startView = findViewById(R.id.detail_edit_start);
        startView.setText("개점 시간");
        startView.setOnClickListener(this);

        endView = findViewById(R.id.detail_edit_end);
        endView.setText("폐점 시간");
        endView.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private TimePickerDialog.OnTimeSetListener startTimePickerListener = (view, hourOfDay, minute) -> {
        startView.setText(hourOfDay + "시 " + minute + "분");
    };

    @SuppressLint("SetTextI18n")
    private TimePickerDialog.OnTimeSetListener endTimePickerListener = (view, hourOfDay, minute) -> {
        endView.setText(hourOfDay + "시 " + minute + "분");
    };

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        findViewById(R.id.detail_edit_time_container).setVisibility(b ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_edit_start:
                new TimePickerDialog(this, startTimePickerListener, 15, 24, false).show();
                break;
            case R.id.detail_edit_end:
                new TimePickerDialog(this, endTimePickerListener, 15, 24, false).show();
                break;
            default:
        }
    }
}
