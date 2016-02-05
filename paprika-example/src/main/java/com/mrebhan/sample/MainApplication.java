package com.mrebhan.sample;

import android.app.Application;

import com.mrebhan.paprika.Paprika;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Paprika paprika = new Paprika();
        paprika.init(this);
    }
}
