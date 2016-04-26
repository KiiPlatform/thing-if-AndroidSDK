package com.kii.thingif.gateway;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.kii.thingif.AbstractTargetThing;

public class Endnode extends AbstractTargetThing {
    public Endnode(@NonNull String thingID) {
        super(thingID);
    }
    @Override
    public String getAccessToken() {
        return null;
    }
    // Implementation of Parcelable
    protected Endnode(Parcel in) {
        super(in);
    }
    public static final Creator<Endnode> CREATOR = new Creator<Endnode>() {
        @Override
        public Endnode createFromParcel(Parcel in) {
            return new Endnode(in);
        }

        @Override
        public Endnode[] newArray(int size) {
            return new Endnode[size];
        }
    };
}
