package com.herokuapp.hear_seoul.core;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.herokuapp.hear_seoul.R;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class Utils {

    public static float calcDistance(LatLng from, LatLng to) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(to.latitude - from.latitude);
        double lngDiff = Math.toRadians(to.longitude - from.longitude);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) + Math.cos(Math.toRadians(to.latitude)) * Math.cos(Math.toRadians(from.latitude)) *
                Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (float) (distance * meterConversion);
    }

    public static void showStyleToast(Context context, String message) {
        new StyleableToast
                .Builder(context)
                .text(message)
                .textColor(Color.WHITE)
                .backgroundColor(context.getColor(R.color.colorAccent))
                .show();
    }

    // 앱 정보 화면
    public static void startAppInformationActivity(Context context) {
        String packageName = context.getPackageName();
        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            context.startActivity(intent);
        }
    }
}
