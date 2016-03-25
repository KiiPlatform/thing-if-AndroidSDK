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

public class GatewayAPI4GatewayImpl extends AbstractGatewayAPI implements GatewayAPI4Gateway {

    GatewayAPI4GatewayImpl(
            @Nullable Context context,
            @NonNull KiiApp app) {
        super(context, app);
    }
    protected GatewayAPI4GatewayImpl(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<GatewayAPI4GatewayImpl> CREATOR
            = new Parcelable.Creator<GatewayAPI4GatewayImpl>() {
        public GatewayAPI4GatewayImpl createFromParcel(Parcel in) {
            return new GatewayAPI4GatewayImpl(in);
        }

        public GatewayAPI4GatewayImpl[] newArray(int size) {
            return new GatewayAPI4GatewayImpl[size];
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
