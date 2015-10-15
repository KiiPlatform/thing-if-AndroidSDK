package com.kii.iotcloud;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Represents owner of things.
 * All {@link com.kii.iotcloud.IoTCloudAPI} operations will be performed with the owner's access token.
 */
public class Owner implements Parcelable {

    private final TypedID typedID;
    private final String accessToken;

    public Owner(@NonNull TypedID ownerID, @NonNull String accessToken) {
        if (ownerID == null) {
            throw new IllegalArgumentException("ownerID is null");
        }
        if (TextUtils.isEmpty(accessToken)) {
            throw new IllegalArgumentException("accessToken is null or empty");
        }
        this.typedID = ownerID;
        this.accessToken = accessToken;
    }

    public TypedID getTypedID() {
        return this.typedID;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    // Implementation of Parcelable
    protected Owner(Parcel in) {
        this.typedID = in.readParcelable(TypedID.class.getClassLoader());
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
        parcel.writeParcelable(this.typedID, i);
        parcel.writeString(this.accessToken);
    }
}
