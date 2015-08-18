package com.kii.iotcloud;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Pair;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.exception.IoTCloudException;
import com.kii.iotcloud.exception.UnsupportedActionException;
import com.kii.iotcloud.exception.UnsupportedSchemaException;
import com.kii.iotcloud.http.IoTRestClient;
import com.kii.iotcloud.http.IoTRestRequest;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.trigger.Predicate;
import com.kii.iotcloud.trigger.SchedulePredicate;
import com.kii.iotcloud.trigger.Trigger;
import com.kii.iotcloud.utils.GsonRepository;
import com.kii.iotcloud.utils.JsonUtils;
import com.kii.iotcloud.utils.Path;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class operates an IoT device that is specified by <@link #onBoard(String, String, String, JSONObject)></@link> method.
 */
public class IoTCloudAPI implements Parcelable, Serializable {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    private static final MediaType MEDIA_TYPE_INSTALLATION_CREATION_REQUEST = MediaType.parse("application/vnd.kii.InstallationCreationRequest+json");

    private final String appID;
    private final String appKey;
    private final Site site;
    private final Owner owner;
    private final Map<Pair<String, Integer>, Schema> schemas = new HashMap<Pair<String, Integer>, Schema>();
    private final IoTRestClient restClient;
    private boolean onBoarded = false;
    private String installationID;

    IoTCloudAPI(
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull Site site,
            @NonNull Owner owner,
            @NonNull List<Schema> schemas) {
        this.appID = appID;
        this.appKey = appKey;
        this.site = site;
        this.owner = owner;
        for (Schema schema : schemas) {
            this.schemas.put(new Pair<String, Integer>(schema.getSchemaName(), schema.getSchemaVersion()), schema);
        }
        this.restClient = new IoTRestClient();
    }

    /** On board IoT Cloud with the specified vendor thing ID.
     * Specified thing will be owned by owner who is specified
     * <@link newWithAccessToken></@link>.
     * IoT Cloud prepares communication channel to the target.
     * @param vendorThingID Thing ID given by vendor. Must be specified.
     * @param thingPassword Thing Password given by vendor. Must be specified.
     * @param thingType Type of the thing given by vendor.
     *                  If the thing is already registered, this value would be
     *                  ignored by IoT Cloud.
     * @param thingProperties Properties of thing.
     *                        If the thing is already registered, this value
     *                        would be ignored by IoT Cloud.<br>
     *                        Refer to the <@link http://docs.kii.com/rest/#thing_management-register_a_thing>register_a_thing</@link><br>
     *                        About the format of this Document.
     * @return Target instance can be used to operate target, manage resources
     * of the target.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public Target onBoard(
            @NonNull String vendorThingID,
            @NonNull String thingPassword,
            @Nullable String thingType,
            @Nullable JSONObject thingProperties)
            throws IoTCloudException {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("vendorThingID", vendorThingID);
            requestBody.put("thingPassword", thingPassword);
            requestBody.put("thingType", thingType);
            if (thingProperties != null && thingProperties.length() > 0) {
                requestBody.put("thingProperties", thingProperties);
            }
            requestBody.put("owner", this.owner.getID().toString());
        } catch (JSONException e) {
            // Won’t happen
        }
        return this.onBoard(requestBody);
    }

    /** On board IoT Cloud with the specified thing ID.
     * When you are sure that the on boarding process has been done,
     * this method is more convenient than
     * <@link #onBoard(String, String, String, JSONObject)></@link>.
     * @param thingID Thing ID given by IoT Cloud. Must be specified.
     * @param thingPassword Thing password given by vendor. Must be specified.
     * @return Target instance can be used to operate target, manage resources
     * of the target.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public Target onBoard(
            @NonNull String thingID,
            @NonNull String thingPassword) throws
            IoTCloudException {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("thingID", thingID);
            requestBody.put("thingPassword", thingPassword);
        } catch (JSONException e) {
            // Won’t happen
        }
        return this.onBoard(requestBody);
    }

    private Target onBoard(JSONObject requestBody) throws IoTCloudException {
        String path = MessageFormat.format("/iot-api/apps/{0}/onboardings", this.appID);
        String url = Path.combine(site.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MEDIA_TYPE_JSON, requestBody);
        JSONObject response = this.restClient.sendRequest(request);
        String thingID = response.optString("thingID");
        String accessToken = response.optString("accessToken");
        this.onBoarded = true;
        return new Target(new TypedID(TypedID.Types.THING, thingID), accessToken);
    }

    /** Checks whether on boarding is done.
     * @return true if done, otherwise false.
     */
    public boolean onBoarded()
    {
        return this.onBoarded;
    }

