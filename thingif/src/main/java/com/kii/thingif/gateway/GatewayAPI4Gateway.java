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

public class GatewayAPI4Gateway extends GatewayAPI {

    GatewayAPI4Gateway(
            @Nullable Context context,
            @NonNull KiiApp app) {
        super(context, app);
    }
    protected GatewayAPI4Gateway(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<GatewayAPI4Gateway> CREATOR
            = new Parcelable.Creator<GatewayAPI4Gateway>() {
        public GatewayAPI4Gateway createFromParcel(Parcel in) {
            return new GatewayAPI4Gateway(in);
        }

        public GatewayAPI4Gateway[] newArray(int size) {
            return new GatewayAPI4Gateway[size];
        }
    };

    @Override
    public void login(String username, String password) throws ThingIFException {
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

    @NonNull
    @WorkerThread
    @Override
    public String onboardGateway() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = "/gateway-app/gateway/onboarding";
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return responseBody.optString("thingID", null);
    }

    @NonNull
    @WorkerThread
    @Override
    public String getGatewayID() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/id", this.siteName, this.appID);
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return responseBody.optString("thingID", null);
    }
    @Override
    public List<JSONObject> listPendingEndNodes() throws ThingIFException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyOnboardingCompletion(String thingID, String venderThingID) throws ThingIFException {
        throw new UnsupportedOperationException();
    }

    @WorkerThread
    @Override
    public void restore() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = "/gateway-app/gateway/restore";
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers);
        this.restClient.sendRequest(request);
    }
}
