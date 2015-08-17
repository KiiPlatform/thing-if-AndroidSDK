package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

public class NotEquals implements Statement {
    private final Equals equals;
    public NotEquals(Equals equals) {
        this.equals = equals;
    }
    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("type", "not");
            ret.put("clause", this.equals.toJSONObject());
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }

    protected NotEquals(Parcel in) {
        this.equals = in.readParcelable(Equals.class.getClassLoader());
    }
    public static final Creator<NotEquals> CREATOR = new Creator<NotEquals>() {
        @Override
        public NotEquals createFromParcel(Parcel in) {
            return new NotEquals(in);
        }

        @Override
        public NotEquals[] newArray(int size) {
            return new NotEquals[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.equals, flags);
    }
}
