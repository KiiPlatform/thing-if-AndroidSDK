package com.kii.iotcloud.trigger.statement;

import org.json.JSONException;
import org.json.JSONObject;

public class Equals extends Statement {

    private String field;
    private Object value;
    public Equals(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public Equals(String field, long value) {
        this.field = field;
        this.value = value;
    }

    public Equals(String field, boolean value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        JSONObject kv = new JSONObject();
        try {
            kv.put("field", this.field);
            kv.put("value", this.value);
            ret.put("=", kv);
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }
}
