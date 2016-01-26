package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class TriggerServerCodeResult implements Parcelable {

    private boolean succeeded;
    private JSONObject returnedValue;
    private long executedAt;
    private String errorMessage;

    TriggerServerCodeResult() {
    }
    public boolean isSucceeded() {
        return this.succeeded;
    }
    public JSONObject getReturnedValue() {
        return this.returnedValue;
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
        String returnedValue = in.readString();
        if (!TextUtils.isEmpty(returnedValue)) {
            try {
                this.returnedValue = new JSONObject(returnedValue);
            } catch (JSONException ignore) {
                // Won’t happen
            }
        }
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
        dest.writeString(this.returnedValue == null ? null : this.returnedValue.toString());
        dest.writeLong(this.executedAt);
        dest.writeString(this.errorMessage);
    }
}
