package com.kii.thingif.gateway;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.kii.thingif.KiiApp;
import com.kii.thingif.Site;
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
    protected final String siteName;
    protected final String baseUrl;
    protected String accessToken;
    protected final IoTRestClient restClient;

    GatewayAPI(@Nullable Context context,
               @NonNull KiiApp app) {
        if (context != null) {
            GatewayAPI.context = context.getApplicationContext();
        }
        this.appID = app.getAppID();
        this.appKey = app.getAppKey();
        this.siteName = app.getSiteName();
        this.baseUrl = app.getBaseUrl();
        this.restClient = new IoTRestClient();
    }

    protected Map<String, String> newHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + this.accessToken);
        return headers;
    }

    /** Login to the Gateway.
     * Local authentication for the Gateway access.
     * Required prior to call other APIs access to the gateway.
     * @param username Username of the Gateway.
     * @param password Password of the Gateway.
     * @throws ThingIFException
     */
    @WorkerThread
    public abstract void login(String username, String password) throws ThingIFException;

    /** Let the Gateway Onboard.
     * @return Thing ID assigned by Kii Cloud.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    public abstract String onboardGateway() throws ThingIFException;

    /**
     * Get Gateway ID
     * @return Thing ID assigned by Kii Cloud.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    public abstract String getGatewayID() throws ThingIFException;

    /** List connected end nodes which has not been onboarded.
     * @return List of end nodes connected to the gateway but waiting for onboarding.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    public abstract List<JSONObject> listPendingEndNodes() throws ThingIFException;

    /** Notify Onboarding completion
     * Call this api when the End Node onboarding is done.
     * After the call succeeded, End Node will be fully connected to Kii Cloud through the Gateway.
     * @param endNodeThingID ID of the end-node assigned by Kii Cloud.
     * @param endNodeVenderThingID ID of the end-node assigned by End Node vendor.
     * @throws ThingIFException
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    public abstract void notifyOnboardingCompletion(String endNodeThingID, String endNodeVenderThingID) throws ThingIFException;

    /** Restore the Gateway
     * @throws ThingIFException
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    public abstract void restore() throws ThingIFException;

    /** Check If user is logged in to the Gateway.
     * @return true if user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(this.accessToken);
    }

    // Implementation of Parcelable
    protected GatewayAPI(Parcel in) {
        this.appID = in.readString();
        this.appKey = in.readString();
        this.siteName = in.readString();
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
        dest.writeString(this.siteName);
        dest.writeString(this.baseUrl);
        dest.writeString(this.accessToken);
    }
}
