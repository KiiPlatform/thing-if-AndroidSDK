package com.kii.thing_if.gateway;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thing_if.MediaTypes;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.KiiApp;
import com.kii.thing_if.Owner;
import com.kii.thing_if.command.ActionResult;
import com.kii.thing_if.command.ActionResultAdapter;
import com.kii.thing_if.exception.ThingIFException;
import com.kii.thing_if.internal.http.IoTRestClient;
import com.kii.thing_if.internal.http.IoTRestRequest;
import com.kii.thing_if.internal.utils.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * GatewayCommandAPI represents APIs, sending command to gateway through kii cloud.
 * It requires the gateway has already been onboarded by the owner.
 * <p>
 * To onboard gateway, please refer to {@link ThingIFAPI#onboardWithVendorThingID(String, String)} or
 * {@link ThingIFAPI#onboardWithThingID(String, String)}.
 * <p>
 * Since gateway can't parse trait format command yet. So if the gateway onboard with trait
 * configured thingType and firmwareVersion, should not use GatewayCommandAPI.
 */
public class GatewayCommandAPI {
    private @NonNull  KiiApp app;
    private @NonNull Owner owner;
    private @NonNull String gatewayID;

    private final IoTRestClient restClient;

    private Map<String, String> newHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        if (!TextUtils.isEmpty(this.app.getAppID())) {
            headers.put("X-Kii-AppID", this.app.getAppID());
        }
        if (!TextUtils.isEmpty(this.app.getAppKey())) {
            headers.put("X-Kii-AppKey", this.app.getAppKey());
        }
        if (this.owner != null && !TextUtils.isEmpty(this.owner.getAccessToken())) {
            headers.put("Authorization", "Bearer " + this.owner.getAccessToken());
        }
        return headers;
    }

    private JSONObject getCommand(String commandID) throws ThingIFException {
        String path = MessageFormat.format(
                "/thing-if/apps/{0}/targets/THING:{1}/commands/{2}",
                this.app.getAppID(),
                this.gatewayID,
                commandID);
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(
                url,
                IoTRestRequest.Method.GET,
                headers);
        return this.restClient.sendRequest(request);
    }

    private String postNewCommand(JSONObject requestBody) throws ThingIFException {
        String path = MessageFormat.format(
                "/thing-if/apps/{0}/targets/THING:{1}/commands",
                this.app.getAppID(),
                this.gatewayID);
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(
                url,
                IoTRestRequest.Method.POST,
                headers,
                MediaTypes.MEDIA_TYPE_JSON,
                requestBody);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return responseBody.optString("commandID", null);
    }

    /**
     * Initialize GatewayCommandAPI instance.
     * @param app app instance.
     * @param owner owner of gateway.
     * @param gatewayID thingID of gateway.
     */
    public GatewayCommandAPI(
            @NonNull KiiApp app,
            @NonNull Owner owner,
            @NonNull String gatewayID) {
        this.app = app;
        this.owner = owner;
        this.gatewayID = gatewayID;
        this.restClient = new IoTRestClient();
    }

    /**
     * Send command to gateway to get list of pending nodes in gateway.
     * @return command id
     * @throws ThingIFException
     */
    public String postListPendingEndNodesCommand() throws ThingIFException {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("issuer", this.owner.getTypedID().toString());
            requestBody.put("actions", new JSONArray()
                    .put(new JSONObject()
                            .put("listPendings", new JSONObject()
                                    .put("serverLocation", this.app.getSiteName())
                                    .put("appID", this.app.getAppID())
                            )
                    )
            );
        }catch (JSONException ex) {
            // wont happend
        }
        return postNewCommand(requestBody);
    }

    /**
     * Send command to gateway to notify it with completion of onboarding for end node.
     * @param endNode EndNode instance.
     * @return command id
     * @throws ThingIFException
     */
    public String postOnboardCompletionCommand(@NonNull EndNode endNode) throws ThingIFException {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("issuer", this.owner.getTypedID().toString());
            requestBody.put("actions", new JSONArray()
                    .put(new JSONObject()
                            .put("onboardEndnode", new JSONObject()
                                    .put("serverLocation", this.app.getSiteName())
                                    .put("appID", this.app.getAppID())
                                    .put("vendorThingID", endNode.getVendorThingID())
                                    .put("thingID", endNode.getThingID())
                            )
                    )
            );
        }catch (JSONException ex) {
            // wont happend
        }
        return postNewCommand(requestBody);
    }

    @NonNull
    public KiiApp getApp() {
        return app;
    }

    @NonNull
    public Owner getOwner() {
        return owner;
    }

    @NonNull
    public String getGatewayID() {
        return gatewayID;
    }

    /**
     * Get action results of list pending endnode command.
     * @param commandID command id.
     * @return ActionResult instance.
     * @throws ThingIFException
     */
    public ActionResult getListPendingEndNodesResult(String commandID) throws ThingIFException {
        JSONObject responseBody = this.getCommand(commandID);
        JSONObject actionResults = responseBody.optJSONObject("actionResults");
        if(actionResults == null) {
            return null;
        }
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ActionResult.class, new ActionResultAdapter())
                .create();
        ActionResult actionResult = gson.fromJson(actionResults.toString(), ActionResult.class);
        if(!actionResult.getActionName().equals("listPendings")){
            return null;
        }
        return actionResult;
    }

    /**
     * Get action results of onboard completion command.
     * @param commandID command id.
     * @return ActionResult instance.
     * @throws ThingIFException
     */
    public ActionResult getOnboardCompletionResult(String commandID) throws ThingIFException {
        JSONObject responseBody = this.getCommand(commandID);
        JSONObject actionResults = responseBody.optJSONObject("actionResults");
        if(actionResults == null) {
            return null;
        }
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ActionResult.class, new ActionResultAdapter())
                .create();
        ActionResult actionResult = gson.fromJson(actionResults.toString(), ActionResult.class);
        if(!actionResult.getActionName().equals("onboardEndnode")){
            return null;
        }
        return actionResult;
    }
}
