package com.kii.thingif.gateway;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.kii.thingif.KiiApp;
import com.kii.thingif.MediaTypes;
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
            @NonNull KiiApp app) {
        super(context, app);
    }
    protected GatewayAPI4EndNode(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<GatewayAPI4EndNode> CREATOR
            = new Parcelable.Creator<GatewayAPI4EndNode>() {
        public GatewayAPI4EndNode createFromParcel(Parcel in) {
            return new GatewayAPI4EndNode(in);
        }

        public GatewayAPI4EndNode[] newArray(int size) {
            return new GatewayAPI4EndNode[size];
        }
    };

    @NonNull
    @WorkerThread
    @Override
    public String onboardGateway() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/onboarding", this.siteName, this.appID);
        String url = Path.combine(baseUrl, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return responseBody.optString("thingID", null);
    }

    @Override
    public String getGatewayID() throws ThingIFException {
        throw new UnsupportedOperationException("This operation is not supported by this instance. Please use GatewayAPIBuilder#build4Gateway()");
    }

    @NonNull
    @WorkerThread
    @Override
    public List<PendingEndNode> listPendingEndNodes() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/end-nodes/pending", this.siteName, this.appID);
        String url = Path.combine(baseUrl, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        List<PendingEndNode> nodes = new ArrayList<PendingEndNode>();
        JSONArray results = responseBody.optJSONArray("results");
        if (results != null) {
            for (int i = 0; i < results.length(); i++) {
                try {
                    nodes.add(new PendingEndNode(results.getJSONObject(i)));
                } catch (JSONException ignore) {
                }
            }
        }
        return nodes;
    }

    @WorkerThread
    @Override
    public void notifyOnboardingCompletion(String endNodeThingID, String endNodeVenderThingID) throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        if (TextUtils.isEmpty(endNodeThingID)) {
            throw new IllegalArgumentException("thingID is null or empty");
        }
        if (TextUtils.isEmpty(endNodeVenderThingID)) {
            throw new IllegalArgumentException("venderThingID is null or empty");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/end-nodes/VENDOR_THING_ID:{2}", this.siteName, this.appID, endNodeVenderThingID);
        String url = Path.combine(baseUrl, path);
        Map<String, String> headers = this.newHeader();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("thingID", endNodeThingID);
        } catch (JSONException e) {
            // Won’t happen
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PUT, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);
        this.restClient.sendRequest(request);
    }

    @WorkerThread
    @Override
    public void restore() throws ThingIFException {
        throw new UnsupportedOperationException("This operation is not supported by this instance. Please use GatewayAPIBuilder#build4Gateway()");
    }

    @WorkerThread
    @Override
    public void replaceEndNode(String endNodeThingID, String endNodeVenderThingID) throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        if (TextUtils.isEmpty(endNodeThingID)) {
            throw new IllegalArgumentException("thingID is null or empty");
        }
        if (TextUtils.isEmpty(endNodeVenderThingID)) {
            throw new IllegalArgumentException("venderThingID is null or empty");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/end-nodes/THING_ID:{2}", this.siteName, this.appID, endNodeThingID);
        String url = Path.combine(baseUrl, path);
        Map<String, String> headers = this.newHeader();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("vendorThingID", endNodeVenderThingID);
        } catch (JSONException e) {
            // Won’t happen
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PUT, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);
        this.restClient.sendRequest(request);
    }

}
