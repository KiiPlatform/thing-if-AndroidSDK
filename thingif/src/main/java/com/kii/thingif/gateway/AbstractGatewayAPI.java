package com.kii.thingif.gateway;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Base64;

import com.kii.thingif.KiiApp;
import com.kii.thingif.MediaTypes;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.internal.http.IoTRestClient;
import com.kii.thingif.internal.http.IoTRestRequest;
import com.kii.thingif.internal.utils.Path;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractGatewayAPI implements Parcelable, GatewayAPI {

    protected static Context context;
    protected final String appID;
    protected final String appKey;
    protected final String siteName;
    protected final String baseUrl;
    protected String accessToken;
    protected final IoTRestClient restClient;

    AbstractGatewayAPI(@Nullable Context context,
                       @NonNull KiiApp app) {
        if (context != null) {
            AbstractGatewayAPI.context = context.getApplicationContext();
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
    @Override
    @WorkerThread
    public void login(@NonNull String username, @NonNull String password) throws ThingIFException {
        if (TextUtils.isEmpty(username)) {
            throw new IllegalArgumentException("username is null or empty");
        }
        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("password is null or empty");
        }
        String path = MessageFormat.format("/{0}/token", this.siteName);
        String url = Path.combine(baseUrl, path);

        String credential = this.appID + ":" + this.appKey;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + Base64.encodeToString(credential.getBytes(), Base64.NO_WRAP));

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
        } catch (JSONException e) {
            // Won’t happen
        }

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);
        JSONObject responseBody = new IoTRestClient().sendRequest(request);
        this.accessToken = responseBody.optString("accessToken", null);
    }
    /**
     * Get vendorThingID of the Gateway.
     * When the end user replaces the Gateway, Gateway App/End Node App need to obtain the new Gateway’s vendorThingID.
     *
     * @return vendorThingID of the Gateway.
     * @throws ThingIFException
     * @throws IllegalStateException Thrown when user is not logged in.
     */
    @Override
    @WorkerThread
    @NonNull
    public String getGatewayInformation() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = "/gateway-info";
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return responseBody.optString("vendorThingID", null);
    }

    /** Check If user is logged in to the Gateway.
     * @return true if user is logged in, false otherwise.
     */
    @Override
    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(this.accessToken);
    }

//    /** Let the Gateway Onboard.
//     * @return Thing ID assigned by Kii Cloud.
//     * @throws ThingIFException Thrown when gateway returns error response.
//     * @throws IllegalStateException Thrown when user is not logged in.
//     * See {@link #login(String, String)}
//     */
//    @WorkerThread
//    @NonNull
//    public abstract String onboardGateway() throws ThingIFException;
//
//    /**
//     * Get Gateway ID
//     * @return Thing ID assigned by Kii Cloud.
//     * @throws ThingIFException Thrown when gateway returns error response.
//     * @throws IllegalStateException Thrown when user is not logged in.
//     * See {@link #login(String, String)}
//     */
//    @WorkerThread
//    @NonNull
//    public abstract String getGatewayID() throws ThingIFException;
//
//    /** List connected end nodes which has not been onboarded.
//     * @return List of end nodes connected to the gateway but waiting for onboarding.
//     * @throws ThingIFException Thrown when gateway returns error response.
//     * @throws IllegalStateException Thrown when user is not logged in.
//     * See {@link #login(String, String)}
//     */
//    @WorkerThread
//    @NonNull
//    public abstract List<PendingEndNode> listPendingEndNodes() throws ThingIFException;
//
//    /** Notify Onboarding completion
//     * Call this api when the End Node onboarding is done.
//     * After the call succeeded, End Node will be fully connected to Kii Cloud through the Gateway.
//     * @param endNodeThingID ID of the end-node assigned by Kii Cloud.
//     * @param endNodeVenderThingID ID of the end-node assigned by End Node vendor.
//     * @throws ThingIFException
//     * @throws IllegalStateException Thrown when user is not logged in.
//     * See {@link #login(String, String)}
//     */
//    @WorkerThread
//    public abstract void notifyOnboardingCompletion(@NonNull String endNodeThingID, @NonNull String endNodeVenderThingID) throws ThingIFException;
//
//    /** Restore the Gateway
//     * @throws ThingIFException
//     * @throws IllegalStateException Thrown when user is not logged in.
//     * See {@link #login(String, String)}
//     */
//    @WorkerThread
//    public abstract void restore() throws ThingIFException;
//
//    /**
//     * Replace end-node by new vendorThingID for end node thingID.
//     *
//     * @param endNodeThingID ID of the end-node assigned by Kii Cloud.
//     * @param endNodeVenderThingID ID of the end-node assigned by End Node vendor.
//     * @throws ThingIFException
//     * @throws IllegalStateException Thrown when user is not logged in.
//     * See {@link #login(String, String)}
//     */
//    @WorkerThread
//    public abstract void replaceEndNode(@NonNull String endNodeThingID, @NonNull String endNodeVenderThingID) throws ThingIFException;

    // Implementation of Parcelable
    protected AbstractGatewayAPI(Parcel in) {
        this.appID = in.readString();
        this.appKey = in.readString();
        this.siteName = in.readString();
        this.baseUrl = in.readString();
        this.accessToken = in.readString();
        this.restClient = new IoTRestClient();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appID);
        dest.writeString(this.appKey);
        dest.writeString(this.siteName);
        dest.writeString(this.baseUrl);
        dest.writeString(this.accessToken);
    }
}
