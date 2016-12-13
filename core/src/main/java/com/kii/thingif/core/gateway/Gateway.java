package com.kii.thingif.core.gateway;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.kii.thingif.core.AbstractThing;

public class Gateway extends AbstractThing {
    public Gateway(@NonNull String thingID, @NonNull String vendorThingID) {
        super(thingID, vendorThingID);
    }
    @Override
    public String getAccessToken() {
        return null;
    }
    // Implementation of Parcelable
    protected Gateway(Parcel in) {
        super(in);
    }
    public static final Creator<Gateway> CREATOR = new Creator<Gateway>() {
        @Override
        public Gateway createFromParcel(Parcel in) {
            return new Gateway(in);
        }

        @Override
        public Gateway[] newArray(int size) {
            return new Gateway[size];
        }
    };
}
