package com.kii.thingiftrait.clause.trigger;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingiftrait.clause.base.BaseRange;

public class RangeClauseInTrigger implements BaseRange, TriggerClause {
    private @NonNull String alias;
    private @NonNull String field;
    private @Nullable Number upperLimit;
    private @Nullable Number lowerLimit;
    private @Nullable Boolean upperIncluded;
    private @Nullable Boolean lowerIncluded;

    private transient volatile int hashCode; // cached hashcode for performance

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
                upperLimit,
                upperIncluded,
                lowerLimit,
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
                lowerLimit,
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
                lowerLimit,
                Boolean.TRUE);
    }
    public static RangeClauseInTrigger lessThan(
            @NonNull String alias,
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new RangeClauseInTrigger(
                alias,
                field,
                upperLimit,
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
                upperLimit,
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.alias);
        dest.writeString(this.field);
        dest.writeValue(this.lowerLimit);
        dest.writeValue(this.upperLimit);
        dest.writeValue(this.lowerIncluded);
        dest.writeValue(this.upperIncluded);
    }

    private RangeClauseInTrigger(Parcel in) {
        this.alias = in.readString();
        this.field = in.readString();
        this.lowerLimit = (Number) in.readValue(Number.class.getClassLoader());
        this.upperLimit = (Number) in.readValue(Number.class.getClassLoader());
        this.lowerIncluded = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.upperIncluded = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<RangeClauseInTrigger> CREATOR = new Creator<RangeClauseInTrigger>() {
        @Override
        public RangeClauseInTrigger createFromParcel(Parcel source) {
            return new RangeClauseInTrigger(source);
        }

        @Override
        public RangeClauseInTrigger[] newArray(int size) {
            return new RangeClauseInTrigger[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof RangeClauseInTrigger)) return false;

        RangeClauseInTrigger range = (RangeClauseInTrigger) o;

        if(!this.alias.equals(range.alias)) return false;

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

        return this.field.equals(range.field);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.alias.hashCode();
            result = 31 * result + this.field.hashCode();
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
