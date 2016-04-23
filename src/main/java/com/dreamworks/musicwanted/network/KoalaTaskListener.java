package com.dreamworks.musicwanted.network;

import android.os.Handler;

import com.dreamworks.musicwanted.global.MainApplication;


/**
 * Created by zhang on 2016/4/21.
 */
public abstract class KoalaTaskListener<T> {

    private MODE mode;

    public KoalaTaskListener () {
        mode = MODE.MAIN;
    }

    public KoalaTaskListener (MODE mode) {
        this.mode = mode;
    }

    public abstract void onResponse (T result);

    public void onResponseResult (final T result) {

        switch (mode) {
            case MAIN:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        KoalaTaskListener.this.onResponse(result);
                    }
                });
                break;
            case SYNC:
                KoalaTaskListener.this.onResponse(result);
                break;
            case BACKGROUND:
                ExecutorHelper.getInstance().bgExecute(new Runnable() {
                    @Override
                    public void run() {
                        KoalaTaskListener.this.onResponse(result);
                    }
                });
                break;
        }

    }

    public Handler handler = new Handler(MainApplication.getApp().getMainLooper());

    public enum MODE {
        MAIN,
        SYNC,
        BACKGROUND
    }


}
