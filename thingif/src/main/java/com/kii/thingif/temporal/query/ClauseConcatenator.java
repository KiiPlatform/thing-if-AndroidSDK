package com.kii.thingif.temporal.query;

import com.kii.thingif.temporal.query.QueryClause;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClauseConcatenator {
    public static QueryClause and(QueryClause... claues) {
        final JSONObject o = new JSONObject();
        JSONArray a = new JSONArray();
        for (QueryClause c: claues) {
            a.put(c.toJSONObject());
        }
        try {
            // Would be wrong format.
            o.put("and clauses", a);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new QueryClause() {
            @Override
            public JSONObject toJSONObject() {
                return o;
            }
        };
    }
}
