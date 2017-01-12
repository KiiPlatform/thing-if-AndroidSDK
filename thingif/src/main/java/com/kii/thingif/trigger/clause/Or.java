package com.kii.thingif.trigger.clause;

import android.os.Parcel;

import org.json.JSONObject;

public class Or extends com.kii.thingif.query.clause.Or implements Clause{
    public Or(Clause... clauses) {
        super(clauses);
    }
    @Override
    public JSONObject toJSONObject() {
        return super.toJSONObject();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private Or(Parcel in) {
        super(in);
    }
    public static final Creator<Or> CREATOR = new Creator<Or>() {
        @Override
        public Or createFromParcel(Parcel in) {
            return new Or(in);
        }

        @Override
        public Or[] newArray(int size) {
            return new Or[size];
        }
    };
}
