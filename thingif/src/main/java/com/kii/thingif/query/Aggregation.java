package com.kii.thingif.query;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represent aggregation
 */
public class Aggregation {
    private AggregationType type;
    private String responseField;
    private String field;
    private String fieldType;

    /**
     * Initialize Aggregation
     * @param type Type of aggregation
     * @param responseField
     * @param field
     * @param fieldType
     */
    public Aggregation(
            AggregationType type,
            String responseField,
            String field,
            String fieldType
    ) {
        this.type = type;
        this.responseField = responseField;
        this.field = field;
        this.fieldType = fieldType;
    }

    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("type", this.type);
            ret.put("responseField", this.responseField);
            ret.put("field", this.field);
            ret.put("fieldType", this.fieldType);
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }
}
