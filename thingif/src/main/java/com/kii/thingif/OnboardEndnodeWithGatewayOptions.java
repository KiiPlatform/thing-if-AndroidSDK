package com.kii.thingif;

import android.support.annotation.Nullable;

import com.kii.thingif.gateway.PendingEndNode;

public class OnboardEndnodeWithGatewayOptions {
    private final DataGroupingInterval dataGroupingInterval;

    /**
     * Optional parameters of {@link ThingIFAPI#onboardEndnodeWithGateway(PendingEndNode, String, OnboardEndnodeWithGatewayOptions)}.
     * @param interval INTERVAL_1_MINUTE | INTERVAL_15_MINUTES | INTERVAL_30_MINUTES | INTERVAL_1_HOUR | INTERVAL_12_HOURS.
     * Will be used to create the bucket to store the state history when the thing is not using traits.
     */
    public OnboardEndnodeWithGatewayOptions(
            @Nullable DataGroupingInterval interval) {
        this.dataGroupingInterval = interval;
    }

    @Nullable
    public DataGroupingInterval getDataGroupingInterval() {
        return this.dataGroupingInterval;
    }
}