    /** Install push notification to receive notification from IoT Cloud.
     * IoT Cloud will send notification when the Target replies to the Command.
     * Application can receive the notification and check the result of Command
     * fired by Application or registered Trigger.
     * After installation is done Installation ID is managed in this class.
     * @param deviceToken for GCM, specify token obtained by
     *                    InstanceID.getToken().
     *                    for JPUSH, specify id obtained by
     *                    JPushInterface.getUdid().
     * @param pushBackend Specify backend to use.
     * @return Installation ID used in IoT Cloud.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public String installPush(
            @Nullable String deviceToken,
            @NonNull PushBackend pushBackend
    ) throws IoTCloudException {
        String path = MessageFormat.format("/api/apps/{0}/installations", this.appID);
        String url = Path.combine(site.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("installationRegistrationID", deviceToken);
            requestBody.put("deviceType", pushBackend.getDeviceType());
        } catch (JSONException e) {
            // Won’t happen
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MEDIA_TYPE_INSTALLATION_CREATION_REQUEST, requestBody);
        JSONObject response = this.restClient.sendRequest(request);
        this.installationID = response.optString("InstallationID");
        return this.installationID;
    }

    /** Get installationID if the push is already installed.
     * null will be returned if the push installation has not been done.
     * @return Installation ID used in IoT Cloud.
     */
    @Nullable
    public String getInstallationID() {
        return this.installationID;
    }

