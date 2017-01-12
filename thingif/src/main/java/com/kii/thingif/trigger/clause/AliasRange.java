package com.kii.thingif.trigger.clause;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.kii.thingif.query.clause.Range;

import org.json.JSONException;
import org.json.JSONObject;

public class AliasRange extends Range implements AliasClause {

    private final String alias;

    public AliasRange(String field,
                      Long upperLimit,
                      Boolean upperIncluded,
                      Long lowerLimit,
                      Boolean lowerIncluded,
                      String alias) {
        super(field, upperLimit, upperIncluded, lowerLimit, lowerIncluded);
        this.alias = alias;
    }
    public static AliasRange range(
            @NonNull String field,
            Number upperLimit,
            Boolean upperIncluded,
            Number lowerLimit,
            Boolean lowerIncluded, 
            String alias) {
        return new AliasRange(
                field,
                upperLimit.longValue(),
                upperIncluded, lowerLimit.longValue(),
                lowerIncluded,
                alias);
    }
    public static AliasRange greaterThan(
            @NonNull String field,
            @NonNull Number lowerLimit,
            @NonNull String alias) {
        return new AliasRange(
                field,
                null,
                null,
                lowerLimit.longValue(),
                Boolean.FALSE,
                alias);
    }
    public static AliasRange greaterThanEquals(
            @NonNull String field,
            @NonNull Number lowerLimit,
            @NonNull String alias) {
        return new AliasRange(
                field,
                null,
                null,
                lowerLimit.longValue(),
                Boolean.TRUE,
                alias);
    }
    public static AliasRange lessThan(
            @NonNull String field,
            @NonNull Number upperLimit,
            @NonNull String alias) {
        return new AliasRange(
                field,
                upperLimit.longValue(),
                Boolean.FALSE,
                null,
                null,
                alias);
    }
    public static AliasRange lessThanEquals(
            @NonNull String field,
            @NonNull Number upperLimit,
            @NonNull String alias) {
        return new AliasRange(
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
        AliasRange range = (AliasRange) o;
        return this.alias.equals(range.alias);
     }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + alias.hashCode();
        return result;
    }

    // Implementation of Parcelable
    private AliasRange(Parcel in) {
        super(in);
        this.alias = in.readString();
    }
    public static final Creator<AliasRange> CREATOR = new Creator<AliasRange>() {
        @Override
        public AliasRange createFromParcel(Parcel in) {
            return new AliasRange(in);
        }

        @Override
        public AliasRange[] newArray(int size) {
            return new AliasRange[size];
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
