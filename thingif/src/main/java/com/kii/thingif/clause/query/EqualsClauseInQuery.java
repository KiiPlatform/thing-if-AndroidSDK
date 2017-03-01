package com.kii.thingif.clause.query;

import android.support.annotation.NonNull;

import com.kii.thingif.clause.base.BaseEquals;

public class EqualsClauseInQuery implements BaseEquals, QueryClause {
    private @NonNull String field;
    private @NonNull Object value;

    private transient volatile int hashCode; // cached hashcode for performance

    public EqualsClauseInQuery(
            @NonNull String field,
            @NonNull String value) {
        this.field = field;
        this.value = value;
    }

    public EqualsClauseInQuery(
            @NonNull String field,
            long value) {
        this.field = field;
        this.value = value;
    }

    public EqualsClauseInQuery(@NonNull String field, boolean value) {
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
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof EqualsClauseInQuery)) return false;
        EqualsClauseInQuery equals = (EqualsClauseInQuery) o;
        return field.equals(equals.field) &&
                value.equals(equals.value);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.field.hashCode();
            result = 31 * result + this.value.hashCode();
            this.hashCode = result;
        }
        return result;
    }
}
