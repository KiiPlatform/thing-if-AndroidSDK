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
    private final String hostname;
    private final String username;
    private final String password;

    private GatewayAPIBuilder(
            @Nullable Context context,
            @NonNull KiiApp app,
            @NonNull String hostname,
            @NonNull String username,
            @NonNull String password) {
        this.context = context;
        this.app = app;
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }

    /**
     * Instantiate new GatewayAPIBuilder.
     *
     * @param context Application context.
     * @param app Kii Cloud Application.
     * @param hostname Hostname or IP address for gateway
     * @param username Username for gateway
     * @param password Password for gateway
     * @return
     */
    @NonNull
    public static GatewayAPIBuilder newBuilder(
            @NonNull Context context,
            @NonNull KiiApp app,
            @NonNull String hostname,
            @NonNull String username,
            @NonNull String password) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (app == null) {
            throw new IllegalArgumentException("app is null");
        }
        if (TextUtils.isEmpty(hostname)) {
            throw new IllegalArgumentException("hostname is null or empty");
        }
        return new GatewayAPIBuilder(context, app, hostname, username, password);
    }

    /**
     * Instantiate new GatewayAPI4GatewayImpl instance.
     * @return GatewayAPI4GatewayImpl instance.
     * @throws ThingIFException
     */
    @WorkerThread
    @NonNull
    public GatewayAPI4Gateway build4Gateway() throws ThingIFException {
        String baseUrl = "http://" + this.hostname;
        return new GatewayAPI4GatewayImpl(this.context, this.app);
    }
    /**
     * Instantiate new GatewayAPI4EndNodeImpl instance.
     * @return GatewayAPI4EndNodeImpl instance.
     * @throws ThingIFException
     */
    @WorkerThread
    @NonNull
    public GatewayAPI4EndNode build4EndNode() throws ThingIFException {
        String baseUrl = "http://" + this.hostname;
        return new GatewayAPI4EndNodeImpl(this.context, this.app);
    }
}
