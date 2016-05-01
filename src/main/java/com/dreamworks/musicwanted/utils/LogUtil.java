package com.dreamworks.musicwanted.utils;

import android.util.Log;

public class LogUtil {

    private final static boolean DEBUG = true;

    public static void d (String tag, Object obj) {
        if (DEBUG) {
            String msg = obj.toString();
            Log.d(tag, msg);
        }
    }

    public static void i (String tag, Object obj) {
        if (DEBUG) {
            String msg = obj.toString();
            Log.i(tag, msg);
        }
    }

    public static void w (String tag, Object obj) {
        if (DEBUG) {
            String msg = obj.toString();
            Log.w(tag, msg);
        }
    }

    public static void v (String tag, Object obj) {
        if (DEBUG) {
            String msg = obj.toString();
            Log.v(tag, msg);
        }
    }

    public static void e (String tag, Object obj) {
        if (DEBUG) {
            String msg = obj.toString();
            Log.e(tag, msg);
        }
    }

}