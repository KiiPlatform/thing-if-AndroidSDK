package com.kii.thingif.trigger.clause;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Range extends com.kii.thingif.query.clause.Range implements Clause{

    private final String alias;

    public Range(String field, 
                 Long upperLimit,
                 Boolean upperIncluded, 
                 Long lowerLimit, 
                 Boolean lowerIncluded, 
                 String alias) {
        super(field, upperLimit, upperIncluded, lowerLimit, lowerIncluded);
        this.alias = alias;
    }
    public static  Range range(
            @NonNull String field,
            Number upperLimit,
            Boolean upperIncluded,
            Number lowerLimit,
            Boolean lowerIncluded, 
            String alias) {
        return new Range(
                field,
                upperLimit.longValue(),
                upperIncluded, lowerLimit.longValue(),
                lowerIncluded,
                alias);
    }
    public static Range greaterThan(
            @NonNull String field,
            @NonNull Number lowerLimit,
            @NonNull String alias) {
        return new Range(
                field,
                null,
                null,
                lowerLimit.longValue(),
                Boolean.FALSE,
                alias);
    }
    public static Range greaterThanEquals(
            @NonNull String field,
            @NonNull Number lowerLimit,
            @NonNull String alias) {
        return new Range(
                field,
                null,
                null,
                lowerLimit.longValue(),
                Boolean.TRUE,
                alias);
    }
    public static Range lessThan(
            @NonNull String field,
            @NonNull Number upperLimit,
            @NonNull String alias) {
        return new Range(
                field,
                upperLimit.longValue(),
                Boolean.FALSE,
                null,
                null,
                alias);
    }
    public static Range lessThanEquals(
            @NonNull String field,
            @NonNull Number upperLimit,
            @NonNull String alias) {
        return new Range(
                field,
                upperLimit.longValue(),
                Boolean.TRUE,
                null,
                null,
                alias);
    }

    public String getField() {
        return this.field;
    }
    public Long getUpperLimit() {
        return this.upperLimit;
    }
    public Long getLowerLimit() {
        return this.lowerLimit;
    }
    public Boolean getUpperIncluded() {
        return this.upperIncluded;
    }
    public Boolean getLowerIncluded() {
        return this.lowerIncluded;
    }
    public String getAlias() {
        return alias;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = super.toJSONObject();
        try {
            ret.put("alias", this.alias);
        }catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
        return  ret;
    }

    @Override
    public boolean equals(Object o) {
        if(!super.equals(o)){
            return false;
        }
        Range range = (Range) o;
        return this.alias.equals(range.alias);
     }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + alias.hashCode();
        return result;
    }

    // Implementation of Parcelable
    private Range(Parcel in) {
        super(in);
        this.alias = in.readString();
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
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.alias);
    }
}
