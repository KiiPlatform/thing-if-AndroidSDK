package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents ActionResult.
 */
public final class ActionResult implements Parcelable {
    @Nullable private String errorMessage;
    private boolean succeeded;
    @NonNull private String actionName;
    @Nullable private JSONObject data;

    private volatile int hashCode; // cached hashcode for performance

    ActionResult(
            @NonNull String actionName,
            boolean succeeded,
            @Nullable  String errorMessage,
            @Nullable JSONObject data) {
        this.actionName = actionName;
        this.succeeded = succeeded;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public boolean isSucceeded() {
        return this.succeeded;
    }

    @Nullable
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @NonNull
    public String getActionName() {
        return this.actionName;
    }

    @Nullable
    public JSONObject getData() {
        return this.data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Initialize ActionResult from Parcel.
     * @param in Parcel instance.
     * @throws IllegalArgumentException Thrown when actionName is null or empty.
     */
    public ActionResult(Parcel in) {
        this.actionName = in.readString();
        if (TextUtils.isEmpty(actionName)) {
            throw new IllegalArgumentException("actionName is null or empty");
        }
        this.errorMessage = in.readString();
        this.succeeded = in.readByte() != 0;
        if (in.readString() != null) {
            try {
                this.data = new JSONObject(in.readString());
            } catch (JSONException ex) {
                // never happen
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actionName);
        dest.writeString(this.errorMessage);
        dest.writeByte((byte)(this.succeeded? 1: 0));
        dest.writeString(this.data == null? null: this.data.toString());
    }

    public static final Creator<ActionResult> CREATOR = new Creator<ActionResult>() {
        @Override
        public ActionResult createFromParcel(Parcel source) {
            return new ActionResult(source);
        }

        @Override
        public ActionResult[] newArray(int size) {
            return new ActionResult[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof ActionResult)) return false;
        ActionResult result = (ActionResult)o;

        return this.actionName.equals(result.getActionName()) &&
                (this.errorMessage == null?
                        result.getErrorMessage() == null:
                        this.errorMessage.equals(result.getErrorMessage())) &&
                this.succeeded == result.isSucceeded() &&
                (this.data == null?
                        result.data == null :
                        this.data.toString().equals(result.data.toString()));
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.actionName.hashCode();
            result = 31 * result +
                    (this.errorMessage == null? 0: this.errorMessage.hashCode());
            result = 31 * result +
                    (this.succeeded? 1: 0);
            result = 31 * result +
                    (this.data == null? 0: this.data.hashCode());
            this.hashCode = result;
        }
        return result;
    }
}
