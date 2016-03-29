package com.kii.thingif.gateway;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.kii.thingif.KiiApp;
import com.kii.thingif.exception.ThingIFException;

public class GatewayAPIBuilder {

    private final Context context;
    private final KiiApp app;

    private GatewayAPIBuilder(
            @Nullable Context context,
            @NonNull KiiApp app) {
        this.context = context;
        this.app = app;
    }

    /**
     * Instantiate new GatewayAPIBuilder.
     *
     * @param context Application context.
     * @param app Kii Cloud Application.
     * @return
     */
    @NonNull
    public static GatewayAPIBuilder newBuilder(
            @NonNull Context context,
            @NonNull KiiApp app) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (app == null) {
            throw new IllegalArgumentException("app is null");
        }
        return new GatewayAPIBuilder(context, app);
    }

    /**
     * Instantiate new GatewayAPI instance.
     * @return GatewayAPI instance.
     */
    @WorkerThread
    @NonNull
    public GatewayAPI build() {
        return new GatewayAPI(this.context, this.app);
    }
}
