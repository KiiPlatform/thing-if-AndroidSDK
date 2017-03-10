package com.kii.thingif;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * Optional parameters of {@link ThingIFAPI#onboardWithVendorThingID(String, String)}.
 */
public class OnboardWithVendorThingIDOptions {
    private final String thingType;
    private final String firmwareVersion;
    private final JSONObject thingProperties;
    private final LayoutPosition layoutPosition;

    private OnboardWithVendorThingIDOptions(
            @Nullable String thingType,
            @Nullable String firmwareVersion,
            @Nullable JSONObject thingProperties,
            @Nullable LayoutPosition position) {
        this.thingType = thingType;
        this.firmwareVersion = firmwareVersion;
        this.thingProperties = thingProperties;
        this.layoutPosition = position;
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

    public static class Builder {
        private String thingType;
        private String firmwareVersion;
        private JSONObject thingProperties;
        private LayoutPosition layoutPosition;

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
         * build option object.
         * @return OnboardWithVendorThingIDOptions object.
         */
        @NonNull
        public OnboardWithVendorThingIDOptions build() {
            return new OnboardWithVendorThingIDOptions(this.thingType,
                    this.firmwareVersion, this.thingProperties,
                    this.layoutPosition);
        }
    }
}
