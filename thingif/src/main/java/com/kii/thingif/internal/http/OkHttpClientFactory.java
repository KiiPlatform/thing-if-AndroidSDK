package com.kii.thingif.internal.http;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class OkHttpClientFactory {
    public static OkHttpClient newInstance() {
        // TODO: adjust parameters
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);
        return client;
    }
}
