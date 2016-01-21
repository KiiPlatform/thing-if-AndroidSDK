package com.kii.thingif.servercode;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerCode implements Parcelable {

    private final String endpoint;
    private final String executorAccessToken;
    private String targetAppID;
    private JSONObject parameters;

    public ServerCode(
            @NonNull String endpoint,
            @NonNull String executorAccessToken) {
        this(endpoint, executorAccessToken, null, null);
    }
    public ServerCode(
            @NonNull String endpoint,
            @NonNull String executorAccessToken,
            @Nullable String targetAppID,
            @Nullable JSONObject parameters) {
        if (TextUtils.isEmpty(endpoint)) {
            throw new IllegalArgumentException("endpoint is null or empty");
        }
        if (TextUtils.isEmpty(executorAccessToken)) {
            throw new IllegalArgumentException("executorAccessToken is null or empty");
        }
        this.endpoint = endpoint;
        this.executorAccessToken = executorAccessToken;
        this.targetAppID = targetAppID;
        // TODO:Do we need to check nested JSON?
        this.parameters = parameters;
    }

    public String getEndpoint() {
        return this.endpoint;
    }
    public String getExecutorAccessToken() {
        return this.executorAccessToken;
    }
    public String getTargetAppID() {
        return this.targetAppID;
    }
    public JSONObject getParameters() {
        return this.parameters;
    }

    // Implementation of Parcelable
    protected ServerCode(Parcel in) {
        this.endpoint = in.readString();
        this.executorAccessToken = in.readString();
        this.targetAppID = in.readString();
        String parameters = in.readString();
        if (!TextUtils.isEmpty(parameters)) {
            try {
                this.parameters = new JSONObject(parameters);
            } catch (JSONException ignore) {
                // Won’t happen
            }
        }
    }
    public static final Creator<ServerCode> CREATOR = new Creator<ServerCode>() {
        @Override
        public ServerCode createFromParcel(Parcel in) {
            return new ServerCode(in);
        }

        @Override
        public ServerCode[] newArray(int size) {
            return new ServerCode[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.endpoint);
        dest.writeString(this.executorAccessToken);
        dest.writeString(this.targetAppID);
        dest.writeString(this.parameters == null ? null : this.parameters.toString());
    }
}
