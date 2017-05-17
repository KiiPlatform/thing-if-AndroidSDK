package com.kii.thingiftrait.internal.utils;

import com.kii.thingiftrait.BuildConfig;

/**
 * Wrap the android.util._Log class
 * This class is for internal use only. Do not use it from your application.
 */
public class _Log {
    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg);
    }
    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag, msg);
    }
    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.i(tag, msg);
    }
    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.w(tag, msg);
    }
    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.e(tag, msg);
    }
    public static void wtf(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.wtf(tag, msg);
    }
    public static void v(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg, t);
    }
    public static void d(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag, msg, t);
    }
    public static void i(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.i(tag, msg, t);
    }
    public static void w(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.w(tag, msg, t);
    }
    public static void e(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.e(tag, msg, t);
    }
    public static void wtf(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.wtf(tag, msg, t);
    }
}
