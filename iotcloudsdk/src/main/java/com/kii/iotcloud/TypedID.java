package com.kii.iotcloud;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

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
        public static Types fromString(String s) {
            for (Types type : values()) {
                if (type.value.equals(s)) {
                    return type;
                }
            }
            return null;
        }
    }

    private final Types type;
    private final String ID;
    private final String qualifiedID;

    public static TypedID fromString(String id) {
        if (TextUtils.isEmpty(id)) {
            throw new IllegalArgumentException("id is null or empty");
        }
        String[] ids = id.split(":");
        if (ids.length != 2) {
            throw new IllegalArgumentException(id + " is invalid format");
        }
        Types type = Types.fromString(ids[0]);
        if (type == null) {
            throw new IllegalArgumentException(ids[0] + " is unknown type");
        }
        return new TypedID(type, ids[1]);
    }

    public TypedID(Types type, String ID) {
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        }
        if (TextUtils.isEmpty(ID)) {
            throw new IllegalArgumentException("ID is null or empty");
        }
        this.type = type;
        this.ID = ID;
        this.qualifiedID = this.type.getValue() + ":" + this.ID;
    }

    protected TypedID(Parcel in) {
        this.type = Types.fromString(in.readString());
        this.ID = in.readString();
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


    public static final Creator<TypedID> CREATOR = new Creator<TypedID>() {
        @Override
        public TypedID createFromParcel(Parcel in) {
            return new TypedID(in);
        }

        @Override
        public TypedID[] newArray(int size) {
            return new TypedID[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.type.getValue());
        parcel.writeString(this.ID);
    }

}
