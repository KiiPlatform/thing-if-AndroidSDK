package com.kii.thingif.temporal.trigger;

import com.kii.thingif.temporal.base.BaseEquals;

import org.json.JSONObject;

public class EqualsClauseInTrigger implements BaseEquals, TriggerClause{
    public EqualsClauseInTrigger(String alias, String fieldName, Object value) {

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
