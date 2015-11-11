package com.kii.thingif;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.internal.http.IoTRestRequest;
import com.kii.thingif.internal.utils.Path;

import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatewayAPI4Gateway extends GatewayAPI {

    GatewayAPI4Gateway(
            @Nullable Context context,
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull Site site,
            @NonNull String baseUrl,
            @NonNull String accessToken) {
        super(context, appID, appKey, site, baseUrl, accessToken);
    }
    protected GatewayAPI4Gateway(Parcel in) {
        super(in);
    }

    /**
     * Onboard the Gateway for the Gateway App
     * @return Thing ID
     * @throws ThingIFException Thrown when gateway returns error response.
     */
    @NonNull
    @WorkerThread
    public String onboardGateway() throws ThingIFException {
        String path = "/gateway-app/gateway/onboarding";
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return responseBody.optString("thingID", null);
    }
    /**
     * Get Gateway ID
     * @return Thing ID
     * @throws ThingIFException Thrown when gateway returns error response.
     */
    @NonNull
    @WorkerThread
    public String getGatewayID() throws ThingIFException {
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/id", this.site.name(), this.appID);
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return responseBody.optString("thingID", null);
    }
    @Override
    public List<JSONObject> listNoOnboardedEndNodes() throws ThingIFException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyOnboardingCompletion(String thingID, String venderThingID) throws ThingIFException {
        throw new UnsupportedOperationException();
    }
    @WorkerThread
    public void restore() throws ThingIFException {
        String path = "/gateway-app/gateway/restore";
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers);
        this.restClient.sendRequest(request);
    }
}
