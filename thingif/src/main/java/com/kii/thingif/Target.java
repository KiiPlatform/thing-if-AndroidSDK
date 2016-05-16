package com.kii.thingif;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Target implements Parcelable {

    private final TypedID typedID;
    private final String accessToken;

    public Target(@NonNull TypedID ID, @Nullable String accessToken) {
        if (ID == null) {
            throw new IllegalArgumentException("ID is null");
        }
        this.typedID = ID;
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
        parcel.writeParcelable(this.typedID, i);
        parcel.writeString(this.accessToken);
    }
}
