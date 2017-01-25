package com.kii.thingif.clause.trigger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.clause.base.BaseRange;

import org.json.JSONObject;

public class RangeClauseInTrigger implements BaseRange, TriggerClause {
    private @NonNull String alias;
    private @NonNull String field;
    private @Nullable Number upperLimit;
    private @Nullable Number lowerLimit;
    private @Nullable Boolean upperIncluded;
    private @Nullable Boolean lowerIncluded;


    private RangeClauseInTrigger(
            @NonNull String alias,
            @NonNull String field,
            @Nullable Number upperLimit,
            @Nullable Boolean upperIncluded,
            @Nullable Number lowerLimit,
            @Nullable Boolean lowerIncluded) {
        this.alias = alias;
        this.field = field;
        this.upperIncluded = upperIncluded;
        this.upperLimit = upperLimit;
        this.lowerIncluded = lowerIncluded;
        this.lowerLimit = lowerLimit;
    }
    public static RangeClauseInTrigger range(
            @NonNull String alias,
            @NonNull String field,
            @NonNull Number upperLimit,
            @NonNull Boolean upperIncluded,
            @NonNull Number lowerLimit,
            @NonNull Boolean lowerIncluded) {
        return new RangeClauseInTrigger(
                alias,
                field,
                upperLimit.longValue(),
                upperIncluded,
                lowerLimit.longValue(),
                lowerIncluded);
    }
    public static RangeClauseInTrigger greaterThan(
            @NonNull String alias,
            @NonNull String field,
            @NonNull Number lowerLimit) {
        return new RangeClauseInTrigger(
                alias,
                field,
                null,
                null,
                lowerLimit.longValue(),
                Boolean.FALSE);
    }
    public static RangeClauseInTrigger greaterThanOrEqualTo(
            @NonNull String alias,
            @NonNull String field,
            @NonNull Number lowerLimit) {
        return new RangeClauseInTrigger(
                alias,
                field,
                null,
                null,
                lowerLimit.longValue(),
                Boolean.TRUE);
    }
    public static RangeClauseInTrigger lessThan(
            @NonNull String alias,
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new RangeClauseInTrigger(
                alias,
                field,
                upperLimit.longValue(),
                Boolean.FALSE,
                null,
                null);
    }
    public static RangeClauseInTrigger lessThanOrEqualTo(
            @NonNull String alias,
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new RangeClauseInTrigger(
                alias,
                field,
                upperLimit.longValue(),
                Boolean.TRUE,
                null,
                null);
    }

    @NonNull
    public String getAlias() {
        return this.alias;
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
    public Number getUpperLimit() {
        return this.upperLimit;
    }

    @Override
    @Nullable
    public Number getLowerLimit() {
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
