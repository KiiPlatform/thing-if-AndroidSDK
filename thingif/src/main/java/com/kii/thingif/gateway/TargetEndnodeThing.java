package com.kii.thingif.gateway;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.kii.thingif.AbstractTargetThing;

public class TargetEndnodeThing extends AbstractTargetThing {
    public TargetEndnodeThing(@NonNull String thingID) {
        super(thingID);
    }
    @Override
    public String getAccessToken() {
        return null;
    }
    // Implementation of Parcelable
    protected TargetEndnodeThing(Parcel in) {
        super(in);
    }
    public static final Creator<TargetEndnodeThing> CREATOR = new Creator<TargetEndnodeThing>() {
        @Override
        public TargetEndnodeThing createFromParcel(Parcel in) {
            return new TargetEndnodeThing(in);
        }

        @Override
        public TargetEndnodeThing[] newArray(int size) {
            return new TargetEndnodeThing[size];
        }
    };
}
