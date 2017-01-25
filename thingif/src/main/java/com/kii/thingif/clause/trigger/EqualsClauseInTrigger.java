package com.kii.thingif.clause.trigger;

import android.support.annotation.NonNull;

import com.kii.thingif.clause.base.BaseEquals;

import org.json.JSONObject;

public class EqualsClauseInTrigger implements BaseEquals, TriggerClause{
    private @NonNull String field;
    private @NonNull Object value;
    private @NonNull String alias;

    public EqualsClauseInTrigger(
            @NonNull String alias,
            @NonNull String field,
            @NonNull String value) {
        this.alias = alias;
        this.field = field;
        this.value = value;
    }

    public EqualsClauseInTrigger(
            @NonNull String alias,
            @NonNull String field,
            long value) {
        this.alias = alias;
        this.field = field;
        this.value = value;
    }

    public EqualsClauseInTrigger(
            @NonNull String alias,
            @NonNull String field,
            boolean value) {
        this.alias = alias;
        this.field = field;
        this.value = value;
    }

    @NonNull
    public String getField() {
        return this.field;
    }

    @NonNull
    public Object getValue() {
        return this.value;
    }

    @Override
    public JSONObject toJSONObject() {
        //TODO: implement me
        return null;
    }
}
