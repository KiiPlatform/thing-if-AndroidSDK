package com.kii.thingif.gateway;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.kii.thingif.AbstractTargetThing;

public class TargetGatewayThing extends AbstractTargetThing {
    public TargetGatewayThing(@NonNull String thingID) {
        super(thingID);
    }
    @Override
    public String getAccessToken() {
        return null;
    }
    // Implementation of Parcelable
    protected TargetGatewayThing(Parcel in) {
        super(in);
    }
    public static final Creator<TargetGatewayThing> CREATOR = new Creator<TargetGatewayThing>() {
        @Override
        public TargetGatewayThing createFromParcel(Parcel in) {
            return new TargetGatewayThing(in);
        }

        @Override
        public TargetGatewayThing[] newArray(int size) {
            return new TargetGatewayThing[size];
        }
    };
}
