package com.kii.thingif.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.kii.thingif.exception.ThingIFException;

public interface GatewayAPI4Gateway extends GatewayAPI {
    /** Let the Gateway Onboard.
     * @return Thing ID assigned by Kii Cloud.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @NonNull
    @WorkerThread
    String onboardGateway() throws ThingIFException;

    /**
     * Get Gateway ID
     * @return Thing ID assigned by Kii Cloud.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @NonNull
    @WorkerThread
    String getGatewayID() throws ThingIFException;

    /** Restore the Gateway
     * @throws ThingIFException
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    void restore() throws ThingIFException;
}
