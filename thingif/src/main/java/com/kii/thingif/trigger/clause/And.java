package com.kii.thingif.trigger.clause;

import android.os.Parcel;

import org.json.JSONObject;

public class And extends com.kii.thingif.query.clause.And implements Clause{
    public And(Clause... clauses) {
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

    // Implementation of Parcelable
    private And(Parcel in) {
        super(in);
    }

    public static final Creator<And> CREATOR = new Creator<And>() {
        @Override
        public And createFromParcel(Parcel in) {
            return new And(in);
        }

        @Override
        public And[] newArray(int size) {
            return new And[size];
        }
    };
    @Override
    public int hashCode() {
        return super.hashCode();
    }

}