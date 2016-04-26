package com.kii.thingif.gateway;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.kii.thingif.AbstractTargetThing;

public class Gateway extends AbstractTargetThing {
    public Gateway(@NonNull String thingID) {
        super(thingID);
    }
    @Override
    public String getAccessToken() {
        return null;
    }
    // Implementation of Parcelable
    protected Gateway(Parcel in) {
        super(in);
    }
    public static final Creator<Gateway> CREATOR = new Creator<Gateway>() {
        @Override
        public Gateway createFromParcel(Parcel in) {
            return new Gateway(in);
        }

        @Override
        public Gateway[] newArray(int size) {
            return new Gateway[size];
        }
    };
}
