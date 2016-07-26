package com.kii.thingif;

import android.support.annotation.Nullable;

public class OnboardWithThingIDOptions {
    private final LayoutPosition layoutPosition;
    private final DataGroupingInterval dataGroupingInterval;

    /**
     * Optional parameters of {@link ThingIFAPI#onboard(String, String, OnboardWithThingIDOptions)}.
     * @param position GATEWAY | STANDALONE | ENDNODE.
     * @param interval INTERVAL_1_MINUTE | INTERVAL_15_MINUTES | INTERVAL_30_MINUTES | INTERVAL_1_HOUR | INTERVAL_12_HOURS.
     * Will be used to create the bucket to store the state history when the thing is not using traits.
     */
    public OnboardWithThingIDOptions(
            @Nullable LayoutPosition position,
            @Nullable DataGroupingInterval interval) {
        this.layoutPosition = position;
        this.dataGroupingInterval = interval;
    }

    @Nullable
    public LayoutPosition getLayoutPosition() {
        return this.layoutPosition;
    }

    @Nullable
    public DataGroupingInterval getDataGroupingInterval() {
        return this.dataGroupingInterval;
    }
}
