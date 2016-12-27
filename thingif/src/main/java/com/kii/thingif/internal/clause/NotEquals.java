package com.kii.thingif.internal.clause;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class NotEquals implements Clause {
    private final Equals equals;
    public NotEquals(@NonNull Equals equals) {
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
    public Equals getEquals() {
        return this.equals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotEquals notEquals = (NotEquals) o;
        return equals.equals(notEquals.equals);
    }
    @Override
    public int hashCode() {
        return equals.hashCode();
    }

    // Implementation of Parcelable
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
