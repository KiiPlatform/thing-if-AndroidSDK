package com.kii.thingif.gateway;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.kii.thingif.KiiApp;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.internal.http.IoTRestRequest;
import com.kii.thingif.internal.utils.Path;

import org.json.JSONObject;

import java.text.MessageFormat;
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
    public List<PendingEndNode> listPendingEndNodes() throws ThingIFException {
        throw new UnsupportedOperationException("This operation is not supported by this instance. Please use GatewayAPIBuilder#build4EndNode()");
    }

    @Override
    public void notifyOnboardingCompletion(String thingID, String venderThingID) throws ThingIFException {
        throw new UnsupportedOperationException("This operation is not supported by this instance. Please use GatewayAPIBuilder#build4EndNode()");
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

    @WorkerThread
    @Override
    public void replaceEndNode(String endNodeThingID, String endNodeVenderThingID) throws ThingIFException {
        throw new UnsupportedOperationException("This operation is not supported by this instance. Please use GatewayAPIBuilder#build4EndNode()");
    }
}
