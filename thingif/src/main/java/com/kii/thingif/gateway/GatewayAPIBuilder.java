package com.kii.thingif.gateway;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.kii.thingif.KiiApp;

public class GatewayAPIBuilder {

    private final Context context;
    private String tag;
    private final KiiApp app;
    private final Uri gatewayAddress;

    private GatewayAPIBuilder(
            @Nullable Context context,
            @NonNull KiiApp app,
            @NonNull Uri gatewayAddress) {
        this.context = context;
        this.app = app;
        this.gatewayAddress = gatewayAddress;
    }

    /** Set tag to this GatewayAPI instance.
     * tag is used to distinguish storage area of instance.
     * <br>
     * If the api instance is tagged with same string, It will be overwritten.
     * <br>
     * If the api instance is tagged with different string, Different key is used to store the
     * instance.
     * <br>
     * <br>
     * Please refer to {@link GatewayAPI#loadFromStoredInstance(Context, String)} as well.
     * @param tag if null or empty string is passed, it will be ignored.
     * @return builder instance for chaining call.
     */
    @NonNull
    public GatewayAPIBuilder setTag(@Nullable String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * Instantiate new GatewayAPIBuilder.
     *
     * @param context Application context.
     * @param app Kii Cloud Application.
     * @param gatewayAddress address information for the gateway
     * @return
     */
    @NonNull
    public static GatewayAPIBuilder newBuilder(
            @NonNull Context context,
            @NonNull KiiApp app,
            @NonNull Uri gatewayAddress) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (app == null) {
            throw new IllegalArgumentException("app is null");
        }
        if (gatewayAddress == null) {
            throw new IllegalArgumentException("gatewayAddress is null");
        }
        return new GatewayAPIBuilder(context, app, gatewayAddress);
    }

    /**
     * Instantiate new GatewayAPI instance.
     * @return GatewayAPI instance.
     */
    @WorkerThread
    @NonNull
    public GatewayAPI build() {
        GatewayAPI api = new GatewayAPI(this.context, this.tag, this.app, this.gatewayAddress);
        return api;
    }
}