package com.kii.thingif;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.kii.thingif.exception.ThingIFException;

public class GatewayAPIBuilder {

    private final Context context;
    private final String appID;
    private final String appKey;
    private final Site site;
    private final String hostname;
    private final String username;
    private final String password;

    private GatewayAPIBuilder(
            @Nullable Context context,
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull Site site,
            @NonNull String hostname,
            @NonNull String username,
            @NonNull String password) {
        this.context = context;
        this.appID = appID;
        this.appKey = appKey;
        this.site = site;
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }

    /**
     * Instantiate new GatewayAPIBuilder.
     *
     * @param context Application context.
     * @param appID Application ID given by Kii Cloud.
     * @param appKey Application Key given by Kii Cloud.
     * @param site Application Site specified when create application.
     * @param hostname Hostname or IP address for gateway
     * @param username Username for gateway
     * @param password Password for gateway
     * @return
     */
    @NonNull
    public static GatewayAPIBuilder newBuilder(
            @NonNull Context context,
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull Site site,
            @NonNull String hostname,
            @NonNull String username,
            @NonNull String password) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (TextUtils.isEmpty(appID)) {
            throw new IllegalArgumentException("appID is null or empty");
        }
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("appKey is null or empty");
        }
        if (site == null) {
            throw new IllegalArgumentException("site is null");
        }
        if (TextUtils.isEmpty(hostname)) {
            throw new IllegalArgumentException("hostname is null or empty");
        }
        return new GatewayAPIBuilder(context, appID, appKey, site, hostname, username, password);
    }

    /**
     * Instantiate new GatewayAPI4Gateway instance.
     * @return GatewayAPI4Gateway instance.
     * @throws ThingIFException
     */
    @WorkerThread
    @NonNull
    public GatewayAPI build4Gateway() throws ThingIFException {
        String baseUrl = "http://" + this.hostname;
        return new GatewayAPI4Gateway(this.context, this.appID, this.appKey, this.site, baseUrl);
    }
    /**
     * Instantiate new GatewayAPI4EndNode instance.
     * @return GatewayAPI4EndNode instance.
     * @throws ThingIFException
     */
    @WorkerThread
    @NonNull
    public GatewayAPI build4EndNode() throws ThingIFException {
        String baseUrl = "http://" + this.hostname;
        return new GatewayAPI4EndNode(this.context, this.appID, this.appKey, this.site, baseUrl);
    }
}
