package com.kii.thingif.trigger.clause;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class TimeRange extends Clause{

    private Date lowerLimit;
    private Date upperLimit;

    public TimeRange(Date lowerLimit, Date upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }
    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("type", "withinTimeRange");
            ret.put("lowerLimit", this.lowerLimit.getTime());
            ret.put("upperLimit", this.upperLimit.getTime());
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.lowerLimit.getTime());
        dest.writeLong(this.upperLimit.getTime());
    }

    public TimeRange(Parcel in) {
        this.lowerLimit = new Date(in.readLong());
        this.upperLimit = new Date(in.readLong());
    }

    public static final Creator<TimeRange> CREATOR = new Creator<TimeRange>() {
        @Override
        public TimeRange createFromParcel(Parcel source) {
            return new TimeRange(source);
        }

        @Override
        public TimeRange[] newArray(int size) {
            return new TimeRange[size];
        }
    };
}
