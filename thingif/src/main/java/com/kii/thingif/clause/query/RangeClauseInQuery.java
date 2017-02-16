package com.kii.thingif.clause.query;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.clause.base.BaseRange;

import org.json.JSONObject;

public class RangeClauseInQuery implements QueryClause, BaseRange {
    private @NonNull String field;
    private @Nullable Number upperLimit;
    private @Nullable Number lowerLimit;
    private @Nullable Boolean upperIncluded;
    private @Nullable Boolean lowerIncluded;

    private volatile int hashCode; // cached hashcode for performance

    private RangeClauseInQuery(
            @NonNull String field,
            @Nullable Number upperLimit,
            @Nullable Boolean upperIncluded,
            @Nullable Number lowerLimit,
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
                upperLimit,
                upperIncluded,
                lowerLimit,
                lowerIncluded);
    }
    public static RangeClauseInQuery greaterThan(
            @NonNull String field,
            @NonNull Number lowerLimit) {
        return new RangeClauseInQuery(
                field,
                null,
                null,
                lowerLimit,
                Boolean.FALSE);
    }
    public static RangeClauseInQuery greaterThanOrEqualTo(
            @NonNull String field,
            @NonNull Number lowerLimit) {
        return new RangeClauseInQuery(
                field,
                null,
                null,
                lowerLimit,
                Boolean.TRUE);
    }
    public static RangeClauseInQuery lessThan(
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new RangeClauseInQuery(
                field,
                upperLimit,
                Boolean.FALSE,
                null,
                null);
    }
    public static RangeClauseInQuery lessThanOrEqualTo(
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new RangeClauseInQuery(
                field,
                upperLimit,
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.field);
        dest.writeValue(this.lowerLimit);
        dest.writeValue(this.upperLimit);
        dest.writeValue(this.lowerIncluded);
        dest.writeValue(this.upperIncluded);
    }

    private RangeClauseInQuery(Parcel in) {
        this.field = in.readString();
        this.lowerLimit = (Number) in.readValue(Number.class.getClassLoader());
        this.upperLimit = (Number) in.readValue(Number.class.getClassLoader());
        this.lowerIncluded = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.upperIncluded = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<RangeClauseInQuery> CREATOR = new Creator<RangeClauseInQuery>() {
        @Override
        public RangeClauseInQuery createFromParcel(Parcel source) {
            return new RangeClauseInQuery(source);
        }

        @Override
        public RangeClauseInQuery[] newArray(int size) {
            return new RangeClauseInQuery[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof RangeClauseInQuery)) return false;

        RangeClauseInQuery range = (RangeClauseInQuery) o;

        if(this.upperLimit != null?
                !this.upperLimit.equals(range.upperLimit) :
                range.upperLimit != null) {
            return false;
        }

        if(this.upperIncluded != null?
                !this.upperIncluded.equals(range.upperIncluded) :
                range.upperIncluded != null) {
            return false;
        }

        if(this.lowerLimit != null?
                !this.lowerLimit.equals(range.lowerLimit) :
                range.lowerLimit != null) {
            return false;
        }

        if(this.lowerIncluded != null?
                !this.lowerIncluded.equals(range.lowerIncluded) :
                range.lowerIncluded != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result +
                    (this.lowerIncluded != null? this.lowerIncluded.hashCode(): 0);
            result = 31 * result +
                    (this.lowerLimit != null? this.lowerLimit.hashCode(): 0);
            result = 31 * result +
                    (this.upperIncluded != null? this.upperIncluded.hashCode(): 0);
            result = 31 * result +
                    (this.upperLimit != null? this.upperLimit.hashCode(): 0);

            this.hashCode = result;
        }
        return result;
    }
}
