package com.kii.iotcloud.trigger.clause;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Range extends Clause {

    private final String field;
    private final Long upperLimit;
    private final Long lowerLimit;
    private final Boolean upperIncluded;
    private final Boolean lowerIncluded;

    public Range(String field, Long upperLimit, Boolean upperIncluded, Long lowerLimit, Boolean lowerIncluded) {
        this.field = field;
        this.upperLimit = upperLimit;
        this.upperIncluded = upperIncluded;
        this.lowerLimit = lowerLimit;
        this.lowerIncluded = lowerIncluded;
    }
    public static Range range(
            @NonNull String field,
            Number upperLimit,
            Boolean upperIncluded,
            Number lowerLimit,
            Boolean lowerIncluded) {
        return new Range(field, upperLimit.longValue(), upperIncluded, lowerLimit.longValue(), lowerIncluded);
    }
    public static Range greaterThan(
            @NonNull String field,
            @NonNull Number lowerLimit) {
        return new Range(field, null, null, lowerLimit.longValue(), Boolean.FALSE);
    }
    public static Range greaterThanEquals(
            @NonNull String field,
            @NonNull Number lowerLimit) {
        return new Range(field, null, null, lowerLimit.longValue(), Boolean.TRUE);
    }
    public static Range lessThan(
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new Range(field, upperLimit.longValue(), Boolean.FALSE, null, null);
    }
    public static Range lessThanEquals(
            @NonNull String field,
            @NonNull Number upperLimit) {
        return new Range(field, upperLimit.longValue(), Boolean.TRUE, null, null);
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

    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("type", "range");
            ret.put("field", this.field);
            if (this.upperLimit != null) {
                ret.put("upperLimit", this.upperLimit);
            }
            if (this.upperIncluded != null) {
                ret.put("upperIncluded", this.upperIncluded);
            }
            if (this.lowerLimit != null) {
                ret.put("lowerLimit", this.lowerLimit);
            }
            if (this.lowerIncluded != null) {
                ret.put("lowerIncluded", this.lowerIncluded);
            }
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        if (!field.equals(range.field)) return false;
        if (upperLimit != null ? !upperLimit.equals(range.upperLimit) : range.upperLimit != null)
            return false;
        if (lowerLimit != null ? !lowerLimit.equals(range.lowerLimit) : range.lowerLimit != null)
            return false;
        if (upperIncluded != null ? !upperIncluded.equals(range.upperIncluded) : range.upperIncluded != null)
            return false;
        return !(lowerIncluded != null ? !lowerIncluded.equals(range.lowerIncluded) : range.lowerIncluded != null);
    }
    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + (upperLimit != null ? upperLimit.hashCode() : 0);
        result = 31 * result + (lowerLimit != null ? lowerLimit.hashCode() : 0);
        result = 31 * result + (upperIncluded != null ? upperIncluded.hashCode() : 0);
        result = 31 * result + (lowerIncluded != null ? lowerIncluded.hashCode() : 0);
        return result;
    }

    // Implementation of Parcelable
    protected Range(Parcel in) {
        this.field = in.readString();
        this.upperLimit = (Long)in.readValue(Range.class.getClassLoader());
        this.upperIncluded = (Boolean)in.readValue(Range.class.getClassLoader());
        this.lowerLimit = (Long)in.readValue(Range.class.getClassLoader());
        this.lowerIncluded = (Boolean)in.readValue(Range.class.getClassLoader());
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
        dest.writeString(this.field);
        dest.writeValue(this.upperLimit);
        dest.writeValue(this.upperIncluded);
        dest.writeValue(this.lowerLimit);
        dest.writeValue(this.lowerIncluded);
    }
}
