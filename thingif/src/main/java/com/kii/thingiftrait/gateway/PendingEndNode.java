package com.kii.thingiftrait.gateway;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PendingEndNode implements Parcelable {
    @NonNull private final String vendorThingID;
    @Nullable private final String thingType;
    @Nullable private JSONObject thingProperties;
    @Nullable private final String firmwareVersion;

    public PendingEndNode(@NonNull String vendorThingID) {
        this(vendorThingID, null);
    }
    public PendingEndNode(@NonNull String vendorThingID, @Nullable String thingType) {
        this(vendorThingID, thingType, null, null);
    }
    public PendingEndNode(
            @NonNull String vendorThingID,
            @Nullable String thingType,
            @Nullable String firmwareVersion,
            @Nullable JSONObject thingProperties) {
        this.vendorThingID = vendorThingID;
        this.thingType = thingType;
        this.thingProperties = thingProperties;
        this.firmwareVersion = firmwareVersion;
    }

    PendingEndNode(JSONObject json) {
        this.vendorThingID = json.optString("vendorThingID");
        this.thingProperties = json.optJSONObject("thingProperties");
        if (this.thingProperties != null && this.thingProperties.has("thingType")) {
            this.thingType = this.thingProperties.optString("thingType");
        } else {
            this.thingType = null;
        }
        if (this.thingProperties != null && this.thingProperties.has("firmwareVersion")) {
            this.firmwareVersion = this.thingProperties.optString("firmwareVersion");
        } else {
            this.firmwareVersion = null;
        }
    }
    @NonNull
    public String getVendorThingID() {
        return this.vendorThingID;
    }
    @Nullable
    public String getThingType() {
        return this.thingType;
    }
    @Nullable
    public JSONObject getThingProperties() {
        return this.thingProperties;
    }
    @Nullable
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    protected PendingEndNode(Parcel in) {
        this.vendorThingID = in.readString();
        this.thingType = in.readString();
        this.firmwareVersion = in.readString();
        String json = in.readString();
        if (!TextUtils.isEmpty(json)) {
            try {
                this.thingProperties = new JSONObject(json);
            } catch (JSONException ignore) {
            }
        }
    }
    public static final Creator<PendingEndNode> CREATOR = new Creator<PendingEndNode>() {
        @Override
        public PendingEndNode createFromParcel(Parcel in) {
            return new PendingEndNode(in);
        }

        @Override
        public PendingEndNode[] newArray(int size) {
            return new PendingEndNode[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.vendorThingID);
        dest.writeString(this.thingType);
        dest.writeString(this.firmwareVersion);
        if (this.thingProperties != null) {
            dest.writeString(this.thingProperties.toString());
        }
    }
}
