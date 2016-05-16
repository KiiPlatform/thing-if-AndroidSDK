package com.kii.thingif.gateway;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PendingEndNode implements Parcelable {
    private final String vendorThingID;
    private final String thingType;
    private JSONObject thingProperties;

    public PendingEndNode(String vendorThingID) {
        this(vendorThingID, null, null);
    }
    public PendingEndNode(String vendorThingID, String thingType) {
        this(vendorThingID, thingType, null);
    }
    public PendingEndNode(String vendorThingID, String thingType, JSONObject thingProperties) {
        this.vendorThingID = vendorThingID;
        this.thingType = thingType;
        this.thingProperties = thingProperties;
    }

    PendingEndNode(JSONObject json) {
        this.vendorThingID = json.optString("vendorThingID");
        this.thingProperties = json.optJSONObject("thingProperties");
        if (this.thingProperties != null && this.thingProperties.has("thingType")) {
            this.thingType = this.thingProperties.optString("thingType");
        } else {
            this.thingType = null;
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

    protected PendingEndNode(Parcel in) {
        this.vendorThingID = in.readString();
        this.thingType = in.readString();
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
        if (this.thingProperties != null) {
            dest.writeString(this.thingProperties.toString());
        }
    }
}
