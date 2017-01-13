package com.kii.thingif.clause.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.clause.base.BaseRange;

import org.json.JSONObject;

public class RangeClauseInQuery implements QueryClause, BaseRange {
    private @NonNull String field;
    private @Nullable Long upperLimit;
    private @Nullable Long lowerLimit;
    private @Nullable Boolean upperIncluded;
    private @Nullable Boolean lowerIncluded;

    private RangeClauseInQuery(
            @NonNull String field,
            @Nullable Long upperLimit,
            @Nullable Boolean upperIncluded,
            @Nullable Long lowerLimit,
            @Nullable Boolean lowerIncluded) {
        this.field = field;
        this.upperIncluded = upperIncluded;
        this.upperLimit = upperLimit;
        this.lowerIncluded = lowerIncluded;
        this.lowerLimit = lowerLimit;
    }
    public static RangeClauseInQuery range(
            @NonNull String field,
            @NonNull Number upperLimit,
            @NonNull Boolean upperIncluded,
            @NonNull Number lowerLimit,
            @NonNull Boolean lowerIncluded) {
        return new RangeClauseInQuery(
                field,
                upperLimit.longValue(),
                upperIncluded,
                lowerLimit.longValue(),
                lowerIncluded);
    }
    public static RangeClauseInQuery greaterThan(
            @NonNull String field,
            @NonNull Number lowerLimit) {
        return new RangeClauseInQuery(
                field,
                null,
                null,
                lowerLimit.longValue(),
                Boolean.FALSE);
    }
    public static RangeClauseInQuery greaterThanEquals(
            @NonNull String field,
            @NonNull Number lowerLimit) {
        return new RangeClauseInQuery(
                field,
                null,
                null,
                lowerLimit.longValue(),
                Boolean.TRUE);
    }
    public static RangeClauseInQuery lessThan(
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new RangeClauseInQuery(
                field,
                upperLimit.longValue(),
                Boolean.FALSE,
                null,
                null);
    }
    public static RangeClauseInQuery lessThanEquals(
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new RangeClauseInQuery(
                field,
                upperLimit.longValue(),
                Boolean.TRUE,
                null,
                null);
    }

    @Override
    @NonNull
    public String getField() {
        return this.field;
    }

    @Override
    @Nullable
    public Boolean getLowerIncluded() {
        return this.lowerIncluded;
    }

    @Override
    @Nullable
    public Long getUpperLimit() {
        return this.upperLimit;
    }

    @Override
    @Nullable
    public Long getLowerLimit() {
        return this.lowerLimit;
    }

    @Override
    @Nullable
    public Boolean getUpperIncluded() {
        return this.upperIncluded;
    }

    @Override
    public JSONObject toJSONObject() {
        //TODO: implement me
        return null;
    }
}
