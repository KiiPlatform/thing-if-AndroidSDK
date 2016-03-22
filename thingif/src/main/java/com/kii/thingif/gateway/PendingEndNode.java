package com.kii.thingif.gateway;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PendingEndNode implements Parcelable {
    private String vendorThingID;
    private JSONObject thingProperties;

    PendingEndNode(JSONObject json) {
        this.vendorThingID = json.optString("vendorThingID");
        this.thingProperties = json.optJSONObject("thingProperties");
    }
    public String getVendorThingID() {
        return this.vendorThingID;
    }
    public JSONObject getThingProperties() {
        return this.thingProperties;
    }

    protected PendingEndNode(Parcel in) {
        this.vendorThingID = in.readString();
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
        dest.writeString(vendorThingID);
        if (this.thingProperties != null) {
            dest.writeString(this.thingProperties.toString());
        }
    }
}
