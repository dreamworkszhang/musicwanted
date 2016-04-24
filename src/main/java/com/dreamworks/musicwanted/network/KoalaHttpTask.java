package com.dreamworks.musicwanted.network;

import com.dreamworks.musicwanted.global.MainApplication;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by zhang on 2016/4/23.
 */
public class KoalaHttpTask {

    private static KoalaHttpTask instance;
    private OkHttpClient okHttpClient;

    public static final String RESPONSE_CACHE = "cache_response";
    public static final int RESPONSE_CACHE_SIZE = 16 * 1024;
    public static final int HTTP_CONNECT_TIMEOUT = 1000 * 10;
    public static final int HTTP_READ_TIMEOUT = 1000 * 10;
    public static final int HTTP_WRITE_TIMEOUT = 1000 * 10;

    private KoalaHttpTask() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //File cacheDir = new File(MainApplication.getApp().getCacheDir(), RESPONSE_CACHE);
        // builder.cache(new Cache(cacheDir, RESPONSE_CACHE_SIZE));
        builder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient = builder.build();
    }

    public static KoalaHttpTask getInstance() {
        if (instance == null) {
            synchronized (KoalaHttpTask.class) {
                if (instance == null) {
                    instance = new KoalaHttpTask();
                }
            }
        }
        return instance;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }


}
