package com.kii.thingif.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.kii.thingif.exception.ThingIFException;

public interface GatewayAPI {
    @WorkerThread
    void login(@NonNull String username, @NonNull String password) throws ThingIFException;

    @WorkerThread
    @NonNull
    String getGatewayInformation() throws ThingIFException;

    boolean isLoggedIn();
}
