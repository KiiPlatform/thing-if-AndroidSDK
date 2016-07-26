package com.kii.thingif;

import android.support.annotation.Nullable;

import org.json.JSONObject;

public class OnboardWithVendorThingIDOptions {
    private final String thingType;
    private final JSONObject thingProperties;
    private final LayoutPosition layoutPosition;
    private final DataGroupingInterval dataGroupingInterval;

    /**
     * Optional parameters of {@link ThingIFAPI#onboard(String, String, OnboardWithVendorThingIDOptions)}.
     * @param thingType Type of the thing given by vendor.
     * If the thing is already registered, this value would be
     * ignored by IoT Cloud.
     * @param thingProperties Properties of thing.
     * If the thing is already registered, this value
     * would be ignored by IoT Cloud.<br>
     * Refer to the <a href="http://docs.kii.com/rest/#thing_management-register_a_thin">register_a_thing</a>
     * About the format of this Document.
     * @param position GATEWAY | STANDALONE | ENDNODE.
     * @param interval INTERVAL_1_MINUTE | INTERVAL_15_MINUTES | INTERVAL_30_MINUTES | INTERVAL_1_HOUR | INTERVAL_12_HOURS.
     * Will be used to create the bucket to store the state history when the thing is not using traits.
     */
    public OnboardWithVendorThingIDOptions(
            @Nullable String thingType,
            @Nullable JSONObject thingProperties,
            @Nullable LayoutPosition position,
            @Nullable DataGroupingInterval interval) {
        this.thingType = thingType;
        this.thingProperties = thingProperties;
        this.layoutPosition = position;
        this.dataGroupingInterval = interval;
    }

    @Nullable
    public String getThingType() { return this.thingType; }

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
}
