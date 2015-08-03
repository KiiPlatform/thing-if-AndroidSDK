package com.kii.iotcloud.trigger;

import android.os.Parcel;
import android.os.Parcelable;

import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.command.Command;

public class Trigger implements Parcelable {

    public TypedID getTargetID() {
        // TODO: implement
        return null;
    }

    public boolean enabled() {
        // TODO: implement
        return false;
    }

    public Predicate getPredicate() {
        // TODO: implement
        return null;
    }

    public Command getCommand() {
        // TODO: implement
        return null;
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
