package com.kii.thingif.query.clause;

import android.support.annotation.NonNull;

import org.json.JSONObject;

public class Equals extends com.kii.thingif.internal.clause.Equals implements Clause{

    public Equals(@NonNull String field, String value) {
        super(field, value);
    }

    public Equals(String field, long value) {
        super(field, value);
    }

    public Equals(String field, boolean value) {
        super(field,value);
    }
    public String getField() {
        return super.getField();
    }
    public Object getValue() {
        return super.getValue();
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
}
