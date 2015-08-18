package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class LessThan extends Statement {
    private String field;
    private long limit;
    public LessThan(@NonNull String field, long limit) {
        this.field = field;
        this.limit = limit;
    }
    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("type", "range");
            ret.put("field", this.field);
            ret.put("lowerLimit", this.limit);
            ret.put("lowerLimitIncluded", false);
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
        LessThan lessThan = (LessThan) o;
        if (limit != lessThan.limit) return false;
        return field.equals(lessThan.field);
    }
    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + (int) (limit ^ (limit >>> 32));
        return result;
    }

    // Implementation of Parcelable
    protected LessThan(Parcel in) {
        this.field = in.readString();
        this.limit = in.readLong();
    }
    public static final Creator<LessThan> CREATOR = new Creator<LessThan>() {
        @Override
        public LessThan createFromParcel(Parcel in) {
            return new LessThan(in);
        }

        @Override
        public LessThan[] newArray(int size) {
            return new LessThan[size];
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
