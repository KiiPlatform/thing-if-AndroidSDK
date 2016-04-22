package com.kii.thingif;

import android.os.Parcel;
import android.support.annotation.NonNull;

public class TargetStandaloneThing extends AbstractTargetThing {
    private final String accessToken;
    public TargetStandaloneThing(@NonNull String thingID, String accessToken) {
        super(thingID);
        this.accessToken = accessToken;
    }
    @Override
    public String getAccessToken() {
        return this.accessToken;
    }
    // Implementation of Parcelable
    protected TargetStandaloneThing(Parcel in) {
        super(in);
        this.accessToken = in.readString();
    }
    public static final Creator<TargetStandaloneThing> CREATOR = new Creator<TargetStandaloneThing>() {
        @Override
        public TargetStandaloneThing createFromParcel(Parcel in) {
            return new TargetStandaloneThing(in);
        }

        @Override
        public TargetStandaloneThing[] newArray(int size) {
            return new TargetStandaloneThing[size];
        }
    };
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.accessToken);
    }
}
