package com.kii.thingif.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.kii.thingif.exception.ThingIFException;

public interface GatewayAPI4Gateway extends GatewayAPI {
    @NonNull
    @WorkerThread
    String onboardGateway() throws ThingIFException;

    @NonNull
    @WorkerThread
    String getGatewayID() throws ThingIFException;

    @WorkerThread
    void restore() throws ThingIFException;
}
