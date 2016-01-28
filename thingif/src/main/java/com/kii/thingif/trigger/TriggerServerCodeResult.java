package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class TriggerServerCodeResult implements Parcelable {

    private static final String NULL_VALUE = "null";
    private final boolean succeeded;
    private final String returnedValue;
    private final long executedAt;
    private final String errorMessage;

    public TriggerServerCodeResult(boolean succeeded, String returnedValue, long executedAt, String errorMessage) {
        this.succeeded = succeeded;
        this.returnedValue = returnedValue;
        this.executedAt = executedAt;
        this.errorMessage = errorMessage;
    }
    public boolean isSucceeded() {
        return this.succeeded;
    }
    public String getReturnedValue() {
        return this.returnedValue;
    }
    public JSONObject getReturnedValueAsJsonObject() {
        if (this.returnedValue == null || NULL_VALUE.equals(this.returnedValue)) {
            return null;
        }
        try {
            return new JSONObject(this.returnedValue);
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to org.json.JSONObject");
        }
    }
    public JSONArray getReturnedValueAsJsonArray() {
        if (this.returnedValue == null || NULL_VALUE.equals(this.returnedValue)) {
            return null;
        }
        try {
            return new JSONArray(this.returnedValue);
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to org.json.JSONArray");
        }
    }
    public Integer getReturnedValueAsInteger() {
        if (this.returnedValue == null || NULL_VALUE.equals(this.returnedValue)) {
            return null;
        }
        try {
            return Integer.parseInt(this.returnedValue);
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to Integer");
        }
    }
    public Long getReturnedValueAsLong() {
        if (this.returnedValue == null || NULL_VALUE.equals(this.returnedValue)) {
            return null;
        }
        try {
            return Long.parseLong(this.returnedValue);
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to Long");
        }
    }
    public Boolean getReturnedValueAsBoolean() {
        if (this.returnedValue == null || NULL_VALUE.equals(this.returnedValue)) {
            return null;
        }
        try {
            return Boolean.parseBoolean(this.returnedValue);
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to Boolean");
        }

    }
    public Double getReturnedValueAsDouble() {
        if (this.returnedValue == null || NULL_VALUE.equals(this.returnedValue)) {
            return null;
        }
        try {
            return Double.parseDouble(this.returnedValue);
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to Double");
        }
    }
    public long getExecutedAt() {
        return this.executedAt;
    }
    public String getErrorMessage() {
        return this.errorMessage;
    }

    // Implementation of Parcelable
    protected TriggerServerCodeResult(Parcel in) {
        this.succeeded = (in.readByte() != 0);
        this.returnedValue = in.readString();
        this.executedAt = in.readLong();
        this.errorMessage = in.readString();
    }
    public static final Creator<TriggerServerCodeResult> CREATOR = new Creator<TriggerServerCodeResult>() {
        @Override
        public TriggerServerCodeResult createFromParcel(Parcel in) {
            return new TriggerServerCodeResult(in);
        }

        @Override
        public TriggerServerCodeResult[] newArray(int size) {
            return new TriggerServerCodeResult[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (this.succeeded ? 1 : 0));
        dest.writeString(this.returnedValue);
        dest.writeLong(this.executedAt);
        dest.writeString(this.errorMessage);
    }
}
