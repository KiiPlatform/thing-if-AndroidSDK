package com.kii.thingif.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.kii.thingif.exception.ThingIFException;

import java.util.List;

public interface GatewayAPI4EndNode extends GatewayAPI {
    /** Let the Gateway Onboard.
     * @return Thing ID assigned by Kii Cloud.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @NonNull
    @WorkerThread
    String onboardGateway() throws ThingIFException;

    /** List connected end nodes which has not been onboarded.
     * @return List of end nodes connected to the gateway but waiting for onboarding.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @NonNull
    @WorkerThread
    List<PendingEndNode> listPendingEndNodes() throws ThingIFException;

    /** Notify Onboarding completion
     * Call this api when the End Node onboarding is done.
     * After the call succeeded, End Node will be fully connected to Kii Cloud through the Gateway.
     * @param endNodeThingID ID of the end-node assigned by Kii Cloud.
     * @param endNodeVenderThingID ID of the end-node assigned by End Node vendor.
     * @throws ThingIFException
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    void notifyOnboardingCompletion(@NonNull String endNodeThingID, @NonNull String endNodeVenderThingID) throws ThingIFException;

    /**
     * Replace end-node by new vendorThingID for end node thingID.
     *
     * @param endNodeThingID ID of the end-node assigned by Kii Cloud.
     * @param endNodeVenderThingID ID of the end-node assigned by End Node vendor.
     * @throws ThingIFException
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    void replaceEndNode(@NonNull String endNodeThingID, @NonNull String endNodeVenderThingID) throws ThingIFException;
}
