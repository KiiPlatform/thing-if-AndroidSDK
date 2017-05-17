package com.kii.thingiftrait.gateway;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingiftrait.AbstractThing;

public class EndNode extends AbstractThing {
    private final String accessToken;
    public EndNode(@NonNull String thingID, @NonNull String vendorThingID, @Nullable String accessToken) {
        super(thingID, vendorThingID);
        this.accessToken = accessToken;
    }
    @Override
    public String getAccessToken() {
        return this.accessToken;
    }
    // Implementation of Parcelable
    protected EndNode(Parcel in) {
        super(in);
        this.accessToken = in.readString();
    }
    public static final Creator<EndNode> CREATOR = new Creator<EndNode>() {
        @Override
        public EndNode createFromParcel(Parcel in) {
            return new EndNode(in);
        }

        @Override
        public EndNode[] newArray(int size) {
            return new EndNode[size];
        }
    };
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.accessToken);
    }
}
