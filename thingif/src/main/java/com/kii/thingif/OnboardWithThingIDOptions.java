package com.kii.thingif;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Optional parameters of {@link ThingIFAPI#onboard(String, String, OnboardWithThingIDOptions)}.
 */
public class OnboardWithThingIDOptions {
    private final LayoutPosition layoutPosition;
    private final DataGroupingInterval dataGroupingInterval;

    OnboardWithThingIDOptions(
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

    public class Builder {
        private LayoutPosition layoutPosition;
        private DataGroupingInterval dataGroupingInterval;

        /**
         * set layout position.
         * @param layoutPosition GATEWAY | STANDALONE | ENDNODE.
         * @return this.
         */
        @NonNull
        public Builder setLayoutPosition(@Nullable  LayoutPosition layoutPosition) {
            this.layoutPosition = layoutPosition;
            return this;
        }

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
         * get layout position.
         * @return layout position.
         */
        @Nullable
        public LayoutPosition getLayoutPosition() { return this.layoutPosition; }

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
         * @return OnboardWithThingIDOptions object.
         */
        @NonNull
        public OnboardWithThingIDOptions build() {
            return new OnboardWithThingIDOptions(this.layoutPosition, this.dataGroupingInterval);
        }
    }
}
