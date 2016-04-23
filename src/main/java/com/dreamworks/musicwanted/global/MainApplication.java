package com.dreamworks.musicwanted.global;

import android.app.Application;

/**
 * Created by zhang on 2016/4/22.
 */
public class MainApplication extends Application {

    private static MainApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MainApplication getApp() {
        return instance;
    }
}
