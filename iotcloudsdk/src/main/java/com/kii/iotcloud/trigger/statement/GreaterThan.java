package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class GreaterThan extends Statement {
    private String field;
    private long limit;
    public GreaterThan(@NonNull String field, long limit) {
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
            ret.put("upperLimitIncluded", false);
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
        GreaterThan that = (GreaterThan) o;
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
    protected GreaterThan(Parcel in) {
        this.field = in.readString();
        this.limit = in.readLong();
    }
    public static final Creator<GreaterThan> CREATOR = new Creator<GreaterThan>() {
        @Override
        public GreaterThan createFromParcel(Parcel in) {
            return new GreaterThan(in);
        }

        @Override
        public GreaterThan[] newArray(int size) {
            return new GreaterThan[size];
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
