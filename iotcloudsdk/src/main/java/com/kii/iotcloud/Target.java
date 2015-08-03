package com.kii.iotcloud;

import android.os.Parcel;
import android.os.Parcelable;

import com.kii.iotcloud.trigger.Trigger;

public class Target implements Parcelable {

    private TypedID typedID;
    private String accessToken;

    public Target(TypedID typedID) {
        this.typedID = typedID;
    }


    @Override
    public int describeContents() {
        // TODO: implement it.
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        // TODO: implement it.
    }

}
