package com.kii.iotcloud;

import android.os.Parcel;
import android.os.Parcelable;

public class TypedID implements Parcelable {

    private String type;
    private String ID;
    private String qualifiedID;

    public TypedID(String type, String ID) {
        this.type = type;
        this.ID = ID;
        this.qualifiedID = this.type + ":" + this.ID;
    }

    public String getType() {
        return this.type;
    }

    public String getID() {
        return this.ID;
    }

    @Override
    public String toString() {
        return this.qualifiedID;
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
