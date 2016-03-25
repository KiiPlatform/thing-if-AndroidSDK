package com.kii.thingif.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.kii.thingif.exception.ThingIFException;

import java.util.List;

public interface GatewayAPI4EndNode extends GatewayAPI {
    @NonNull
    @WorkerThread
    String onboardGateway() throws ThingIFException;

    @NonNull
    @WorkerThread
    List<PendingEndNode> listPendingEndNodes() throws ThingIFException;

    @WorkerThread
    void notifyOnboardingCompletion(@NonNull String endNodeThingID, @NonNull String endNodeVenderThingID) throws ThingIFException;

    @WorkerThread
    void replaceEndNode(@NonNull String endNodeThingID, @NonNull String endNodeVenderThingID) throws ThingIFException;
}
