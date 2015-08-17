package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

public class LessThanOrEquals implements Statement {
    private String field;
    private long limit;
    public LessThanOrEquals(String field, long limit) {
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
            ret.put("lowerLimitIncluded", true);
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }

    protected LessThanOrEquals(Parcel in) {
        this.field = in.readString();
        this.limit = in.readLong();
    }
    public static final Creator<LessThanOrEquals> CREATOR = new Creator<LessThanOrEquals>() {
        @Override
        public LessThanOrEquals createFromParcel(Parcel in) {
            return new LessThanOrEquals(in);
        }

        @Override
        public LessThanOrEquals[] newArray(int size) {
            return new LessThanOrEquals[size];
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
