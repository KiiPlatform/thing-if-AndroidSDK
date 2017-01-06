package com.kii.thingif.query.clause;

import org.json.JSONObject;

public interface Clause extends com.kii.thingif.internal.clause.Clause {
    JSONObject toJSONObject();
}
