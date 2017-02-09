package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Represents ActionResult.
 */
public final class ActionResult implements Parcelable {
    @Nullable private String errorMessage;
    private boolean succeeded;
    @NonNull private String actionName;

    private volatile int hashCode; // cached hashcode for performance

    /**
     * Initialize ActionResult instance
     * @param actionName name of action.
     * @param errorMessage error message
     * @param succeeded If true succeeded, otherwise failed.
     */
    public ActionResult(
            @NonNull String actionName,
            @Nullable String errorMessage,
            boolean succeeded) {
        if (TextUtils.isEmpty(actionName)) {
            throw new IllegalArgumentException("actionName is null or empty");
        }
        this.actionName = actionName;
        this.errorMessage = errorMessage;
        this.succeeded = succeeded;
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

    @Override
    public int describeContents() {
        return 0;
    }

    public ActionResult(Parcel in) {
        this.actionName = in.readString();
        this.errorMessage = in.readString();
        this.succeeded = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actionName);
        dest.writeString(this.errorMessage);
        dest.writeByte((byte)(this.succeeded? 1: 0));
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
                this.succeeded == result.isSucceeded();
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
            this.hashCode = result;
        }
        return result;
    }
}
