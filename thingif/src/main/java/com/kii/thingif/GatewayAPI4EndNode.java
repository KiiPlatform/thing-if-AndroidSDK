package com.kii.thingif;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.internal.http.IoTRestRequest;
import com.kii.thingif.internal.utils.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GatewayAPI4EndNode extends GatewayAPI {

    GatewayAPI4EndNode(
            @Nullable Context context,
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull Site site,
            @NonNull String baseUrl,
            @NonNull String accessToken) {
        super(context, appID, appKey, site, baseUrl, accessToken);
    }
    protected GatewayAPI4EndNode(Parcel in) {
        super(in);
    }

    /**
     * Onboard the Gateway for the end node app
     * @return Thing ID
     * @throws ThingIFException Thrown when gateway returns error response.
     */
    @NonNull
    @WorkerThread
    public String onboardGateway() throws ThingIFException {
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/onboarding", this.site.name(), this.appID);
        String url = Path.combine(baseUrl, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return responseBody.optString("thingID", null);
    }
    @Override
    public String getGatewayID() throws ThingIFException {
        throw new UnsupportedOperationException();
    }
    /**
     * List connected end nodes which has not been onboarded.
     *
     * @return List of end nodes
     * @throws ThingIFException Thrown when gateway returns error response.
     */
    @NonNull
    @WorkerThread
    public List<JSONObject> listNoOnboardedEndNodes() throws ThingIFException {
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/end-nodes/pending", this.site.name(), this.appID);
        String url = Path.combine(baseUrl, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        List<JSONObject> nodes = new ArrayList<JSONObject>();
        JSONArray results = responseBody.optJSONArray("results");
        if (results != null) {
            for (int i = 0; i < results.length(); i++) {
                nodes.add(results.optJSONObject(i));
            }
        }
        // TODO:consider to define the model for the end node
        return nodes;
    }
    @WorkerThread
    public void notifyOnboardingCompletion(String thingID, String venderThingID) throws ThingIFException {
        if (TextUtils.isEmpty(thingID)) {
            throw new IllegalArgumentException("thingID is null or empty");
        }
        if (TextUtils.isEmpty(venderThingID)) {
            throw new IllegalArgumentException("venderThingID is null or empty");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/end-nodes/VENDOR_THING_ID:{2}", this.site.name(), this.appID, venderThingID);
        String url = Path.combine(baseUrl, path);
        Map<String, String> headers = this.newHeader();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("thingID", thingID);
        } catch (JSONException e) {
            // Won’t happen
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PUT, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);
        this.restClient.sendRequest(request);
    }
    @WorkerThread
    public void restore() throws ThingIFException {
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/restore", this.site.name(), this.appID);
        String url = Path.combine(baseUrl, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers);
        this.restClient.sendRequest(request);
    }
}
