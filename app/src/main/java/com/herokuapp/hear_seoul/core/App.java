/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.core;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.google.firebase.messaging.FirebaseMessaging;
import com.herokuapp.hear_seoul.R;
import com.skt.baas.api.Baas;

import java.util.Objects;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Baas.init(this, 1, getString(R.string.baas_key));
        Baas.debugMode(false);
        FirebaseMessaging.getInstance().subscribeToTopic("default");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "default_channel";
            String channelName = getString(R.string.notification_channel_default_title);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            Objects.requireNonNull(notificationManager).createNotificationChannel(mChannel);
        }
    }
}
