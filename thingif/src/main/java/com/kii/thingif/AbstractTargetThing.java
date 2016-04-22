package com.kii.thingif;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public abstract class AbstractTargetThing implements TargetThing {
    private final TypedID typedID;

    public AbstractTargetThing(@NonNull String thingID) {
        if (TextUtils.isEmpty(thingID)) {
            throw new IllegalArgumentException("thingID is null or empty");
        }
        this.typedID = new TypedID(TypedID.Types.THING, thingID);
    }
    @Override
    public TypedID getTypedID() {
        return this.typedID;
    }
    @Override
    public String getThingID() {
        return this.typedID.getID();
    }

    // Implementation of Parcelable
    protected AbstractTargetThing(Parcel in) {
        this.typedID = in.readParcelable(TypedID.class.getClassLoader());
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.typedID, i);
    }
}
