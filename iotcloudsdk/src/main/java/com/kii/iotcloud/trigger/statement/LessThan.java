package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

public class LessThan implements Statement {
    private String field;
    private long limit;
    public LessThan(String field, long limit) {
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