    /** Uninstall push notification.
     * After done, notification from IoT Cloud won't be notified.
     * @param installationID installation ID returned from
     *                       <@link #installPush(String, PushBackend)></@link>
     *                       if null is specified, value obtained by
     *                       <@link #getInstallationID()></@link> is used.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public void uninstallPush(@Nullable String installationID) throws IoTCloudException {
        String path = MessageFormat.format("/api/apps/{0}/installations/{1}", this.appID, installationID);
        String url = Path.combine(site.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.DELETE, headers);
        this.restClient.sendRequest(request);
    }

    /** Post new command to IoT Cloud.
     * Command will be delivered to specified target and result will be notified
     * through push notification.
     * @param target Target of the command to be delivered.
     * @param schemaName name of the schema.
     * @param schemaVersion version of schema.
     * @param actions Actions to be executed.
     * @return Created Command instance. At this time, Command is delivered to
     * the target Asynchronously and may not finished. Actual Result will be
     * delivered through push notification or you can check the latest status
     * of the command by calling <@link #getCommand></@link>.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public Command postNewCommand(
            @NonNull Target target,
            @NonNull String schemaName,
            int schemaVersion,
            @NonNull List<Action> actions) throws IoTCloudException {
        if (target == null) {
            throw new IllegalArgumentException("target is null");
        }
        Schema schema = this.getSchema(schemaName, schemaVersion);
        if (schema == null) {
            throw new UnsupportedSchemaException(schemaName, schemaVersion);
        }
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }

        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/commands", this.appID, target.toString());
        String url = Path.combine(site.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        Command command = new Command(schemaName, schemaVersion, target.getTypedID(), this.owner.getID());
        JSONObject requestBody = JsonUtils.newJson(GsonRepository.gson(schema).toJson(command));
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MEDIA_TYPE_JSON, requestBody);
        JSONObject responseBody = this.restClient.sendRequest(request);

        String commandID = responseBody.optString("commandID");
        return this.getCommand(target, commandID);
    }

    /** Get specified command.
     * @param target Target of the command.
     * @param commandID ID of the command to obtain. ID is present in the
     *                  instance returned by <@link #postNewCommand></@link>
     *                  and can be obtained by <@link Command#getCommandID></@link>
     *
     * @return Command instance.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public Command getCommand(
            @NonNull Target target,
            @NonNull String commandID)
            throws IoTCloudException {

        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/commands/{2}", this.appID, target.toString(), commandID);
        String url = Path.combine(site.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);

        String schemaName = responseBody.optString("schema");
        int schemaVersion = responseBody.optInt("schemaVersion");
        Schema schema = this.getSchema(schemaName, schemaVersion);
        if (schema == null) {
            throw new UnsupportedSchemaException(schemaName, schemaVersion);
        }
        return GsonRepository.gson(schema).fromJson(responseBody.toString(), Command.class);
    }

    /** List Commands in the specified Target.
     * @param target Target to which the Commands belongs.
     * @param bestEffortLimit Maximum number of the Commands in the response.
     *                        if the value is <= 0, default limit internally
     *                        defined is applied.
     *                        Meaning of 'bestEffort' is if the specified limit
     *                        is greater than default limit, default limit is
     *                        applied.
     * @param paginationKey Used to get the next page of previously obtained.
     *                      If there is further page to obtain, this method
     *                      returns paginationKey as the 2nd element of pair.
     *                      Applying this key to the argument results continue
     *                      to get the result from the next page.
     * @return 1st Element is Commands belongs to the Target. 2nd element is
     * paginationKey if there is next page to be obtained.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    public Pair<List<Command>, String> listCommands (
            @NonNull Target target,
            int bestEffortLimit,
            @Nullable String paginationKey)
            throws IoTCloudException {
        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/commands", this.appID, target.toString());
        StringBuilder queryString = new StringBuilder();
        if (!TextUtils.isEmpty(paginationKey)) {
            this.addQueryString(queryString, "paginationKey", paginationKey);
        }
        if (bestEffortLimit > 0) {
            this.addQueryString(queryString, "bestEffortLimit", bestEffortLimit);
        }
        path += queryString.toString();
        String url = Path.combine(site.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject response = this.restClient.sendRequest(request);

        String nextPaginationKey = response.optString("nextPaginationKey");
        JSONArray commandArray = response.optJSONArray("commands");
        List<Command> commands = new ArrayList<Command>();
        if (commandArray != null) {
            for (int i = 0; i < commandArray.length(); i++) {
                JSONObject commandJson = commandArray.optJSONObject(i);
                String schemaName = commandJson.optString("schema");
                int schemaVersion = commandJson.optInt("schemaVersion");
                Schema schema = this.getSchema(schemaName, schemaVersion);
                if (schema == null) {
                    throw new UnsupportedSchemaException(schemaName, schemaVersion);
                }
                commands.add(GsonRepository.gson(schema).fromJson(commandJson.toString(), Command.class));
            }
        }
        return new Pair<List<Command>, String>(commands, nextPaginationKey);
    }

    /** Post new Trigger to IoT Cloud.
     * @param target Target of which the trigger stored. It the trigger is based
     *               on state of target, Trigger is evaluated when the state of
     *               the target has been updated.
     * @param schemaName name of the schema.
     * @param schemaVersion version of schema.
     * @param actions Specify actions included in the Command is fired by the
     *                trigger.
     * @param predicate Specify when the Trigger fires command.
     * @return Instance of the Trigger registered in IoT Cloud.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public Trigger postNewTrigger(
            @NonNull Target target,
            @NonNull String schemaName,
            int schemaVersion,
            @NonNull List<Action> actions,
            @NonNull Predicate predicate)
            throws IoTCloudException {

        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/triggers", this.appID, target.toString());
        String url = Path.combine(site.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        JSONObject requestBody = new JSONObject();
        Schema schema = this.getSchema(schemaName, schemaVersion);
        try {
            requestBody.put("predicate", JsonUtils.newJson(GsonRepository.gson(schema).toJson(predicate)));
        } catch (JSONException e) {
            // Won’t happen
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MEDIA_TYPE_JSON, requestBody);
        JSONObject response = this.restClient.sendRequest(request);
        String triggerID = response.optString("triggerID");
        return this.getTrigger(target, triggerID);
    }

    /** Get specified Trigger.
     * @param target Target of which the trigger stored.
     * @param triggerID ID of the Trigger to get.
     * @return Trigger instance.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public Trigger getTrigger(
            @NonNull Target target,
            @NonNull String triggerID)
            throws IoTCloudException {
        // TODO: implement it.
        return null;
    }

    /** Apply Patch to registered Trigger
     * Modify registered Trigger with specified patch.
     * @param target Target of which the Trigger stored
     * @param triggerID ID ot the Trigger to apply patch
     * @param schemaName name of the schema.
     * @param schemaVersion version of schema.
     * @param actions Modified actions.
     *                If null NonNull predicate must be specified.
     * @param predicate Modified predicate.
     *                  If null NonNull actions must be specified.
     * @return Updated Trigger instance.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     * @throws IllegalArgumentException when both actions and predicates are null
     */
    @NonNull
    @WorkerThread
    public Trigger patchTrigger(
            @NonNull Target target,
            @NonNull String triggerID,
            @NonNull String schemaName,
            int schemaVersion,
            @Nullable List<Action> actions,
            @Nullable Predicate predicate) throws
            IoTCloudException {
        // TODO: implement it.
        return null;
    }

