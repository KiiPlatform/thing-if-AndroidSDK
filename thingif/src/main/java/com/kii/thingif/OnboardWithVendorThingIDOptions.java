package com.kii.thingif;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * Optional parameters of {@link ThingIFAPI#onboard(String, String, OnboardWithVendorThingIDOptions)}.
 */
public class OnboardWithVendorThingIDOptions {
    private final String thingType;
    private final String firmwareVersion;
    private final JSONObject thingProperties;
    private final LayoutPosition layoutPosition;
    private final DataGroupingInterval dataGroupingInterval;

    private OnboardWithVendorThingIDOptions(
            @Nullable String thingType,
            @Nullable String firmwareVersion,
            @Nullable JSONObject thingProperties,
            @Nullable LayoutPosition position,
            @Nullable DataGroupingInterval interval) {
        this.thingType = thingType;
        this.firmwareVersion = firmwareVersion;
        this.thingProperties = thingProperties;
        this.layoutPosition = position;
        this.dataGroupingInterval = interval;
    }

    @Nullable
    public String getThingType() { return this.thingType; }

    @Nullable
    public String getFirmwareVersion() { return this.firmwareVersion; }

    @Nullable
    public JSONObject getThingProperties() { return this.thingProperties; }

    @Nullable
    public LayoutPosition getLayoutPosition() {
        return this.layoutPosition;
    }

    @Nullable
    public DataGroupingInterval getDataGroupingInterval() {
        return this.dataGroupingInterval;
    }

    public static class Builder {
        private String thingType;
        private String firmwareVersion;
        private JSONObject thingProperties;
        private LayoutPosition layoutPosition;
        private DataGroupingInterval dataGroupingInterval;

        /**
         * set thing type.
         * @param thingType Type of the thing given by vendor.
         * If the thing is already registered, this value would be ignored by IoT Cloud.
         * @return this.
         */
        @NonNull
        public Builder setThingType(@Nullable String thingType) {
            this.thingType = thingType;
            return this;
        }

        /**
         * set firmware version.
         * @param firmwareVersion Firmware version of the thing.
         * @return this.
         */
        @NonNull
        public Builder setFirmwareVersion(@Nullable String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
            return this;
        }

        /**
         * set thing proeprties.
         * @param thingProperties The properties of the thing.
         * You can set both the predefined and custom fields.
         * Please read <a href="https://docs.kii.com/en/starts/thingifsdk/thingsdk/management/#register-a-thing">here</a> for more details.
         * @return this.
         */
        @NonNull
        public Builder setThingProperties(@Nullable JSONObject thingProperties) {
            this.thingProperties = thingProperties;
            return this;
        }

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
         * get thing type.
         * @return thing type.
         */
        @Nullable
        public String getThingType() { return this.thingType; }

        /**
         * get firmware version.
         * @return firmware version.
         */
        @Nullable
        public String getFirmwareVersion() { return this.firmwareVersion; }

        /**
         * get thing properties.
         * @return thing properties.
         */
        @Nullable
        public JSONObject getThingProperties() { return this.thingProperties; }

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
         * @return OnboardWithVendorThingIDOptions object.
         */
        @NonNull
        public OnboardWithVendorThingIDOptions build() {
            return new OnboardWithVendorThingIDOptions(this.thingType,
                    this.firmwareVersion, this.thingProperties,
                    this.layoutPosition, this.dataGroupingInterval);
        }
    }
}
