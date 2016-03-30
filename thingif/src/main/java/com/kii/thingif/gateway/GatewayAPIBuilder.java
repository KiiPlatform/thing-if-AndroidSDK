package com.kii.thingif.gateway;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.kii.thingif.KiiApp;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.exception.ThingIFException;

public class GatewayAPIBuilder {

    private final Context context;
    private String tag;
    private final KiiApp app;
    private final GatewayAddress gatewayAddress;
    private String accessToken;

    private GatewayAPIBuilder(
            @Nullable Context context,
            @NonNull KiiApp app,
            @NonNull GatewayAddress gatewayAddress) {
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
            @NonNull GatewayAddress gatewayAddress) {
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
     * Instantiate new GatewayAPIBuilder without Context.
     * This method is for internal use only. Do not call it from your application.
     *
     * @param app Kii Cloud Application.
     * @param gatewayAddress Gateway Address
     * @return ThingIFAPIBuilder instance.
     */
    @NonNull
    public static GatewayAPIBuilder _newBuilder(
            @NonNull KiiApp app,
            @NonNull GatewayAddress gatewayAddress) {
        if (app == null) {
            throw new IllegalArgumentException("app is null");
        }
        if (gatewayAddress == null) {
            throw new IllegalArgumentException("gatewayAddress is null");
        }
        return new GatewayAPIBuilder(null, app, gatewayAddress);
    }

    public GatewayAPIBuilder setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    /**
     * Instantiate new GatewayAPI instance.
     * @return GatewayAPI instance.
     */
    @WorkerThread
    @NonNull
    public GatewayAPI build() {
        GatewayAPI api = new GatewayAPI(this.context, this.tag, this.app, this.gatewayAddress);
        api.setAccessToken(this.accessToken);
        return api;
    }
}
