package com.kii.thingif;

import android.support.annotation.Nullable;

import com.kii.thingif.gateway.PendingEndNode;

public class OnboardEndnodeWithGatewayOptions {
    private final DataGroupingInterval dataGroupingInterval;

    /**
     * Optional parameters of {@link ThingIFAPI#onboardEndnodeWithGateway(PendingEndNode, String, OnboardEndnodeWithGatewayOptions)}.
     * @param interval 1_MINUTE | 15_MINUTES | 30_MINUTES | 1_HOUR | 12_HOURS.
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
