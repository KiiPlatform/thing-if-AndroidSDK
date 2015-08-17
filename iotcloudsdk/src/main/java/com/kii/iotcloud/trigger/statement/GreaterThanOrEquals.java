package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class GreaterThanOrEquals extends Statement {
    private String field;
    private long limit;
    public GreaterThanOrEquals(String field, long limit) {
        this.field = field;
        this.limit = limit;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("type", "range");
            ret.put("field", this.field);
            ret.put("upperLimit", this.limit);
            ret.put("upperLimitIncluded", true);
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
        GreaterThanOrEquals that = (GreaterThanOrEquals) o;
        if (limit != that.limit) return false;
        return field.equals(that.field);
    }
    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + (int) (limit ^ (limit >>> 32));
        return result;
    }

    // Implementation of Parcelable
    protected GreaterThanOrEquals(Parcel in) {
        this.field = in.readString();
        this.limit = in.readLong();
    }
    public static final Creator<GreaterThanOrEquals> CREATOR = new Creator<GreaterThanOrEquals>() {
        @Override
        public GreaterThanOrEquals createFromParcel(Parcel in) {
            return new GreaterThanOrEquals(in);
        }

        @Override
        public GreaterThanOrEquals[] newArray(int size) {
            return new GreaterThanOrEquals[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.field);
        dest.writeLong(this.limit);
    }
}