    /** Enable/Disable registered Trigger
     * If its already enabled(/disabled),
     * this method won't throw Exception and behave as succeeded.
     * @param target Target of which the Trigger stored.
     * @param triggerID ID of the Trigger to be enabled(/disabled).
     * @param enable specify whether enable of disable the Trigger.
     * @return Updated Trigger Instance.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public Trigger enableTrigger(
            @NonNull Target target,
            @NonNull String triggerID,
            boolean enable)
            throws IoTCloudException {
        // TODO: implement it.
        return null;
    }

    /** Delete the specified Trigger.
     * @param target Target of which the Trigger stored.
     * @param triggerID ID of the Trigger to be deleted.
     * @return Deleted Trigger Instance.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public Trigger deleteTrigger(Target target, String triggerID) throws
            IoTCloudException {
        // TODO: implement it.
        return null;
    }

    /** List Triggers belongs to the specified Target.
     * @param target Target of which the Trigger stored.
     * @param paginationKey If specified obtain rest of the items.
     * @param bestEffortLimit limit the maximum number of the Triggers in the
     *                        Response. It ensures numbers in
     *                        response is equals to or less than specified number.
     *                        But doesn't ensures number of the Triggers
     *                        in the response is equal to specified value.<br>
     *                        If the specified value <= 0, Default size of the limit
     *                        is applied by IoT Cloud.
     * @return first is list of the Triggers and second is paginationKey returned
     * by IoT Cloud. paginationKey is null when there is next page to be obtained.
     * Obtained paginationKey can be used to get the rest of the items stored
     * in the target.
     * @throws IoTCloudException Thrown when failed to connect IoT Cloud Server
     * or IoT Cloud returns error response.
     */
    @NonNull
    @WorkerThread
    public Pair<List<Trigger>, String> listTriggers(
            @NonNull Target target,
            @NonNull String paginationKey,
            int bestEffortLimit) throws
            IoTCloudException {
        // TODO: implement it.
        return null;
    }

