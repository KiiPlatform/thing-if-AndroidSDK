package com.kii.thingif;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.internal.http.IoTRestClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GatewayAPI implements Parcelable {

    protected static Context context;
    protected final String appID;
    protected final String appKey;
    protected final Site site;
    protected final String baseUrl;
    protected String accessToken;
    protected final IoTRestClient restClient;

    GatewayAPI(@Nullable Context context,
               @NonNull String appID,
               @NonNull String appKey,
               @NonNull Site site,
               @NonNull String baseUrl) {
        if (context != null) {
            GatewayAPI.context = context.getApplicationContext();
        }
        this.appID = appID;
        this.appKey = appKey;
        this.site = site;
        this.baseUrl = baseUrl;
        this.restClient = new IoTRestClient();
    }

    protected Map<String, String> newHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + this.accessToken);
        return headers;
    }

    /**
     *
     * @param username
     * @param password
     * @throws ThingIFException
     */
    @WorkerThread
    public abstract void login(String username, String password) throws ThingIFException;
    /**
     * Onboard the Gateway
     * @return Thing ID
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     */
    @WorkerThread
    public abstract String onboardGateway() throws ThingIFException;
    /**
     * Get Gateway ID
     * @return Thing ID
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     */
    @WorkerThread
    public abstract String getGatewayID() throws ThingIFException;
    /**
     * List connected end nodes which has not been onboarded.
     *
     * @return List of end nodes
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     */
    @WorkerThread
    public abstract List<JSONObject> listNoOnboardedEndNodes() throws ThingIFException;
    /**
     *
     * @param thingID
     * @param venderThingID
     * @throws ThingIFException
     * @throws IllegalStateException Thrown when user is not logged in.
     */
    @WorkerThread
    public abstract void notifyOnboardingCompletion(String thingID, String venderThingID) throws ThingIFException;
    /**
     *
     * @throws ThingIFException
     * @throws IllegalStateException Thrown when user is not logged in.
     */
    @WorkerThread
    public abstract void restore() throws ThingIFException;

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(this.accessToken);
    }

    // Implementation of Parcelable
    protected GatewayAPI(Parcel in) {
        this.appID = in.readString();
        this.appKey = in.readString();
        this.site = (Site)in.readSerializable();
        this.baseUrl = in.readString();
        this.accessToken = in.readString();
        this.restClient = new IoTRestClient();
    }
    public static final Creator<GatewayAPI> CREATOR = new Creator<GatewayAPI>() {
        @Override
        public GatewayAPI createFromParcel(Parcel in) {
            String className = in.readString();
            if (GatewayAPI4Gateway.class.getName().equals(className)) {
                return new GatewayAPI4Gateway(in);
            } else if (GatewayAPI4EndNode.class.getName().equals(className)) {
                return new GatewayAPI4EndNode(in);
            }
            throw new AssertionError("detected unknown class " + className);
        }

        @Override
        public GatewayAPI[] newArray(int size) {
            return new GatewayAPI[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getClass().getName());
        dest.writeString(this.appID);
        dest.writeString(this.appKey);
        dest.writeSerializable(this.site);
        dest.writeString(this.baseUrl);
        dest.writeString(this.accessToken);
    }
}
