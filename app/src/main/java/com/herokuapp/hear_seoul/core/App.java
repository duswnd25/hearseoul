/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.core;

import android.app.Application;

import com.herokuapp.hear_seoul.R;
import com.skt.baas.api.Baas;
import com.skt.baas.api.BaasInstallation;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Baas.init(this, 1, getString(R.string.baas_key));
        Baas.debugMode(false);
        BaasInstallation.isUseBaasPush(true, getString(R.string.google_project));
    }
}
