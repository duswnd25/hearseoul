package com.herokuapp.hear_seoul.core;

import android.support.multidex.MultiDexApplication;

import com.herokuapp.hear_seoul.R;
import com.skt.baas.api.Baas;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Baas.init(this, 2, getString(R.string.baas_key));
        Baas.debugMode(true);
    }
}
