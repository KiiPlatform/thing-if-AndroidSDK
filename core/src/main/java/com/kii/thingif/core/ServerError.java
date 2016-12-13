package com.kii.thingif.core;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class ServerError implements Parcelable {
    private final String errorMessage;
    private final String errorCode;
    private final String detailMessage;

    public ServerError(String errorMessage, String errorCode, String detailMessage) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }
    public ServerError(JSONObject json) {
        this.errorMessage = json.optString("errorMessage");
        JSONObject details = json.optJSONObject("details");
        if (details != null) {
            this.errorCode = details.optString("errorCode");
            this.detailMessage = details.optString("message");
        } else {
            this.errorCode = null;
            this.detailMessage = null;
        }
    }

    /**
     * Gets the error message
     * @return error message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    /**
     * Gets the error code
     * @return error code.
     */
    public String getErrorCode() {
        return errorCode;
    }
    /**
     * Gets the detail message
     * @return detail message.
     */
    public String getDetailMessage() {
        return detailMessage;
    }

    // Implementation of Parcelable
    protected ServerError(Parcel in) {
        errorMessage = in.readString();
        errorCode = in.readString();
        detailMessage = in.readString();
    }
    public static final Creator<ServerError> CREATOR = new Creator<ServerError>() {
        @Override
        public ServerError createFromParcel(Parcel in) {
            return new ServerError(in);
        }
        @Override
        public ServerError[] newArray(int size) {
            return new ServerError[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errorMessage);
        dest.writeString(errorCode);
        dest.writeString(detailMessage);
    }
}
