package com.kii.thingif;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public abstract class AbstractThing implements TargetThing {
    private final TypedID typedID;
    private final String vendorThingID;

    public AbstractThing(@NonNull String thingID, @NonNull String vendorThingID) {
        if (TextUtils.isEmpty(thingID)) {
            throw new IllegalArgumentException("thingID is null or empty");
        }
        this.typedID = new TypedID(TypedID.Types.THING, thingID);
        this.vendorThingID = vendorThingID;
    }
    @Override
    public TypedID getTypedID() {
        return this.typedID;
    }
    @Override
    public String getThingID() {
        return this.typedID.getID();
    }
    @Override
    public String getVendorThingID() {
        return this.vendorThingID;
    }

    // Implementation of Parcelable
    protected AbstractThing(Parcel in) {
        this.typedID = in.readParcelable(TypedID.class.getClassLoader());
        this.vendorThingID = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.typedID, i);
        parcel.writeString(this.vendorThingID);
    }
}
