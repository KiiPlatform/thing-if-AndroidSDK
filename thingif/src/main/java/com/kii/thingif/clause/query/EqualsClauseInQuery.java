package com.kii.thingif.clause.query;

import android.support.annotation.NonNull;

import com.kii.thingif.clause.base.BaseEquals;

import org.json.JSONObject;

public class EqualsClauseInQuery implements BaseEquals, QueryClause {
    private @NonNull String field;
    private @NonNull Object value;

    public EqualsClauseInQuery(
            @NonNull String field,
            @NonNull String value) {
        this.field = field;
        this.value = value;
    }

    public EqualsClauseInQuery(
            @NonNull String field,
            @NonNull long value) {
        this.field = field;
        this.value = value;
    }

    public EqualsClauseInQuery(String field, boolean value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return this.field;
    }

    public Object getValue() {
        return this.value;
    }
}
