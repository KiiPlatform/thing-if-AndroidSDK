package com.kii.iotcloud;

import android.os.Parcel;
import android.os.Parcelable;

public class TypedID implements Parcelable {

    public enum Types {
        USER("user"),
        GRPUP("group");
        private final String value;
        private Types(String value) {
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }
    }

    private final Types type;
    private final String ID;
    private final String qualifiedID;

    public TypedID(Types type, String ID) {
        this.type = type;
        this.ID = ID;
        this.qualifiedID = this.type.getValue() + ":" + this.ID;
    }

    public Types getType() {
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
