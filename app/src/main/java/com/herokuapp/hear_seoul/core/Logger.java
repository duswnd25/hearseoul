/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.core;

import android.util.Log;

public class Logger {
    private static final String APP_NAME = "여기, 서울";

    public static void d(String message) {
        Log.d(APP_NAME, message);
    }

    public static void e(String message) {
        Log.e(APP_NAME, message);
    }
}