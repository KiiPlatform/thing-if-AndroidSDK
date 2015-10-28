package com.kii.thingif;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.internal.http.IoTRestClient;

import java.util.HashMap;
import java.util.Map;

public abstract class GatewayAPI {

    protected final Context context;
    protected final String appID;
    protected final String appKey;
    protected final Site site;
    protected final String baseUrl;
    protected final String accessToken;
    protected final IoTRestClient restClient;

    GatewayAPI(@Nullable Context context,
               @NonNull String appID,
               @NonNull String appKey,
               @NonNull Site site,
               @NonNull String baseUrl,
               @NonNull String accessToken) {
        this.context = context;
        this.appID = appID;
        this.appKey = appKey;
        this.site = site;
        this.baseUrl = baseUrl;
        this.accessToken = accessToken;
        this.restClient = new IoTRestClient();
    }
    protected Map<String, String> newHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + this.accessToken);
        return headers;
    }
}
