package com.kii.iotcloud;

import android.os.Parcel;
import android.os.Parcelable;

import com.kii.iotcloud.trigger.Trigger;

public class Target implements Parcelable {

    private final TypedID typedID;
    private final String accessToken;

    public Target(TypedID typedID, String accessToken) {
        this.typedID = typedID;
        this.accessToken = accessToken;
    }
    public TypedID getTypedID() {
        return this.typedID;
    }
    public String getAccessToken() {
        return this.accessToken;
    }

    // Implementation of Parcelable
    protected Target(Parcel in) {
        this.typedID = in.readParcelable(TypedID.class.getClassLoader());
        this.accessToken = in.readString();
    }
    public static final Creator<Target> CREATOR = new Creator<Target>() {
        @Override
        public Target createFromParcel(Parcel in) {
            return new Target(in);
        }

        @Override
        public Target[] newArray(int size) {
            return new Target[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(typedID, i);
        parcel.writeString(accessToken);
    }
}
