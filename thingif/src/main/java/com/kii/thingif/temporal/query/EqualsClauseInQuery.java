package com.kii.thingif.temporal.query;

import com.kii.thingif.temporal.base.BaseEquals;

import org.json.JSONObject;

public class EqualsClauseInQuery implements BaseEquals, QueryClause {
    private String fieldName;
    private Object value;
    public EqualsClauseInQuery(String fieldName, Object value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public String getFieldName() {
        return null;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }
}
