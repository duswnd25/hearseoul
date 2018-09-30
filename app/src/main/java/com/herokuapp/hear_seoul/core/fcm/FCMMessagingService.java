/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.core.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.RemoteMessage;
import com.herokuapp.hear_seoul.R;
import com.herokuapp.hear_seoul.bean.SpotBean;
import com.herokuapp.hear_seoul.core.Const;
import com.herokuapp.hear_seoul.core.Utils;
import com.herokuapp.hear_seoul.ui.main.MainActivity;
import com.herokuapp.hear_seoul.ui.spot_detail.DetailActivity;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class FCMMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    // 메시지 수신
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences savedData = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isUserWantNotification = savedData.getBoolean(getString(R.string.pref_notification), true);
        boolean isUserWantSound = savedData.getBoolean(getString(R.string.pref_notification_sound), true);

        // Data from FCM
        Map<String, String> data = remoteMessage.getData();
        String id = data.get("id");
        String title = data.get("title");
        String message = data.get("content");
        String channelId = data.get("channel");
        String type = data.get("type");
        int priority = Integer.parseInt(data.get("priority"));

        if (!isUserWantNotification && priority < 4) {
            return;
        }

        // Notification Data
        Intent intent = new Intent(this, MainActivity.class);
        if (type.equals("suggest")) {
            // 추천 알림
            String[] targetLocation = data.get("location").split("/");
            String[] userLocation = savedData.getString(Const.PREFERENCE.SAVED_LOCATION, "37.541/126.986").split("/");
            float distance = Utils.calcDistance(
                    new LatLng(Double.parseDouble(userLocation[0]), Double.parseDouble(userLocation[1])),
                    new LatLng(Double.parseDouble(targetLocation[0]), Double.parseDouble(targetLocation[1]))
            );
            if (distance < 10000) {
                return;
            }
            SpotBean spotBean = new SpotBean();
            spotBean.setId(id);

            intent = new Intent(this, DetailActivity.class);
            intent.putExtra(Const.INTENT_EXTRA.SPOT, spotBean);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        int[] colorList = {R.color.colorHighlight, R.color.colorAccent, android.R.color.black};
        Random generator = new Random();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setContentTitle(title)
                .setPriority(priority)
                .setContentText(message)
                .setColor(getResources().getColor(colorList[generator.nextInt(colorList.length)], null))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setSound(isUserWantSound ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) : null)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent);

        Objects.requireNonNull(notificationManager).notify(generator.nextInt(), builder.build());
    }
}