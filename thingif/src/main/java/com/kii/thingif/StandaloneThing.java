package com.kii.thingif;

import android.os.Parcel;
import android.support.annotation.NonNull;

public class StandaloneThing extends AbstractTargetThing {
    private final String accessToken;
    public StandaloneThing(@NonNull String thingID, String accessToken) {
        super(thingID);
        this.accessToken = accessToken;
    }
    @Override
    public String getAccessToken() {
        return this.accessToken;
    }
    // Implementation of Parcelable
    protected StandaloneThing(Parcel in) {
        super(in);
        this.accessToken = in.readString();
    }
    public static final Creator<StandaloneThing> CREATOR = new Creator<StandaloneThing>() {
        @Override
        public StandaloneThing createFromParcel(Parcel in) {
            return new StandaloneThing(in);
        }

        @Override
        public StandaloneThing[] newArray(int size) {
            return new StandaloneThing[size];
        }
    };
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.accessToken);
    }
}
