package com.kii.iotcloud;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Owner implements Parcelable {

    private final TypedID ID;
    private final String accessToken;

    public Owner(TypedID ownerID, String accessToken) {
        if (ownerID == null) {
            throw new IllegalArgumentException("ownerID is null");
        }
        if (TextUtils.isEmpty(accessToken)) {
            throw new IllegalArgumentException("accessToken is null or empty");
        }
        this.ID = ownerID;
        this.accessToken = accessToken;
    }

    public TypedID getID() {
        return this.ID;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    // Implementation of Parcelable
    protected Owner(Parcel in) {
        this.ID = in.readParcelable(TypedID.class.getClassLoader());
        this.accessToken = in.readString();
    }
    public static final Creator<Owner> CREATOR = new Creator<Owner>() {
        @Override
        public Owner createFromParcel(Parcel in) {
            return new Owner(in);
        }

        @Override
        public Owner[] newArray(int size) {
            return new Owner[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.ID, i);
        parcel.writeString(this.accessToken);
    }
}