    /** Get the State of specified Target.
     * State will be serialized with Gson library.
     * @param classOfS Specify class of the State.
     * @param <S> State class.
     * @return Instance of Target State.
     */
    @NonNull
    @WorkerThread
    public <S extends TargetState> S getTargetState(
            @NonNull Target target,
            @NonNull Class<S> classOfS) {
        // TODO: implement it.
        Gson gson = GsonRepository.gson(null);
        S ret = gson.fromJson("{\"power\" : true}", classOfS);
        return ret;
    }
    private Schema getSchema(String schemaName, int schemaVersion) {
        return this.schemas.get(new Pair<String, Integer>(schemaName, schemaVersion));
    }
    private Action generateAction(String schemaName, int schemaVersion, String actionName, JSONObject actionParameters) throws IoTCloudException {
        Schema schema = this.getSchema(schemaName, schemaVersion);
        if (schema == null) {
            throw new UnsupportedSchemaException(schemaName, schemaVersion);
        }
        Class<? extends Action> actionClass = schema.getActionClass(actionName);
        if (actionClass == null) {
            throw new UnsupportedActionException(schemaName, schemaVersion, actionName);
        }
        Gson gson = GsonRepository.gson(schema);
        String json = actionParameters == null ? "{}" : actionParameters.toString();
        return gson.fromJson(json, actionClass);
    }
    private ActionResult generateActionResult(String schemaName, int schemaVersion, String actionName, JSONObject actionResult) throws IoTCloudException {
        Schema schema = this.getSchema(schemaName, schemaVersion);
        if (schema == null) {
            throw new UnsupportedSchemaException(schemaName, schemaVersion);
        }
        Class<? extends ActionResult> actionResultClass = schema.getActionResultClass(actionName);
        if (actionResultClass == null) {
            throw new UnsupportedActionException(schemaName, schemaVersion, actionName);
        }
        Gson gson = GsonRepository.gson(schema);
        String json = actionResult == null ? "{}" : actionResult.toString();
        return gson.fromJson(json, actionResultClass);
    }
    private Map<String, String> newHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        if (!TextUtils.isEmpty(this.appID)) {
            headers.put("X-Kii-AppID", this.appID);
        }
        if (!TextUtils.isEmpty(this.appKey)) {
            headers.put("X-Kii-AppKey", this.appKey);
        }
        if (this.owner != null && !TextUtils.isEmpty(this.owner.getAccessToken())) {
            headers.put("Authorization", "Bearer " + this.owner.getAccessToken());
        }
        return headers;
    }
    private void addQueryString(StringBuilder queryString, String key, Object value) {
        if (queryString.length() == 0) {
            queryString.append("?");
        } else {
            queryString.append("&");
        }
        queryString.append(key + "=" + value.toString());
    }

    // Implementation of Parcelable
    protected IoTCloudAPI(Parcel in) {
        this.appID = in.readString();
        this.appKey = in.readString();
        this.site = (Site)in.readSerializable();
        this.owner = in.readParcelable(Owner.class.getClassLoader());
        ArrayList<Schema> schemas = in.createTypedArrayList(Schema.CREATOR);
        for (Schema schema : schemas) {
            this.schemas.put(new Pair<String, Integer>(schema.getSchemaName(), schema.getSchemaVersion()), schema);
        }
        this.restClient = new IoTRestClient();
        this.onBoarded = (in.readByte() != 0);
        this.installationID = in.readString();
    }
    public static final Creator<IoTCloudAPI> CREATOR = new Creator<IoTCloudAPI>() {
        @Override
        public IoTCloudAPI createFromParcel(Parcel in) {
            return new IoTCloudAPI(in);
        }

        @Override
        public IoTCloudAPI[] newArray(int size) {
            return new IoTCloudAPI[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appID);
        dest.writeString(this.appKey);
        dest.writeSerializable(this.site);
        dest.writeParcelable(this.owner, flags);
        dest.writeTypedList(new ArrayList<Schema>(this.schemas.values()));
        dest.writeByte((byte) ((Boolean) this.onBoarded ? 1 : 0));
        dest.writeString(this.installationID);
    }

}
