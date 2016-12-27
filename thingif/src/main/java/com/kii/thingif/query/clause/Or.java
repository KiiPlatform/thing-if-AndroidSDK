package com.kii.thingif.query.clause;

import org.json.JSONObject;

public class Or extends com.kii.thingif.internal.clause.Or implements Clause{
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
}
