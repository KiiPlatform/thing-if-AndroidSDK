package com.kii.thingif.query.clause;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONObject;

public class Range extends com.kii.thingif.internal.clause.Range implements Clause{


    private Range(String field,
                 Long upperLimit,
                 Boolean upperIncluded, 
                 Long lowerLimit, 
                 Boolean lowerIncluded) {
        super(field, upperLimit,upperIncluded,lowerLimit,lowerIncluded);
    }
    public static  Range range(
            @NonNull String field,
            Number upperLimit,
            Boolean upperIncluded,
            Number lowerLimit,
            Boolean lowerIncluded) {
        return new Range(
                field,
                upperLimit.longValue(),
                upperIncluded,
                lowerLimit.longValue(),
                lowerIncluded);
    }
    public static Range greaterThan(
            @NonNull String field,
            @NonNull Number lowerLimit) {
        return new Range(
                field,
                null,
                null,
                lowerLimit.longValue(),
                Boolean.FALSE);
    }
    public static Range greaterThanEquals(
            @NonNull String field,
            @NonNull Number lowerLimit) {
        return new Range(
                field,
                null,
                null,
                lowerLimit.longValue(),
                Boolean.TRUE);
    }
    public static Range lessThan(
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new Range(
                field,
                upperLimit.longValue(),
                Boolean.FALSE,
                null,
                null);
    }
    public static Range lessThanEquals(
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new Range(
                field,
                upperLimit.longValue(),
                Boolean.TRUE,
                null,
                null);
    }

    public String getField() {
        return super.getField();
    }
    public Long getUpperLimit() {
        return super.getUpperLimit();
    }
    public Long getLowerLimit() {
        return super.getLowerLimit();
    }
    public Boolean getUpperIncluded() {
        return super.getUpperIncluded();
    }
    public Boolean getLowerIncluded() {
        return super.getLowerIncluded();
    }

    @Override
    public JSONObject toJSONObject() {
        return super.toJSONObject();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private Range(Parcel in) {
        super(in);
    }
    public static final Creator<Range> CREATOR = new Creator<Range>() {
        @Override
        public Range createFromParcel(Parcel in) {
            return new Range(in);
        }

        @Override
        public Range[] newArray(int size) {
            return new Range[size];
        }
    };
}
