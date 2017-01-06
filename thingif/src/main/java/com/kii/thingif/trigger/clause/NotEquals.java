package com.kii.thingif.trigger.clause;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONObject;

public class NotEquals extends com.kii.thingif.internal.clause.NotEquals implements Clause {
    public NotEquals(@NonNull Equals equals) {
        super(equals);
    }
    @Override
    public JSONObject toJSONObject() {
        return super.toJSONObject();
    }
    public Equals getEquals() {
        return (Equals) super.getEquals();
    }

    @Override
    public boolean equals(Object o) {
        return  super.equals(o);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private NotEquals(Parcel in) {
        super(in);
    }
    public static final Creator<NotEquals> CREATOR = new Creator<NotEquals>() {
        @Override
        public NotEquals createFromParcel(Parcel in) {
            return new NotEquals(in);
        }

        @Override
        public NotEquals[] newArray(int size) {
            return new NotEquals[size];
        }
    };
}
