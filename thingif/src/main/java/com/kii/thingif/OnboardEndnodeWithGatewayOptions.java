package com.kii.thingif;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.gateway.PendingEndNode;

/**
 * Optional parameters of {@link ThingIFAPI#onboardEndnodeWithGateway(PendingEndNode, String, OnboardEndnodeWithGatewayOptions)}.
 */
public class OnboardEndnodeWithGatewayOptions {
    private final DataGroupingInterval dataGroupingInterval;

    OnboardEndnodeWithGatewayOptions(
            @Nullable DataGroupingInterval interval) {
        this.dataGroupingInterval = interval;
    }

    @Nullable
    public DataGroupingInterval getDataGroupingInterval() {
        return this.dataGroupingInterval;
    }

    public class Builder {
        private DataGroupingInterval dataGroupingInterval;

        /**
         * set data grouping interval.
         * @param dataGroupingInterval INTERVAL_1_MINUTE | INTERVAL_15_MINUTES | INTERVAL_30_MINUTES | INTERVAL_1_HOUR | INTERVAL_12_HOURS.
         * Will be used to create the bucket to store the state history when the thing is not using traits.
         * @return this.
         */
        @NonNull
        public Builder setDataGroupingInterval(@Nullable  DataGroupingInterval dataGroupingInterval) {
            this.dataGroupingInterval = dataGroupingInterval;
            return this;
        }

        /**
         * get data grouping interval.
         * @return data grouping interval.
         */
        @Nullable
        public DataGroupingInterval getDataGroupingInterval() {
            return this.dataGroupingInterval;
        }

        /**
         * build option object.
         * @return OnboardEndnodeWithGatewayOptions object.
         */
        @NonNull
        public OnboardEndnodeWithGatewayOptions build() {
            return new OnboardEndnodeWithGatewayOptions(this.dataGroupingInterval);
        }
    }
}
