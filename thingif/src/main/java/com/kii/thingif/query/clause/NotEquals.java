package com.kii.thingif.query.clause;

import android.support.annotation.NonNull;

import org.json.JSONObject;

public class NotEquals extends com.kii.thingif.internal.clause.NotEquals implements Clause{
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
}
