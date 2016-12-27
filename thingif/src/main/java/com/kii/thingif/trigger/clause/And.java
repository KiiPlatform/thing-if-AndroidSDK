package com.kii.thingif.trigger.clause;

import org.json.JSONObject;

public class And extends com.kii.thingif.internal.clause.And implements Clause{
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
    @Override
    public int hashCode() {
        return super.hashCode();
    }

}